package xfacthd.advtech.common.util.interfaces;

import net.minecraft.core.Direction;

public interface ITileFluidHandler
{
    boolean canFill(Direction side);

    boolean canDrain(Direction side);
}