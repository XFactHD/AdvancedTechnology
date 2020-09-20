package xfacthd.advtech.common.util;

import net.minecraft.entity.player.PlayerEntity;

public interface ISidedHelper
{
    default PlayerEntity getPlayer() { throw new UnsupportedOperationException("Can't getPlayer() on the server!"); }
}