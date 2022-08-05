package com.luncert.robotcontraption.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Stack;
import java.util.function.BiFunction;

public class ScanUtils {

    public static Pair<Vec3, Vec3> calcShapeForAdjacentBlocks(Level world, BlockPos base) {
        Block targetBlock = world.getBlockState(base).getBlock();

        int ax = base.getX(), ay = base.getY(), az = base.getZ(),
            bx = ax, by = ay, bz = az;

        Stack<BlockPos> stack = new Stack<>();
        stack.add(base.above());
        stack.add(base.below());
        stack.add(base.relative(Direction.NORTH));
        stack.add(base.relative(Direction.EAST));
        stack.add(base.relative(Direction.SOUTH));
        stack.add(base.relative(Direction.WEST));
        while (!stack.isEmpty()) {
            BlockPos pos = stack.pop();
            BlockState blockState = world.getBlockState(pos);
            if (blockState.is(targetBlock)) {
                stack.add(pos.above());
                stack.add(pos.below());
                stack.add(pos.relative(Direction.NORTH));
                stack.add(pos.relative(Direction.EAST));
                stack.add(pos.relative(Direction.SOUTH));
                stack.add(pos.relative(Direction.WEST));

                ax = Math.min(pos.getX(), ax);
                ay = Math.min(pos.getY(), ay);
                az = Math.min(pos.getZ(), az);
                bx = Math.max(pos.getX(), bx);
                by = Math.max(pos.getY(), by);
                bz = Math.max(pos.getZ(), bz);
            }
        }

        return Pair.of(new Vec3(ax, ay, az), new Vec3(bx, by, bz));
    }

    public static void relativeTraverseBlocks(Level world, BlockPos center, int radius, BiFunction<BlockState, BlockPos, Boolean> consumer) {
        traverseBlocks(world, center, radius, consumer, true);
    }

    public static void traverseBlocks(Level world, BlockPos center, int radius, BiFunction<BlockState, BlockPos, Boolean> consumer) {
        traverseBlocks(world, center, radius, consumer, false);
    }

    public static void traverseBlocks(Level world, BlockPos center, int radius, BiFunction<BlockState, BlockPos, Boolean> consumer, boolean relativePosition) {
        int x = center.getX();
        int y = center.getY();
        int z = center.getZ();
        for (int oX = x - radius; oX <= x + radius; oX++) {
            for (int oY = y - radius; oY <= y + radius; oY++) {
                for (int oZ = z - radius; oZ <= z + radius; oZ++) {
                    BlockPos subPos = new BlockPos(oX, oY, oZ);
                    BlockState blockState = world.getBlockState(subPos);
                    if (!blockState.isAir()) {
                        boolean continueTraverse;
                        if (relativePosition) {
                            continueTraverse = consumer.apply(blockState, new BlockPos(oX - x, oY - y, oZ - z));
                        } else {
                            continueTraverse = consumer.apply(blockState, subPos);
                        }
                        if (!continueTraverse) {
                            return;
                        }
                    }
                }
            }
        }
    }
}
