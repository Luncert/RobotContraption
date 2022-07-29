package com.luncert.robotcontraption.content.depothopper;

import com.luncert.robotcontraption.index.RCBlocks;
import com.simibubi.create.AllBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DepotHopperItem extends BlockItem {

    public DepotHopperItem(Block block, Properties props) {
        super(block, props);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        BlockState state = world.getBlockState(pos);
        Player player = context.getPlayer();
        Direction facing = context.getClickedFace();

        if (player == null || !AllBlocks.DEPOT.has(state) || facing.getAxis().equals(Direction.Axis.Y)) {
            return InteractionResult.FAIL;
        }

        BlockState newState = RCBlocks.DEPOT_HOPPER.getDefaultState()
                .setValue(BlockStateProperties.FACING, facing);
        InteractionResult resultType = super.useOn(context);
        if (resultType.consumesAction()) {
            pos = context.getClickedPos().relative(facing);
            world.setBlockAndUpdate(pos, newState);
            if (!player.isCreative()) {
                context.getItemInHand().shrink(1);
            }
        }

        return resultType;
    }
}
