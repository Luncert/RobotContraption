package com.luncert.robotcontraption.content.fuelengine;

import com.luncert.robotcontraption.common.Capabilities;
import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollValueBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FuelEngineTileEntity extends GeneratingKineticTileEntity {

    protected ScrollValueBehaviour generatedSpeed;
    private final LazyOptional<FuelEngineComponent> component;

    public FuelEngineTileEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);

        component = LazyOptional.of(FuelEngineComponent::new);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (Capabilities.isAircraftComponent(cap)) {
            return component.cast();
        }
        return super.getCapability(cap, side);
    }
}
