package com.luncert.robotcontraption.content.depothopper;

import com.simibubi.create.content.logistics.block.depot.DepotTileEntity;
import com.simibubi.create.content.logistics.block.funnel.AbstractFunnelBlock;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.Optional;

public class DepotHopperBehaviour extends TileEntityBehaviour {

    private static final String NBT_PREV_ITEM = "PrevItem";

    private static final BehaviourType<DepotHopperBehaviour> TYPE = new BehaviourType<>();

    private static final AABB coreBB =
            new AABB(VecHelper.CENTER_OF_ORIGIN, VecHelper.CENTER_OF_ORIGIN).inflate(.5f);

    private ItemStack prevItem;

    public DepotHopperBehaviour(SmartTileEntity te) {
        super(te);
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    @Override
    public void tick() {
        super.tick();

        Level world = getWorld();
        if (world.isClientSide) {
            return;
        }

        BlockPos pos = getPos();
        BlockState state = world.getBlockState(pos);
        boolean canDropItem = world.getEntitiesOfClass(ItemEntity.class, getEntityOverflowScanningArea(pos)).isEmpty();

        Optional<Direction> optionalValue = state.getOptionalValue(BlockStateProperties.FACING);
        if (optionalValue.isPresent()) {
            Direction facing = optionalValue.get();

            BlockEntity depotEntity = world.getBlockEntity(pos.relative(facing.getOpposite()));
            if (depotEntity instanceof DepotTileEntity) {
                Optional<IItemHandler> capability = depotEntity
                        .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing).resolve();
                if (capability.isPresent()) {
                    IItemHandler inventory = capability.get();

                    // get processed item
                    ItemStack processedItem = null;
                    if (canDropItem) {
                        processedItem = extractOutputBuffer(inventory);
                    }
                    if (processedItem == null) {
                        // call extractHeldItem to update prevItem
                        processedItem = extractHeldItem(inventory, canDropItem);
                    }

                    // drop processed item
                    if (processedItem != null) {
                        dropItemStack(world,
                                pos.getX() + .5f, pos.getY() + .5f, pos.getZ() + .5f,
                                processedItem);
                    }
                }

                return;
            }
        }

        world.removeBlock(pos, false);
    }

    private AABB getEntityOverflowScanningArea(BlockPos pos) {
        return coreBB.move(pos).expandTowards(0, -1, 0);
    }

    private ItemStack extractOutputBuffer(IItemHandler inventory) {
        for (int slot = 1; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                return inventory.extractItem(slot, stack.getCount(), false);
            }
        }

        return null;
    }

    private ItemStack extractHeldItem(IItemHandler inventory, boolean canDropItem) {
        // search help item
        ItemStack heldItem = inventory.getStackInSlot(0);

        if (heldItem.isEmpty()) {
            prevItem = null;
        } else {
            boolean depotUpdated = prevItem != null && !compareItems(prevItem, heldItem, false);
            if (depotUpdated) {
                boolean isCraftUpdate = getWorld().getRecipeManager().getRecipes()
                        .stream()
                        .anyMatch(recipe -> {
                            // check whether we can find a recipe with prevItem as ingredient and heldItem as result item
                            for (Ingredient ingredient : recipe.getIngredients()) {
                                if (ingredient.test(prevItem)) {
                                    return compareItems(recipe.getResultItem(), heldItem, true);
                                }
                            }

                            return false;
                        });

                if (isCraftUpdate) {
                    if (canDropItem) {
                        prevItem = null;
                        return inventory.extractItem(0, heldItem.getCount(), false);
                    } else {
                        // not change prev item
                        return null;
                    }
                }
            }

            // depot not updated or not a craft update
            prevItem = heldItem;
        }

        return null;
    }

    private void dropItemStack(Level world, double x, double y, double z, ItemStack item) {
        ItemEntity itementity = new ItemEntity(world, x, y - 0.5D, z, item, 0, 0, 0);
        world.addFreshEntity(itementity);
    }

    private boolean compareItems(ItemStack a, ItemStack b, boolean ignoreCount) {
        if (a.isEmpty())
            return b.isEmpty();
        else
            return !b.isEmpty() && a.getItem() == b.getItem() && (ignoreCount || a.getCount() == b.getCount());
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        if (prevItem != null) {
            compound.put(NBT_PREV_ITEM, prevItem.serializeNBT());
        }
    }

    @Override
    public void read(CompoundTag compound, boolean clientPacket) {
        prevItem = null;
        if (compound.contains(NBT_PREV_ITEM)) {
            prevItem = ItemStack.of(compound.getCompound(NBT_PREV_ITEM));
        }
    }
}
