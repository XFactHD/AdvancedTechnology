package xfacthd.advtech.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import xfacthd.advtech.common.util.ISidedHelper;

public class ClientHelper implements ISidedHelper
{
    @Override
    public PlayerEntity getPlayer() { return Minecraft.getInstance().player; }

    @Override
    public World getWorld() { return Minecraft.getInstance().world; }
}