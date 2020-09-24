package xfacthd.advtech.client;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.color.block.*;
import xfacthd.advtech.client.color.item.*;
import xfacthd.advtech.client.gui.storage.*;
import xfacthd.advtech.client.gui.generator.*;
import xfacthd.advtech.client.gui.machine.*;
import xfacthd.advtech.client.gui.utility.*;
import xfacthd.advtech.client.render.ister.*;
import xfacthd.advtech.client.render.ter.*;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.types.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = AdvancedTechnology.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ATClient
{
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        ATContent.blockOre.forEach((mat, block) -> RenderTypeLookup.setRenderLayer(block, RenderType.getCutoutMipped()));

        RenderTypeLookup.setRenderLayer(ATContent.blockElectricFurnace, RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(ATContent.blockCrusher, RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(ATContent.blockAlloySmelter, RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(ATContent.blockMetalPress, RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(ATContent.blockPlanter, RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(ATContent.blockHarvester, RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(ATContent.blockFertilizer, RenderType.getCutoutMipped());

        RenderTypeLookup.setRenderLayer(ATContent.blockBurnerGenerator, RenderType.getCutoutMipped());

        RenderTypeLookup.setRenderLayer(ATContent.blockEnergyCube, type -> type == RenderType.getCutoutMipped() || type == RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ATContent.blockFluidTank, RenderType.getCutoutMipped());

        RenderTypeLookup.setRenderLayer(ATContent.blockChunkLoader, RenderType.getCutoutMipped());

        ClientRegistry.bindTileEntityRenderer(TileEntityTypes.tileTypeCreativeItemSource, RenderCreativeItemSource::new);

        ClientRegistry.bindTileEntityRenderer(TileEntityTypes.tileTypePlanter, RenderRangedMachine::new);
        ClientRegistry.bindTileEntityRenderer(TileEntityTypes.tileTypeHarvester, RenderRangedMachine::new);
        ClientRegistry.bindTileEntityRenderer(TileEntityTypes.tileTypeFertilizer, RenderRangedMachine::new);

        ClientRegistry.bindTileEntityRenderer(TileEntityTypes.tileTypeEnergyCube, RenderEnergyCube::new);
        ClientRegistry.bindTileEntityRenderer(TileEntityTypes.tileTypeFluidTank, RenderFluidTank::new);

        ClientRegistry.bindTileEntityRenderer(TileEntityTypes.tileTypeChunkLoader, RenderChunkLoader::new);

        //noinspection deprecation
        DeferredWorkQueue.runLater(() ->
        {
            ScreenManager.registerFactory(ContainerTypes.containerTypeElectricFurnace, ScreenElectricFurnace::new);
            ScreenManager.registerFactory(ContainerTypes.containerTypeCrusher, ScreenCrusher::new);
            ScreenManager.registerFactory(ContainerTypes.containerTypeAlloySmelter, ScreenAlloySmelter::new);
            ScreenManager.registerFactory(ContainerTypes.containerTypeMetalPress, ScreenMetalPress::new);
            ScreenManager.registerFactory(ContainerTypes.containerTypePlanter, ScreenPlanter::new);
            ScreenManager.registerFactory(ContainerTypes.containerTypeHarvester, ScreenHarvester::new);
            ScreenManager.registerFactory(ContainerTypes.containerTypeFertilizer, ScreenFertilizer::new);

            ScreenManager.registerFactory(ContainerTypes.containerTypeBurnerGenerator, ScreenBurnerGenerator::new);

            ScreenManager.registerFactory(ContainerTypes.containerTypeEnergyCube, ScreenEnergyCube::new);

            ScreenManager.registerFactory(ContainerTypes.containerTypeChunkLoader, ScreenChunkLoader::new);
        });
    }

    @SubscribeEvent
    public static void onRegisterModels(final ModelRegistryEvent event)
    {

    }

    @SubscribeEvent
    public static void onModelBake(final ModelBakeEvent event)
    {
        RenderItemFluidTank.handleModel(event.getModelRegistry());
    }

    @SubscribeEvent
    public static void onTexturePreStitch(final TextureStitchEvent.Pre event)
    {
        if (event.getMap().getTextureLocation().equals(PlayerContainer.LOCATION_BLOCKS_TEXTURE))
        {
            RenderRangedMachine.registerTextures(event);
            RenderEnergyCube.registerTextures(event);
        }
    }

    @SubscribeEvent
    public static void onTexturePostStitch(final TextureStitchEvent.Post event)
    {
        if (event.getMap().getTextureLocation().equals(PlayerContainer.LOCATION_BLOCKS_TEXTURE))
        {
            RenderRangedMachine.retrieveSprites(event.getMap());
            RenderEnergyCube.retrieveSprites(event.getMap());
            RenderFluidTank.invalidateSpriteCache();
        }
    }

    @SubscribeEvent
    public static void onBlockColors(final ColorHandlerEvent.Block event)
    {
        BlockColors blockColors = event.getBlockColors();

        blockColors.register(new BlockColorMaterial(), BlockColorMaterial.getBlocks());
        blockColors.register(new BlockColorMachine(), BlockColorMachine.getBlocks());
        blockColors.register(new BlockColorEnergyCube(), ATContent.blockEnergyCube);
        blockColors.register(new BlockColorFluidTank(), ATContent.blockFluidTank);
    }

    @SubscribeEvent
    public static void onItemColors(final ColorHandlerEvent.Item event)
    {
        ItemColors itemColors = event.getItemColors();

        itemColors.register(new ItemColorMaterial(), ItemColorMaterial.getItems());
        itemColors.register(new ItemColorMachine(), ItemColorMachine.getItems());
        itemColors.register(new ItemColorUpgrade(), ItemColorUpgrade.getItems());
        itemColors.register(new ItemColorComponent(), ItemColorComponent.getItems());
        itemColors.register(new ItemColorEnergyCube(), ATContent.blockEnergyCube);
        itemColors.register(new ItemColorFluidTank(), ATContent.blockFluidTank);
    }
}