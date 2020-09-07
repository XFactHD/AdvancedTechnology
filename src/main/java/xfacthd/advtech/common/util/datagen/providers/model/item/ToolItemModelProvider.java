package xfacthd.advtech.common.util.datagen.providers.model.item;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.*;
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
        simpleItem("item_wrench", modLoc("item/tool/item_wrench"));

        for (MachineLevel level : MachineLevel.values())
        {
            if (level == MachineLevel.BASIC) { continue; }

            simpleItem("item_upgrade_" + level.getName(), modLoc("item/tool/item_upgrade"));
        }

        for (Enhancement type : Enhancement.values())
        {
            for (int level = 0; level < type.getLevels(); level++)
            {
                String name = "item_enhancement_" + type.getName() + "_" + level;
                simpleItem(name, modLoc("item/tool/" + name));
            }
        }

        simpleItem("item_plate_mold", modLoc("item/tool/item_plate_mold"));
        simpleItem("item_gear_mold", modLoc("item/tool/item_gear_mold"));
        simpleItem("item_rod_mold", modLoc("item/tool/item_rod_mold"));
    }

    @Override
    protected String getProviderName() { return "tool_item_models"; }
}