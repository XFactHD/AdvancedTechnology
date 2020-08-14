package xfacthd.advtech.common.tileentity.debug;

import net.minecraft.nbt.CompoundNBT;
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

    @Override
    public void writeNetworkNBT(CompoundNBT nbt) { }

    @Override
    public void readNetworkNBT(CompoundNBT nbt) { }

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