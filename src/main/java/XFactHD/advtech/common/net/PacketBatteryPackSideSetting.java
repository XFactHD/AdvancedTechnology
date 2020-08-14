/*  Copyright (C) <2016>  <XFactHD>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses. */

package XFactHD.advtech.common.net;

import XFactHD.advtech.common.blocks.energy.TileEntityBatteryPack;
import XFactHD.advtech.common.utils.utilClasses.SideSetting;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketBatteryPackSideSetting implements IMessage
{
    private BlockPos pos;
    private EnumFacing side;
    private SideSetting setting;

    public PacketBatteryPackSideSetting(){}

    public PacketBatteryPackSideSetting(BlockPos pos, EnumFacing side, SideSetting setting)
    {
        this.pos = pos;
        this.side = side;
        this.setting = setting;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(pos.toLong());
        buf.writeInt(side.getIndex());
        buf.writeInt(setting.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = BlockPos.fromLong(buf.readLong());
        side = EnumFacing.getFront(buf.readInt());
        setting = SideSetting.values()[buf.readInt()];
    }

    public static class Handler implements IMessageHandler<PacketBatteryPackSideSetting, IMessage>
    {
        @Override
        public IMessage onMessage(PacketBatteryPackSideSetting message, MessageContext ctx)
        {
            ((WorldServer)ctx.getServerHandler().playerEntity.worldObj).addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    TileEntity te = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.pos);
                    if (te instanceof TileEntityBatteryPack)
                    {
                        ((TileEntityBatteryPack)te).setSetting(message.side, message.setting);
                    }
                }
            });
            return null;
        }
    }
}