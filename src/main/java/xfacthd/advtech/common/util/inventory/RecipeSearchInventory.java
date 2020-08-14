package xfacthd.advtech.common.util.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class RecipeSearchInventory implements IInventory
{
    private final ItemStack[] stacks;

    public RecipeSearchInventory(ItemStack... stacks) { this.stacks = stacks; }

    @Override
    public int getSizeInventory() { return stacks.length; }

    @Override
    public boolean isEmpty()
    {
        for (ItemStack stack : stacks)
        {
            if (!stack.isEmpty()) { return false; }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) { return stacks[index]; }

    @Override
    public ItemStack decrStackSize(int index, int count) { return ItemStack.EMPTY; }

    @Override
    public ItemStack removeStackFromSlot(int index) { return ItemStack.EMPTY; }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) { }

    @Override
    public void markDirty() { }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) { return false; }

    @Override
    public void clear() { }
}