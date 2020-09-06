package xfacthd.advtech.common.capability.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import xfacthd.advtech.common.data.subtypes.Enhancements;
import xfacthd.advtech.common.item.tool.ItemEnhancement;
import xfacthd.advtech.common.tileentity.TileEntityMachine;

public class EnhancementItemStackHandler extends ItemStackHandler
{
    private final TileEntityMachine machine;

    public EnhancementItemStackHandler(TileEntityMachine machine)
    {
        super(4);
        this.machine = machine;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (stack.isEmpty()) { return true; }
        if (stack.getItem() instanceof ItemEnhancement)
        {
            Enhancements upgrade = ((ItemEnhancement)stack.getItem()).getType();
            return machine.canInstallEnhancement(upgrade);
        }
        return false;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        ItemStack result = super.insertItem(slot, stack, simulate);
        if (result.isEmpty()) //Empty means the item was inserted -> activate effects in machine
        {
            ItemEnhancement upgrade = (ItemEnhancement)stack.getItem();
            Enhancements type = upgrade.getType();
            int level = upgrade.getLevel();
            machine.installEnhancement(type, level);
        }
        return result;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        ItemStack result = super.extractItem(slot, amount, simulate);
        if (!result.isEmpty()) //Not empty means the item was removed -> deactivate effects on machine
        {
            ItemEnhancement upgrade = (ItemEnhancement)result.getItem();
            Enhancements type = upgrade.getType();
            machine.removeEnhancement(type);
        }
        return result;
    }
}