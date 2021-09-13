package xfacthd.advtech.common.blockentity.debug;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.BlockEntityBase;
import xfacthd.advtech.common.capability.item.InfiniteItemHandler;

public class BlockEntityCreativeItemSource extends BlockEntityBase
{
    private final InfiniteItemHandler itemHandler = new InfiniteItemHandler();
    private LazyOptional<IItemHandler> lazyItemHandler;

    public BlockEntityCreativeItemSource(BlockPos pos, BlockState state)
    {
        super(ATContent.BE_TYPE_CREATIVE_ITEM_SOURCE.get(), pos, state);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
    {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void clearRemoved() //TODO: replace with onLoad() when that PR is merged
    {
        super.clearRemoved();

        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void setRemoved()
    {
        super.setRemoved();

        lazyItemHandler.invalidate();
        lazyItemHandler = LazyOptional.empty();
    }

    public ItemStack getItem() { return itemHandler.getStackInSlot(0); }

    public void setItem(ItemStack stack)
    {
        itemHandler.setStack(stack);
        sendSyncPacket();
    }

    @Override
    public void writeNetworkNBT(CompoundTag nbt) { nbt.put("inv", itemHandler.serializeNBT()); }

    @Override
    public void readNetworkNBT(CompoundTag nbt) { itemHandler.deserializeNBT(nbt.getCompound("inv")); }

    @Override
    public void writeSyncPacket(FriendlyByteBuf buffer) { buffer.writeNbt(itemHandler.serializeNBT()); }

    @Override
    protected void readSyncPacket(FriendlyByteBuf buffer) { itemHandler.deserializeNBT(buffer.readNbt()); }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        nbt.put("inv", itemHandler.serializeNBT());
        return super.save(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inv"));
    }
}