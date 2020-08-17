package xfacthd.advtech.client.color.item;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import xfacthd.advtech.common.data.states.MachineLevel;
import xfacthd.advtech.common.data.states.SideAccess;

public class ItemColorEnergyCube implements IItemColor
{
    @Override
    public int getColor(ItemStack stack, int tintIndex)
    {
        if (tintIndex != 6) { return SideAccess.NONE.getColor(); }

        CompoundNBT nbt = stack.getChildTag("BlockEntityTag");
        if (nbt != null)
        {
            MachineLevel level = MachineLevel.values()[nbt.getInt("level")];
            return level.getColor();
        }
        return MachineLevel.BASIC.getColor();
    }
}