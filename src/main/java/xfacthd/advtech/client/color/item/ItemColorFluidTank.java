package xfacthd.advtech.client.color.item;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import xfacthd.advtech.common.data.states.MachineLevel;
import xfacthd.advtech.common.data.states.SideAccess;

public class ItemColorFluidTank implements ItemColor
{
    @Override
    public int getColor(ItemStack stack, int tintIndex)
    {
        CompoundTag nbt = stack.getTagElement("BlockEntityTag");
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