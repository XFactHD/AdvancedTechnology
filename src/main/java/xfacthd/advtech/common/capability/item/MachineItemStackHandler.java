package xfacthd.advtech.common.capability.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import xfacthd.advtech.common.blockentity.BlockEntityInventoryMachine;

public class MachineItemStackHandler extends ItemStackHandler
{
    private final BlockEntityInventoryMachine te;

    public MachineItemStackHandler(BlockEntityInventoryMachine te, int size)
    {
        super(size);
        this.te = te;
    }

    @Override
    public void setSize(int size) { }

    @Override
    protected void onContentsChanged(int slot) { te.onSlotChanged(slot); }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) { return te.isItemValid(slot, stack); }
}