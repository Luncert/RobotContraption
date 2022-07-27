package com.luncert.robotcontraption.content.common;

import net.minecraft.core.Direction.Axis;

public enum SimpleDirection {

    SOUTH(false, true, 180),
    WEST(true, false, 270),
    NORTH(true, true, 0),
    EAST(false, false, 90);

    private final boolean positive;
    private final boolean inZAxis;
    private final int degree;

    SimpleDirection(boolean positive, boolean inZAxis, int degree) {
        this.positive = positive;
        this.inZAxis = inZAxis;
        this.degree = degree;
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

    public int getDegree() {
        return degree;
    }
}
