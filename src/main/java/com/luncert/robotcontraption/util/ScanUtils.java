package com.luncert.robotcontraption.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;

public class ScanUtils {

    public static Pair<Vec3, Vec3> calcShapeForAdjacentBlocks(Level world, BlockPos base) {
        // TODO compare tag not block
        Block targetBlock = world.getBlockState(base).getBlock();

        int ax = base.getX(), ay = base.getY(), az = base.getZ(),
            bx = ax, by = ay, bz = az;

        Set<BlockPos> visited = new HashSet<>();

        Stack<BlockPos> stack = new Stack<>();
        listAllRelativePos(stack, base);
        visited.add(base);
        while (!stack.isEmpty()) {
            BlockPos pos = stack.pop();
            BlockState blockState = world.getBlockState(pos);
            if (!visited.contains(pos) && blockState.is(targetBlock)) {
                listAllRelativePos(stack, pos);
                visited.add(pos);

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

    private static void listAllRelativePos(Collection<BlockPos> collection, BlockPos pos) {
        collection.add(pos.above());
        pos = pos.below();
        collection.add(pos);

        final BlockPos[] template = new BlockPos[8];
        template[0] = pos.west();
        template[1] = pos.east();
        template[2] = pos.south();
        template[3] = pos.north();
        template[4] = template[2].west();
        template[5] = template[2].east();
        template[6] = template[3].west();
        template[7] = template[3].east();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                BlockPos blockPos = template[j];
                collection.add(blockPos);
                template[j] = blockPos.above();
            }
        }
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
