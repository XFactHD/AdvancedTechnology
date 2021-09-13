package xfacthd.advtech.common.capability.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import xfacthd.advtech.common.data.subtypes.Enhancement;
import xfacthd.advtech.common.item.tool.ItemEnhancement;
import xfacthd.advtech.common.blockentity.BlockEntityMachine;

public class EnhancementItemStackHandler extends ItemStackHandler
{
    private final BlockEntityMachine machine;

    public EnhancementItemStackHandler(BlockEntityMachine machine)
    {
        super(4);
        this.machine = machine;
    }

    @Override
    public int getSlotLimit(int slot) { return 1; }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (stack.isEmpty()) { return true; }
        if (stack.getItem() instanceof ItemEnhancement)
        {
            Enhancement upgrade = ((ItemEnhancement)stack.getItem()).getType();
            return machine.canInstallEnhancement(upgrade);
        }
        return false;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        ItemStack result = super.insertItem(slot, stack, simulate);
        if (result.isEmpty() && !simulate) //Empty means the item was inserted -> activate effects in machine
        {
            ItemEnhancement upgrade = (ItemEnhancement)stack.getItem();
            Enhancement type = upgrade.getType();
            int level = upgrade.getLevel();
            machine.installEnhancement(type, level);
        }
        return result;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        ItemStack result = super.extractItem(slot, amount, simulate);
        if (!result.isEmpty() && !simulate) //Not empty means the item was removed -> deactivate effects on machine
        {
            ItemEnhancement upgrade = (ItemEnhancement)result.getItem();
            Enhancement type = upgrade.getType();
            machine.removeEnhancement(type);
        }
        return result;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack)
    {
        validateSlotIndex(slot);
        ItemStack oldStack = getStackInSlot(slot);
        super.setStackInSlot(slot, stack);

        if (oldStack.getItem() instanceof ItemEnhancement)
        {
            ItemEnhancement upgrade = (ItemEnhancement)oldStack.getItem();
            Enhancement type = upgrade.getType();
            machine.removeEnhancement(type);
        }

        if (stack.getItem() instanceof ItemEnhancement)
        {
            ItemEnhancement upgrade = (ItemEnhancement)stack.getItem();
            Enhancement type = upgrade.getType();
            int level = upgrade.getLevel();
            machine.installEnhancement(type, level);
        }
    }
}