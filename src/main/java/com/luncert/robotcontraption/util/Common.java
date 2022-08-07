package com.luncert.robotcontraption.util;

import com.mojang.math.Vector3d;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class Common {

    private Common() {
    }

    public static Vec3 relative(Vec3 v, Direction.Axis axis, double delta) {
        if (delta != 0) {
            return switch (axis) {
                case X -> new Vec3(v.x + delta, v.y, v.z);
                case Y -> new Vec3(v.x, v.y + delta, v.z);
                case Z -> new Vec3(v.x, v.y, v.z + delta);
            };
        }

        return v;
    }

    public static Vec3 set(Vec3 v, Direction.Axis axis, double value) {
        return switch (axis) {
            case X -> new Vec3(value, v.y, v.z);
            case Y -> new Vec3(v.x, value, v.z);
            case Z -> new Vec3(v.x, v.y, value);
        };
    }

    public static Vec3 linear(Direction.Axis axis, double value) {
        return switch (axis) {
            case X -> new Vec3(value, 0, 0);
            case Y -> new Vec3(0, value, 0);
            case Z -> new Vec3(0, 0, value);
        };
    }

    public static BlockPos set(BlockPos v, Direction.Axis axis, double value) {
        return switch (axis) {
            case X -> new BlockPos(value, v.getY(), v.getZ());
            case Y -> new BlockPos(v.getX(), value, v.getZ());
            case Z -> new BlockPos(v.getX(), v.getY(), value);
        };
    }

    public static Vec3 convert(BlockPos blockPos) {
        return new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static boolean compareFluidKinds(Fluid a, Fluid b) {
        if (a == b) {
            return true;
        }
        if (a == Fluids.WATER) {
            return b == Fluids.FLOWING_WATER;
        }
        if (a == Fluids.LAVA) {
            return b == Fluids.FLOWING_LAVA;
        }
        if (a instanceof ForgeFlowingFluid fa) {
            if (b instanceof ForgeFlowingFluid fb) {
                return fa.getSource() == fb.getSource();
            }
        }
        return false;
    }
}
