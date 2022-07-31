package com.luncert.robotcontraption.util;

import com.mojang.math.Vector3d;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class Common {

    private Common() {
    }

    public static Vector3d relative(Vector3d v, Direction.Axis axis, double delta) {
        if (delta != 0) {
            switch (axis) {
                case X -> v.add(new Vector3d(delta, 0, 0));
                case Y -> v.add(new Vector3d(0, delta, 0));
                case Z -> v.add(new Vector3d(0, 0, delta));
            }
        }

        return v;
    }

    public static Vector3d set(Vec3 v, Direction.Axis axis, double value) {
        return switch (axis) {
            case X -> new Vector3d(value, v.y, v.z);
            case Y -> new Vector3d(v.x, value, v.z);
            case Z -> new Vector3d(v.x, v.y, value);
        };
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
