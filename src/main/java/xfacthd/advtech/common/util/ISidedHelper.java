package xfacthd.advtech.common.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.ChunkPos;

import java.util.List;

public interface ISidedHelper
{
    default PlayerEntity getPlayer() { throw new UnsupportedOperationException("Can't getPlayer() on the server!"); }
}