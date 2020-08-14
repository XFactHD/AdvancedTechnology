package xfacthd.advtech.common.capability.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class InfiniteItemHandler extends ItemStackHandler
{
    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { return stack; }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) { return stacks.get(slot); }
}