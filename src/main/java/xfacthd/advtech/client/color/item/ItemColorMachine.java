package xfacthd.advtech.client.color.item;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.states.MachineLevel;
import xfacthd.advtech.common.data.states.SideAccess;

import java.util.ArrayList;
import java.util.List;

public class ItemColorMachine implements IItemColor
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

    public static IItemProvider[] getItems()
    {
        List<IItemProvider> items = new ArrayList<>();

        items.add(ATContent.blockElectricFurnace);
        items.add(ATContent.blockCrusher);
        items.add(ATContent.blockAlloySmelter);
        items.add(ATContent.blockMetalPress);
        items.add(ATContent.blockLiquifier);
        items.add(ATContent.blockPlanter);
        items.add(ATContent.blockHarvester);
        items.add(ATContent.blockFertilizer);

        items.add(ATContent.blockBurnerGenerator);

        items.add(ATContent.blockChunkLoader);

        return items.toArray(items.toArray(new IItemProvider[0]));
    }
}