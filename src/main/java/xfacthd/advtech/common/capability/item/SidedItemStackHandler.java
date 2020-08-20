package xfacthd.advtech.common.capability.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.items.ItemStackHandler;
import xfacthd.advtech.common.tileentity.TileEntityInventoryMachine;

public class SidedItemStackHandler extends ItemStackHandler
{
    private final TileEntityInventoryMachine te;
    private final MachineItemStackHandler internalHandler;
    private final Direction side;

    public SidedItemStackHandler(TileEntityInventoryMachine te, MachineItemStackHandler internalHandler, Direction side, int size)
    {
        super(size);
        this.te = te;
        this.internalHandler = internalHandler;
        this.side = side;
    }

    @Override
    public void setSize(int size) { }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        if (!te.canInsert(side, slot)) { return stack; }
        return internalHandler.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (!te.canExtract(side, slot)) { return ItemStack.EMPTY; }
        return internalHandler.extractItem(slot, amount, simulate);
    }
}