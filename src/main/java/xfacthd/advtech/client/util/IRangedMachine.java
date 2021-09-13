package xfacthd.advtech.client.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface IRangedMachine
{
    boolean showArea();

    int getRadius();

    default int getHeight() { return 1; }

    default int getAreaOffsetY() { return 0; }

    default boolean isAreaCentered() { return false; }

    default Direction getAreaOffsetDir() { return Direction.NORTH; }

    default int getAreaTint() { return 0xFFFFFF; }



    default boolean showScanPos() { return false; }

    default BlockPos getScanPos() { return BlockPos.ZERO; }

    default int getScanPosTint() { return 0xFF0000; }
}