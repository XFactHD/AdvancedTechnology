package xfacthd.advtech.common.tileentity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.Constants;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.net.packets.tileentity.PacketTileSync;

public abstract class TileEntityBase extends TileEntity
{
    private boolean needsSync = false;
    private boolean needRerender = false;

    public TileEntityBase(TileEntityType<?> type) { super(type); }

    public final void markFullUpdate()
    {
        markDirty();
        //noinspection ConstantConditions
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
    }

    protected final void markForSync()
    {
        markDirty();
        needsSync = true;
    }

    public boolean needsSync() { return needsSync; }

    protected final void markRenderUpdate() { needRerender = true; }

    protected final void forceRenderUpdate()
    {
        //noinspection ConstantConditions
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.RERENDER_MAIN_THREAD);
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

    public abstract void writeSyncPacket(PacketBuffer buffer);

    protected abstract void readSyncPacket(PacketBuffer buffer);

    protected final void sendSyncPacket()
    {
        needsSync = false;

        if (world != null)
        {
            NetworkHandler.sendToPlayersTrackingChunk(new PacketTileSync(this), world.getChunkAt(pos));
        }
    }

    public final void handleSyncPacket(PacketBuffer buffer)
    {
        readSyncPacket(buffer);

        if (needRerender)
        {
            needRerender = false;
            forceRenderUpdate();
        }
    }
}