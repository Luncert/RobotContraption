package com.luncert.robotcontraption.content.common;

import net.minecraft.core.Direction.Axis;

public enum SimpleDirection {

    SOUTH(false, true),
    WEST(true, false),
    NORTH(true, true),
    EAST(false, false);

    private final boolean positive;
    private final boolean inZAxis;

    SimpleDirection(boolean positive, boolean inZAxis) {
        this.positive = positive;
        this.inZAxis = inZAxis;
    }

    public boolean inZAxis() {
        return inZAxis;
    }

    public boolean inXAxis() {
        return !inZAxis;
    }

    public boolean isSouth() {
        return equals(SOUTH);
    }

    public boolean isWest() {
        return equals(WEST);
    }

    public boolean isNorth() {
        return equals(NORTH);
    }

    public boolean isEast() {
        return equals(EAST);
    }

    public Axis getAxis() {
        return inZAxis ? Axis.Z : Axis.X;
    }

    public boolean isPositive() {
        return positive;
    }

    public int getDirectionFactor() {
        return positive ? 1 : -1;
    }
}
