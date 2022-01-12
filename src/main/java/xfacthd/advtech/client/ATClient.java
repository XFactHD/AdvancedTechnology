package xfacthd.advtech.client;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.color.block.*;
import xfacthd.advtech.client.color.item.*;
import xfacthd.advtech.client.gui.generator.*;
import xfacthd.advtech.client.gui.machine.*;
import xfacthd.advtech.client.gui.storage.*;
import xfacthd.advtech.client.gui.utility.*;
import xfacthd.advtech.client.render.blockentity.*;
import xfacthd.advtech.client.render.item.*;
import xfacthd.advtech.client.util.FluidSpriteCache;
import xfacthd.advtech.client.util.BEWLRModelLoader;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.subtypes.MachineType;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = AdvancedTechnology.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ATClient
{
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        ATContent.ORE_BLOCKS.forEach((material, ore) -> ItemBlockRenderTypes.setRenderLayer(ore.get(), RenderType.cutoutMipped()));
        ATContent.MACHINE_BLOCKS.forEach((type, machine) -> ItemBlockRenderTypes.setRenderLayer(machine.get(), RenderType.cutoutMipped()));

        ItemBlockRenderTypes.setRenderLayer(ATContent.BLOCK_ENERGY_CUBE.get(), type -> type == RenderType.cutoutMipped() || type == RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ATContent.BLOCK_FLUID_TANK.get(), RenderType.cutoutMipped());

        event.enqueueWork(() ->
        {
            MenuScreens.register(ATContent.MENU_FURNACE.get(), ScreenElectricFurnace::new);
            MenuScreens.register(ATContent.MENU_CRUSHER.get(), ScreenCrusher::new);
            MenuScreens.register(ATContent.MENU_ALLOY_SMELTER.get(), ScreenAlloySmelter::new);
            MenuScreens.register(ATContent.MENU_METAL_PRESS.get(), ScreenMetalPress::new);
            MenuScreens.register(ATContent.MENU_LIQUIFIER.get(), ScreenLiquifier::new);
            MenuScreens.register(ATContent.MENU_CHARGER.get(), ScreenCharger::new);
            MenuScreens.register(ATContent.MENU_PLANTER.get(), ScreenPlanter::new);
            MenuScreens.register(ATContent.MENU_HARVESTER.get(), ScreenHarvester::new);
            MenuScreens.register(ATContent.MENU_FERTILIZER.get(), ScreenFertilizer::new);

            MenuScreens.register(ATContent.MENU_BURNER_GENERATOR.get(), ScreenBurnerGenerator::new);
            //MenuScreens.register(ATContent.MENU_SOLAR_PANEL.get(), ScreenSolarPanel::new);

            MenuScreens.register(ATContent.MENU_CHUNK_LOADER.get(), ScreenChunkLoader::new);
            MenuScreens.register(ATContent.MENU_ITEM_SPLITTER.get(), ScreenItemSplitter::new);

            MenuScreens.register(ATContent.MENU_ENERGY_CUBE.get(), ScreenEnergyCube::new);
        });
    }

    @SubscribeEvent
    public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(ATContent.machineEntity(MachineType.LIQUIFIER), RenderLiquifier::new);
        event.registerBlockEntityRenderer(ATContent.machineEntity(MachineType.PLANTER), RenderRangedMachine::new);
        event.registerBlockEntityRenderer(ATContent.machineEntity(MachineType.HARVESTER), RenderRangedMachine::new);
        event.registerBlockEntityRenderer(ATContent.machineEntity(MachineType.FERTILIZER), RenderRangedMachine::new);

        event.registerBlockEntityRenderer(ATContent.machineEntity(MachineType.CHUNK_LOADER), RenderChunkLoader::new);

        event.registerBlockEntityRenderer(ATContent.BE_TYPE_ENERGY_CUBE.get(), RenderEnergyCube::new);
        event.registerBlockEntityRenderer(ATContent.BE_TYPE_FLUID_TANK.get(), RenderFluidTank::new);

        event.registerBlockEntityRenderer(ATContent.BE_TYPE_CREATIVE_ITEM_SOURCE.get(), RenderCreativeItemSource::new);
        event.registerBlockEntityRenderer(ATContent.BE_TYPE_CREATIVE_FLUID_SOURCE.get(), RenderCreativeFluidSource::new);
    }

    @SubscribeEvent
    public static void onRegisterModels(final ModelRegistryEvent event)
    {
        ModelLoaderRegistry.registerLoader(BEWLRModelLoader.LOADER_ID, new BEWLRModelLoader());
    }

    @SubscribeEvent
    public static void onModelBake(final ModelBakeEvent event)
    {
        RenderItemEnergyCube.handleModel(event.getModelRegistry());
        RenderItemFluidTank.handleModel(event.getModelRegistry());
    }

    @SubscribeEvent
    public static void onTexturePreStitch(final TextureStitchEvent.Pre event)
    {
        //noinspection deprecation
        if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS))
        {
            RenderRangedMachine.registerTextures(event);
            RenderEnergyCube.registerTextures(event);
        }
    }

    @SubscribeEvent
    public static void onTexturePostStitch(final TextureStitchEvent.Post event)
    {
        //noinspection deprecation
        if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS))
        {
            RenderRangedMachine.retrieveSprites(event.getAtlas());
            RenderEnergyCube.retrieveSprites(event.getAtlas());
            FluidSpriteCache.invalidateSpriteCache();
        }
    }

    @SubscribeEvent
    public static void onBlockColors(final ColorHandlerEvent.Block event)
    {
        BlockColors blockColors = event.getBlockColors();

        blockColors.register(new BlockColorMaterial(), BlockColorMaterial.getBlocks());
        blockColors.register(new BlockColorMachine(), BlockColorMachine.getBlocks());
        blockColors.register(new BlockColorEnergyCube(), ATContent.BLOCK_ENERGY_CUBE.get());
        blockColors.register(new BlockColorFluidTank(), ATContent.BLOCK_FLUID_TANK.get());
    }

    @SubscribeEvent
    public static void onItemColors(final ColorHandlerEvent.Item event)
    {
        ItemColors itemColors = event.getItemColors();

        itemColors.register(new ItemColorMaterial(), ItemColorMaterial.getItems());
        itemColors.register(new ItemColorMachine(), ItemColorMachine.getItems());
        itemColors.register(new ItemColorUpgrade(), ItemColorUpgrade.getItems());
        itemColors.register(new ItemColorComponent(), ItemColorComponent.getItems());
        itemColors.register(new ItemColorEnergyCube(), ATContent.BLOCK_ENERGY_CUBE.get());
        itemColors.register(new ItemColorFluidTank(), ATContent.BLOCK_FLUID_TANK.get());
    }
}