package xfacthd.advtech.common.util;

import net.minecraft.entity.player.PlayerEntity;

public class ServerHelper implements ISidedHelper
{
    @Override
    public PlayerEntity getPlayer() { throw new UnsupportedOperationException("Can't getPlayer() on the server!"); }
}