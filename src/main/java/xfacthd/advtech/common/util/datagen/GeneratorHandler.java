package xfacthd.advtech.common.util.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.util.datagen.providers.lang.EnglishLangProvider;
import xfacthd.advtech.common.util.datagen.providers.loot.*;
import xfacthd.advtech.common.util.datagen.providers.model.block.*;
import xfacthd.advtech.common.util.datagen.providers.model.item.*;
import xfacthd.advtech.common.util.datagen.providers.tags.*;
import xfacthd.advtech.common.util.datagen.providers.recipe.*;

@Mod.EventBusSubscriber(modid = AdvancedTechnology.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GeneratorHandler
{
    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        gen.addProvider(new MaterialBlockStateProvider(gen, fileHelper));
        gen.addProvider(new DebugBlockStateProvider(gen, fileHelper));
        gen.addProvider(new MachineBlockStateProvider(gen, fileHelper));
        gen.addProvider(new StorageBlockStateProvider(gen, fileHelper));

        gen.addProvider(new MaterialItemModelProvider(gen, fileHelper));
        gen.addProvider(new ComponentItemModelProvider(gen, fileHelper));
        gen.addProvider(new ToolItemModelProvider(gen, fileHelper));
        gen.addProvider(new DebugItemModelProvider(gen, fileHelper));

        //gen.addProvider(new SoundProvider(gen, fileHelper));

        gen.addProvider(new BlockLootTableProvider(gen));

        gen.addProvider(new BlockTagProvider(gen));
        gen.addProvider(new ItemTagProvider(gen));

        gen.addProvider(new TableRecipeProvider(gen));
        gen.addProvider(new FurnaceRecipeProvider(gen));
        gen.addProvider(new CrusherRecipeProvider(gen));
        gen.addProvider(new AlloySmelterRecipeProvider(gen));
        gen.addProvider(new MetalPressRecipeProvider(gen));
        gen.addProvider(new LiquifierRecipeProvider(gen));

        gen.addProvider(new EnglishLangProvider(gen));
    }
}