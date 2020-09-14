package xfacthd.advtech.common;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.block.*;
import xfacthd.advtech.common.block.debug.*;
import xfacthd.advtech.common.block.energy.*;
import xfacthd.advtech.common.block.generator.*;
import xfacthd.advtech.common.block.machine.*;
import xfacthd.advtech.common.block.material.*;
import xfacthd.advtech.common.block.utility.*;
import xfacthd.advtech.common.container.energy.*;
import xfacthd.advtech.common.container.generator.*;
import xfacthd.advtech.common.container.machine.*;
import xfacthd.advtech.common.container.utility.*;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.recipes.*;
import xfacthd.advtech.common.data.states.MachineLevel;
import xfacthd.advtech.common.data.subtypes.*;
import xfacthd.advtech.common.data.types.*;
import xfacthd.advtech.common.item.ItemBase;
import xfacthd.advtech.common.item.debug.*;
import xfacthd.advtech.common.item.material.*;
import xfacthd.advtech.common.item.tool.*;
import xfacthd.advtech.common.tileentity.debug.*;
import xfacthd.advtech.common.tileentity.energy.*;
import xfacthd.advtech.common.tileentity.generator.*;
import xfacthd.advtech.common.tileentity.machine.*;
import xfacthd.advtech.common.tileentity.utility.*;

import java.util.Map;

