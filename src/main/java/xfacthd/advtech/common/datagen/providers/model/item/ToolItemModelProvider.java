package xfacthd.advtech.common.datagen.providers.model.item;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.advtech.common.data.states.MachineLevel;
import xfacthd.advtech.common.data.subtypes.Enhancement;

public class ToolItemModelProvider extends ATItemModelProvider
{
    public ToolItemModelProvider(DataGenerator generator, ExistingFileHelper fileHelper)
    {
        super(generator, fileHelper);
    }

    @Override
    protected void registerModels()
    {
        simpleItem("wrench", modLoc("item/tool/item_wrench"));

        for (MachineLevel level : MachineLevel.values())
        {
            if (level == MachineLevel.BASIC) { continue; }

            simpleItem("upgrade_" + level.getSerializedName(), modLoc("item/tool/item_upgrade"));
        }

        for (Enhancement type : Enhancement.values())
        {
            for (int level = 0; level < type.getLevels(); level++)
            {
                String name = "enhancement_" + type.getSerializedName() + "_" + level;
                simpleItem(name, modLoc("item/tool/item_" + name));
            }
        }

        simpleItem("plate_mold", modLoc("item/tool/item_plate_mold"));
        simpleItem("gear_mold", modLoc("item/tool/item_gear_mold"));
        simpleItem("rod_mold", modLoc("item/tool/item_rod_mold"));
    }

    @Override
    protected String getProviderName() { return "tool_item_models"; }
}