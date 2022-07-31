package com.luncert.robotcontraption.content.fuelengine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.tileEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class FuelEngineBoxTransform extends ValueBoxTransform.Sided {

    @Override
    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace(8.0, 8.0, 14.1);
    }

    @Override
    protected boolean isSideActive(BlockState state, Direction direction) {
        return Direction.UP.equals(direction);
    }
}
