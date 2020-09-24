package xfacthd.advtech.client.color.item;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import xfacthd.advtech.common.data.states.MachineLevel;
import xfacthd.advtech.common.data.states.SideAccess;

public class ItemColorFluidTank implements IItemColor
{
    @Override
    public int getColor(ItemStack stack, int tintIndex)
    {
        CompoundNBT nbt = stack.getChildTag("BlockEntityTag");
        if (nbt != null)
        {
            if (tintIndex == 0)
            {
                MachineLevel level = MachineLevel.values()[nbt.getInt("level")];
                return level.getColor();
            }
            else if (tintIndex == 1)
            {
                SideAccess mode = SideAccess.values()[nbt.getInt("mode")];
                return mode.getColor();
            }
        }
        return 0xFFFFFF;
    }
}