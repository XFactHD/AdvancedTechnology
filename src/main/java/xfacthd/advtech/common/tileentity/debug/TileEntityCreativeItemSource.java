package xfacthd.advtech.common.tileentity.debug;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import xfacthd.advtech.common.capability.item.InfiniteItemHandler;
import xfacthd.advtech.common.data.types.TileEntityTypes;
import xfacthd.advtech.common.tileentity.TileEntityBase;

public class TileEntityCreativeItemSource extends TileEntityBase
{
    private final InfiniteItemHandler itemHandler = new InfiniteItemHandler();
    private LazyOptional<IItemHandler> lazyItemHandler;

    public TileEntityCreativeItemSource() { super(TileEntityTypes.tileTypeCreativeItemSource); }

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
    public void validate()
    {
        super.validate();

        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void remove()
    {
        super.remove();

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
    public void writeNetworkNBT(CompoundNBT nbt) { nbt.put("inv", itemHandler.serializeNBT()); }

    @Override
    public void readNetworkNBT(CompoundNBT nbt) { itemHandler.deserializeNBT(nbt.getCompound("inv")); }

    @Override
    public void writeSyncPacket(PacketBuffer buffer) { buffer.writeCompoundTag(itemHandler.serializeNBT()); }

    @Override
    protected void readSyncPacket(PacketBuffer buffer) { itemHandler.deserializeNBT(buffer.readCompoundTag()); }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.put("inv", itemHandler.serializeNBT());
        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inv"));
    }
}