@Mod.EventBusSubscriber(modid = AdvancedTechnology.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ATContent
{
    public static Map<Materials, BlockOre> blockOre;            //STATUS: Complete
    public static Map<Materials, BlockStorage> blockStorage;    //STATUS: Complete

    public static BlockBase blockCreativeEnergySource;          //STATUS: Complete
    public static BlockBase blockCreativeItemSource;            //STATUS: Complete

    public static BlockMachine blockMachineCasing;              //STATUS: Complete
    public static BlockMachine blockElectricFurnace;            //STATUS: Complete
    public static BlockMachine blockCrusher;                    //STATUS: Complete
    public static BlockMachine blockAlloySmelter;               //STATUS: Complete
    public static BlockMachine blockMetalPress;                 //STATUS: Complete
    public static BlockMachine blockLiquifier;                  //STATUS: Not implemented
    public static BlockMachine blockFluidFiller;                //STATUS: Not implemented
    public static BlockMachine blockFreezer;                    //STATUS: Not implemented
    public static BlockMachine blockCharger;                    //STATUS: Not implemented
    public static BlockMachine blockPlanter;                    //STATUS: Complete
    public static BlockMachine blockHarvester;                  //STATUS: Complete
    public static BlockMachine blockFertilizer;                 //STATUS: Complete

    public static BlockMachine blockBurnerGenerator;            //STATUS: Complete
    public static BlockMachine blockSteamGenerator;             //STATUS: Not implemented
    public static BlockMachine blockMagmaGenerator;             //STATUS: Not implemented

    public static BlockBase blockEnergyCube;                    //STATUS: Complete

    public static BlockMachine blockChunkLoader;                //STATUS: Complete

    public static Map<Materials, ItemPowder> itemPowder;        //STATUS: Complete
    public static Map<Materials, ItemIngot> itemIngot;          //STATUS: Complete
    public static Map<Materials, ItemNugget> itemNugget;        //STATUS: Complete
    public static Map<Materials, ItemGear> itemGear;            //STATUS: Complete
    public static Map<Materials, ItemPlate> itemPlate;          //STATUS: Complete
    public static Map<Materials, ItemMaterial> itemMaterial;    //STATUS: Complete

    public static Map<Components, ItemComponent> itemComponent; //STATUS: Missing types

    public static ItemBase itemWrench;                          //STATUS: Complete
    public static Map<MachineLevel, ItemUpgrade> itemUpgrade;   //STATUS: Complete
    public static ItemEnhancement.Holder itemEnhancement;       //STATUS: Missing types
    public static ItemBase itemPlateMold;                       //STATUS: Complete
    public static ItemBase itemGearMold;                        //STATUS: Complete
    public static ItemBase itemRodMold;                         //STATUS: Complete

    public static ItemBase itemBlockRemover;                    //STATUS: Complete

    @SubscribeEvent
    public static void onRegisterBlocks(final RegistryEvent.Register<Block> event)
    {
        final IForgeRegistry<Block> registry = event.getRegistry();

        registry.registerAll(BlockOre.registerBlocks());
        registry.registerAll(BlockStorage.registerBlocks());

        registry.register(blockCreativeEnergySource = new BlockCreativeEnergySource());
        registry.register(blockCreativeItemSource = new BlockCreativeItemSource());

        registry.register(blockMachineCasing = new BlockMachineCasing());
        registry.register(blockElectricFurnace = new BlockElectricFurnace());
        registry.register(blockCrusher = new BlockCrusher());
        registry.register(blockAlloySmelter = new BlockAlloySmelter());
        registry.register(blockMetalPress = new BlockMetalPress());
        registry.register(blockPlanter = new BlockPlanter());
        registry.register(blockHarvester = new BlockHarvester());
        registry.register(blockFertilizer = new BlockFertilizer());

        registry.register(blockBurnerGenerator = new BlockBurnerGenerator());

        registry.register(blockEnergyCube = new BlockEnergyCube());

        registry.register(blockChunkLoader = new BlockChunkLoader());
    }

    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event)
    {
        final IForgeRegistry<Item> registry = event.getRegistry();

        blockOre.forEach((mat, block) -> registry.register(block.createItemBlock()));
        blockStorage.forEach((mat, block) -> registry.register(block.createItemBlock()));

        registry.register(blockCreativeEnergySource.createItemBlock());
        registry.register(blockCreativeItemSource.createItemBlock());

        registry.register(blockMachineCasing.createItemBlock());
        registry.register(blockElectricFurnace.createItemBlock());
        registry.register(blockCrusher.createItemBlock());
        registry.register(blockAlloySmelter.createItemBlock());
        registry.register(blockMetalPress.createItemBlock());
        registry.register(blockPlanter.createItemBlock());
        registry.register(blockHarvester.createItemBlock());
        registry.register(blockFertilizer.createItemBlock());

        registry.register(blockBurnerGenerator.createItemBlock());

        registry.register(blockEnergyCube.createItemBlock());

        registry.register(blockChunkLoader.createItemBlock());

        registry.registerAll(ItemPowder.registerItems());
        registry.registerAll(ItemIngot.registerItems());
        registry.registerAll(ItemNugget.registerItems());
        registry.registerAll(ItemGear.registerItems());
        registry.registerAll(ItemPlate.registerItems());
        registry.registerAll(ItemMaterial.registerItems());

        registry.registerAll(ItemComponent.registerItems());

        registry.register(itemWrench = new ItemWrench());
        registry.registerAll(ItemUpgrade.registerItems());
        registry.registerAll((itemEnhancement = new ItemEnhancement.Holder()).registerItems());
        registry.register(itemPlateMold = new ItemPlateMold());
        registry.register(itemGearMold = new ItemGearMold());
        registry.register(itemRodMold = new ItemRodMold());

        registry.register(itemBlockRemover = new ItemBlockRemover());

        ItemGroups.finalizeItemGroups();
    }

    @SubscribeEvent
    public static void onRegisterTileEntities(final RegistryEvent.Register<TileEntityType<?>> event)
    {
        TileEntityTypes.setRegistry(event.getRegistry());

        TileEntityTypes.tileTypeCreativeEnergySource = TileEntityTypes.create(TileEntityCreativeEnergySource::new, "tile_creative_energy_source", blockCreativeEnergySource);
        TileEntityTypes.tileTypeCreativeItemSource = TileEntityTypes.create(TileEntityCreativeItemSource::new, "tile_creative_item_source", blockCreativeItemSource);

        TileEntityTypes.tileTypeElectricFurnace = TileEntityTypes.create(TileEntityElectricFurnace::new, "tile_electric_furnace", blockElectricFurnace);
        TileEntityTypes.tileTypeCrusher = TileEntityTypes.create(TileEntityCrusher::new, "tile_crusher", blockCrusher);
        TileEntityTypes.tileTypeAlloySmelter = TileEntityTypes.create(TileEntityAlloySmelter::new, "tile_alloy_smelter", blockAlloySmelter);
        TileEntityTypes.tileTypeMetalPress = TileEntityTypes.create(TileEntityMetalPress::new, "tile_metal_press", blockMetalPress);
        TileEntityTypes.tileTypePlanter = TileEntityTypes.create(TileEntityPlanter::new, "tile_planter", blockPlanter);
        TileEntityTypes.tileTypeHarvester = TileEntityTypes.create(TileEntityHarvester::new, "tile_harvester", blockHarvester);
        TileEntityTypes.tileTypeFertilizer = TileEntityTypes.create(TileEntityFertilizer::new, "tile_fertilizer", blockFertilizer);

        TileEntityTypes.tileTypeBurnerGenerator = TileEntityTypes.create(TileEntityBurnerGenerator::new, "tile_burner_generator", blockBurnerGenerator);

        TileEntityTypes.tileTypeEnergyCube = TileEntityTypes.create(TileEntityEnergyCube::new, "tile_energy_cube", blockEnergyCube);

        TileEntityTypes.tileTypeChunkLoader = TileEntityTypes.create(TileEntityChunkLoader::new, "tile_chunk_loader", blockChunkLoader);
    }

    @SubscribeEvent
    public static void onRegisterContainers(final RegistryEvent.Register<ContainerType<?>> event)
    {
        ContainerTypes.setRegistry(event.getRegistry());

        ContainerTypes.containerTypeElectricFurnace = ContainerTypes.create("container_electric_furnace", (windowId, inv, data) ->
        {
            TileEntity te = inv.player.world.getTileEntity(data.readBlockPos());
            return new ContainerElectricFurnace(windowId, (TileEntityElectricFurnace)te, inv);
        });

        ContainerTypes.containerTypeCrusher = ContainerTypes.create("container_crusher", (windowId, inv, data) ->
        {
            TileEntity te = inv.player.world.getTileEntity(data.readBlockPos());
            return new ContainerCrusher(windowId, (TileEntityCrusher)te, inv);
        });

        ContainerTypes.containerTypeAlloySmelter = ContainerTypes.create("container_alloy_smelter", (windowId, inv, data) ->
        {
            TileEntity te = inv.player.world.getTileEntity(data.readBlockPos());
            return new ContainerAlloySmelter(windowId, (TileEntityAlloySmelter)te, inv);
        });

        ContainerTypes.containerTypeMetalPress = ContainerTypes.create("container_metal_press", (windowId, inv, data) ->
        {
            TileEntity te = inv.player.world.getTileEntity(data.readBlockPos());
            return new ContainerMetalPress(windowId, (TileEntityMetalPress)te, inv);
        });

        ContainerTypes.containerTypePlanter = ContainerTypes.create("container_planter", (windowId, inv, data) ->
        {
            TileEntity te = inv.player.world.getTileEntity(data.readBlockPos());
            return new ContainerPlanter(windowId, (TileEntityPlanter)te, inv);
        });

        ContainerTypes.containerTypeHarvester = ContainerTypes.create("container_harvester", (windowId, inv, data) ->
        {
            TileEntity te = inv.player.world.getTileEntity(data.readBlockPos());
            return new ContainerHarvester(windowId, (TileEntityHarvester)te, inv);
        });

        ContainerTypes.containerTypeFertilizer = ContainerTypes.create("container_fertilizer", (windowId, inv, data) ->
        {
            TileEntity te = inv.player.world.getTileEntity(data.readBlockPos());
            return new ContainerFertilizer(windowId, (TileEntityFertilizer)te, inv);
        });



        ContainerTypes.containerTypeBurnerGenerator = ContainerTypes.create("container_burner_generator", (windowId, inv, data) ->
        {
            TileEntity te = inv.player.world.getTileEntity(data.readBlockPos());
            return new ContainerBurnerGenerator(windowId, (TileEntityBurnerGenerator)te, inv);
        });



        ContainerTypes.containerTypeEnergyCube = ContainerTypes.create("container_energy_cube", (windowId, inv, data) ->
        {
            TileEntity te = inv.player.world.getTileEntity(data.readBlockPos());
            //noinspection ConstantConditions
            return new ContainerEnergyCube(windowId, (TileEntityEnergyCube)te, inv);
        });



        ContainerTypes.containerTypeChunkLoader = ContainerTypes.create("container_chunk_loader", (windowId, inv, data) ->
        {
            TileEntity te = inv.player.world.getTileEntity(data.readBlockPos());
            return new ContainerChunkLoader(windowId, (TileEntityChunkLoader)te, inv);
        });
    }

    @SubscribeEvent
    public static void onRegisterRecipeSerializers(final RegistryEvent.Register<IRecipeSerializer<?>> event)
    {
        final IForgeRegistry<IRecipeSerializer<?>> registry = event.getRegistry();

        registry.register(RecipeSerializers.crusher = new CrusherRecipe.Serializer());
        registry.register(RecipeSerializers.alloySmelter = new AlloySmelterRecipe.Serializer());
        registry.register(RecipeSerializers.metalPress = new MetalPressRecipe.Serializer());
    }
}