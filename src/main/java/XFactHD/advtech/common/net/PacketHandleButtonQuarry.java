/*  Copyright (C) <2017>  <XFactHD>

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

import XFactHD.advtech.common.blocks.machine.TileEntityQuarry;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketHandleButtonQuarry implements IMessage
{
    private BlockPos pos;
    private int buttonID;
    private Object[] value;

    @SuppressWarnings("unused")
    public PacketHandleButtonQuarry(){}

    public PacketHandleButtonQuarry(BlockPos pos, int buttonID, Object... value)
    {
        this.pos = pos;
        this.buttonID = buttonID;
        this.value = value;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(pos.toLong());
        buf.writeInt(buttonID);
        switch (buttonID)
        {
            case 4:
            {
                buf.writeInt((Integer) value[0]);
                buf.writeInt((Integer) value[1]);
                break;
            }
            case 5: break;
        }
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = BlockPos.fromLong(buf.readLong());
        buttonID = buf.readInt();
        switch (buttonID)
        {
            case 4:
            {
                value = new Object[2];
                value[0] = buf.readInt();
                value[1] = buf.readInt();
                break;
            }
            case 5: break;
        }
    }

    public static class Handler implements IMessageHandler<PacketHandleButtonQuarry, IMessage>
    {
        @Override
        public IMessage onMessage(PacketHandleButtonQuarry message, MessageContext ctx)
        {
            ((WorldServer)ctx.getServerHandler().playerEntity.worldObj).addScheduledTask(new Runnable()
            {
                private final PacketHandleButtonQuarry msg = message;

                @Override
                public void run()
                {
                    final World world = ctx.getServerHandler().playerEntity.worldObj;
                    TileEntity te = world.getTileEntity(msg.pos);
                    if (te instanceof TileEntityQuarry)
                    {
                        TileEntityQuarry quarry = (TileEntityQuarry)te;
                        switch (msg.buttonID)
                        {
                            case 4:
                            {
                                quarry.setRangeNorth((Integer)msg.value[0]);
                                quarry.setRangeEast((Integer)msg.value[1]);
                                quarry.setRunning(true);
                                break;
                            }
                            case 5:
                            {
                                quarry.setRunning(false);
                                break;
                            }
                        }
                    }
                }
            });
            return null;
        }
    }
}