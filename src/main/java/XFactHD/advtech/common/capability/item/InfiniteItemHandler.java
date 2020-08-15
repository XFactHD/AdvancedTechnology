package xfacthd.advtech.common.capability.item;

import com.google.common.base.Preconditions;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class InfiniteItemHandler implements IItemHandler, INBTSerializable<CompoundNBT>
{
    private ItemStack stack = ItemStack.EMPTY;

    @Override
    public int getSlots() { return 1; }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        Preconditions.checkArgument(slot == 0, "Invalid slot index!");
        return stack;
    }

    public void setStack(ItemStack stack)
    {
        this.stack = stack.copy();
        this.stack.setCount(this.stack.getMaxStackSize());
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { return stack; }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) { return stack.copy(); }

    @Override
    public int getSlotLimit(int slot) { return 64; }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) { return false; }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        stack.write(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) { stack = ItemStack.read(nbt); }
}