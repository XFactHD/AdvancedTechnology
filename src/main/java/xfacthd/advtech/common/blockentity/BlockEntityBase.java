package xfacthd.advtech.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.net.packets.blockentity.PacketTileSync;

public abstract class BlockEntityBase extends BlockEntity
{
    private boolean needsSync = false;
    private boolean needRerender = false;

    public BlockEntityBase(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

    public Level level()
    {
        if (level == null) { throw new IllegalStateException("BlockEntity#level accessed before set!"); }
        return level;
    }

    public final void markFullUpdate()
    {
        setChanged();
        //noinspection ConstantConditions
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public final void markForSync()
    {
        setChanged();
        needsSync = true;
    }

    public boolean needsSync() { return needsSync; }

    public final void markRenderUpdate() { needRerender = true; }

    protected final void forceRenderUpdate()
    {
        //noinspection ConstantConditions
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
    }

    public abstract void writeNetworkNBT(CompoundTag nbt);

    public abstract void readNetworkNBT(CompoundTag nbt);

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag tag = new CompoundTag();
        writeNetworkNBT(tag);
        super.saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) { readNetworkNBT(tag); }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this, be ->
        {
            CompoundTag tag = new CompoundTag();
            ((BlockEntityBase) be).writeNetworkNBT(tag);
            return tag;
        });
    }

    public abstract void writeSyncPacket(FriendlyByteBuf buffer);

    protected abstract void readSyncPacket(FriendlyByteBuf buffer);

    protected final void sendSyncPacket()
    {
        needsSync = false;

        if (level != null)
        {
            NetworkHandler.sendToPlayersTrackingChunk(new PacketTileSync(this), level.getChunkAt(worldPosition));
        }
    }

    public final void handleSyncPacket(FriendlyByteBuf buffer)
    {
        readSyncPacket(buffer);

        if (needRerender)
        {
            needRerender = false;
            forceRenderUpdate();
        }
    }
}