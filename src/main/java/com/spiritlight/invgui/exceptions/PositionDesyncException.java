package com.spiritlight.invgui.exceptions;

import net.minecraft.util.math.BlockPos;

public class PositionDesyncException extends RuntimeException {
    public PositionDesyncException(String s) {
        super(s);
    }

    public PositionDesyncException(BlockPos dest) {
        this("Position test failed! Expected: " + dest.getX() + " " + dest.getY() + " " + dest.getZ());
    }
}
