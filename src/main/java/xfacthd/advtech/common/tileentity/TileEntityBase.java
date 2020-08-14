package xfacthd.advtech.common.tileentity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.Constants;

public abstract class TileEntityBase extends TileEntity
{
    public TileEntityBase(TileEntityType<?> type) { super(type); }

    public final void markFullUpdate()
    {
        markDirty();
        //noinspection ConstantConditions
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
    }

    public abstract void writeNetworkNBT(CompoundNBT nbt);

    public abstract void readNetworkNBT(CompoundNBT nbt);

    @Override
    public final CompoundNBT getUpdateTag()
    {
        CompoundNBT nbt = new CompoundNBT();
        writeNetworkNBT(nbt);
        super.write(nbt);
        return nbt;
    }

    @Override
    public final void handleUpdateTag(CompoundNBT nbt) { readNetworkNBT(nbt); }

    @Override
    public final SUpdateTileEntityPacket getUpdatePacket()
    {
        CompoundNBT nbt = new CompoundNBT();
        writeNetworkNBT(nbt);
        return new SUpdateTileEntityPacket(getPos(), -1, nbt);
    }

    @Override
    public final void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        CompoundNBT nbt = pkt.getNbtCompound();
        readNetworkNBT(nbt);
    }
}