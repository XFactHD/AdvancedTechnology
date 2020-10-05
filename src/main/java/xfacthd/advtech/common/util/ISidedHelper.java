package xfacthd.advtech.common.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface ISidedHelper
{
    default PlayerEntity getPlayer() { throw new UnsupportedOperationException("Can't getPlayer() on the server!"); }

    default World getWorld() { throw new UnsupportedOperationException("Can't getWorld() on the server!"); }
}