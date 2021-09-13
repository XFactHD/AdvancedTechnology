package xfacthd.advtech.client.color.item;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import xfacthd.advtech.common.data.states.MachineLevel;
import xfacthd.advtech.common.data.states.SideAccess;

public class ItemColorEnergyCube implements ItemColor
{
    @Override
    public int getColor(ItemStack stack, int tintIndex)
    {
        if (tintIndex != 6) { return SideAccess.NONE.getColor(); }

        CompoundTag nbt = stack.getTagElement("BlockEntityTag");
        if (nbt != null)
        {
            MachineLevel level = MachineLevel.values()[nbt.getInt("level")];
            return level.getColor();
        }
        return MachineLevel.BASIC.getColor();
    }
}