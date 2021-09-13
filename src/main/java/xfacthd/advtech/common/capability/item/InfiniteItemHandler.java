package xfacthd.advtech.common.capability.item;

import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;

public class InfiniteItemHandler implements IItemHandler, INBTSerializable<CompoundTag>
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
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        ItemStack result = stack.copy();
        result.setCount(amount);
        return result;
    }

    @Override
    public int getSlotLimit(int slot) { return 64; }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) { return false; }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag nbt = new CompoundTag();
        stack.save(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) { stack = ItemStack.of(nbt); }
}