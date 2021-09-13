package xfacthd.advtech.common.util.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record RecipeSearchInventory(ItemStack... stacks) implements Container
{
    @Override
    public int getContainerSize() { return stacks.length; }

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
    public ItemStack getItem(int index) { return stacks[index]; }

    @Override
    public ItemStack removeItem(int index, int count) { return ItemStack.EMPTY; }

    @Override
    public ItemStack removeItemNoUpdate(int index) { return ItemStack.EMPTY; }

    @Override
    public void setItem(int index, ItemStack stack) { }

    @Override
    public void setChanged() { }

    @Override
    public boolean stillValid(Player player) { return false; }

    @Override
    public void clearContent() { }
}