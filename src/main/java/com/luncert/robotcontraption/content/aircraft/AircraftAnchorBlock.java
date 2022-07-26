package com.luncert.robotcontraption.content.aircraft;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;

public class AircraftAnchorBlock extends Block {

    public AircraftAnchorBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    @Nonnull
    public VoxelShape getShape(@Nonnull BlockState p_220053_1_, @Nonnull BlockGetter p_220053_2_,
                               @Nonnull BlockPos p_220053_3_, @Nonnull CollisionContext p_220053_4_) {
        return Shapes.empty();
    }
}
