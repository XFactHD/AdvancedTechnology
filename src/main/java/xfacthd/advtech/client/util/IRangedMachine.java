package xfacthd.advtech.client.util;

import net.minecraft.util.Direction;

public interface IRangedMachine
{
    int getRadius();

    default int getHeight() { return 1; }

    default boolean isAreaCentered() { return false; }

    boolean showArea();

    default int getAreaTint() { return 0xFFFFFF; }

    default Direction getAreaOffsetDir() { return Direction.NORTH; }

    default int getAreaOffsetY() { return 0; }
}