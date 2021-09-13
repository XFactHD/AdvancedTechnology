package xfacthd.advtech.common;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.fmllegacy.network.IContainerFactory;
import net.minecraftforge.registries.*;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.block.debug.*;
import xfacthd.advtech.common.block.material.*;
import xfacthd.advtech.common.block.storage.*;
import xfacthd.advtech.common.blockentity.BlockEntityMachine;
import xfacthd.advtech.common.blockentity.debug.*;
import xfacthd.advtech.common.blockentity.generator.*;
import xfacthd.advtech.common.blockentity.machine.*;
import xfacthd.advtech.common.blockentity.storage.*;
import xfacthd.advtech.common.blockentity.utility.*;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.recipes.*;
import xfacthd.advtech.common.data.sorting.*;
import xfacthd.advtech.common.data.states.*;
import xfacthd.advtech.common.data.subtypes.*;
import xfacthd.advtech.common.item.material.*;
import xfacthd.advtech.common.item.tool.*;
import xfacthd.advtech.common.menu.ContainerMenuMachine;
import xfacthd.advtech.common.menu.generator.*;
import xfacthd.advtech.common.menu.machine.*;
import xfacthd.advtech.common.menu.storage.*;
import xfacthd.advtech.common.menu.utility.*;
import xfacthd.advtech.common.util.MachineContainerFactory;
import xfacthd.advtech.common.util.interfaces.IBlockItemProvider;

import java.util.*;
import java.util.function.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = AdvancedTechnology.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ATContent
{
    /* REGISTRIES */
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AdvancedTechnology.MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AdvancedTechnology.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BE_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, AdvancedTechnology.MODID);
    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, AdvancedTechnology.MODID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AdvancedTechnology.MODID);

    /* BLOCKS */
    public static final Map<MaterialType, RegistryObject<Block>> ORE_BLOCKS = registerMaterialObject(BLOCKS, MaterialCategory.ORE, MaterialType::hasOre, BlockOre::new);
    public static final Map<MaterialType, RegistryObject<Block>> STORAGE_BLOCKS = registerMaterialObject(BLOCKS, MaterialCategory.BLOCK, MaterialType::hasBlock, BlockStorage::new);
    public static final Map<MachineType, RegistryObject<Block>> MACHINE_BLOCKS = registerMachines();

    public static final RegistryObject<Block> BLOCK_ENERGY_CUBE = BLOCKS.register("energy_cube", BlockEnergyCube::new);
    public static final RegistryObject<Block> BLOCK_FLUID_TANK = BLOCKS.register("fluid_tank", BlockFluidTank::new);

    public static final RegistryObject<Block> BLOCK_CREATIVE_ENERGY_SOURCE = BLOCKS.register("creative_energy_source", BlockCreativeEnergySource::new);
    public static final RegistryObject<Block> BLOCK_CREATIVE_ITEM_SOURCE = BLOCKS.register("creative_item_source", BlockCreativeItemSource::new);
    public static final RegistryObject<Block> BLOCK_CREATIVE_FLUID_SOURCE = BLOCKS.register("creative_fluid_source", BlockCreativeFluidSource::new);

    /* ITEMS */
    public static final Map<MaterialType, RegistryObject<Item>> POWDER_ITEMS = registerMaterialObject(ITEMS, MaterialCategory.POWDER, MaterialType::hasPowder, ItemMaterial::new);
    public static final Map<MaterialType, RegistryObject<Item>> INGOT_ITEMS = registerMaterialObject(ITEMS, MaterialCategory.INGOT, MaterialType::hasIngot, ItemMaterial::new);
    public static final Map<MaterialType, RegistryObject<Item>> NUGGET_ITEMS = registerMaterialObject(ITEMS, MaterialCategory.NUGGET, MaterialType::hasNugget, ItemMaterial::new);
    public static final Map<MaterialType, RegistryObject<Item>> GEAR_ITEMS = registerMaterialObject(ITEMS, MaterialCategory.GEAR, MaterialType::hasGear, ItemMaterial::new);
    public static final Map<MaterialType, RegistryObject<Item>> PLATE_ITEMS = registerMaterialObject(ITEMS, MaterialCategory.PLATE, MaterialType::hasPlate, ItemMaterial::new);
    public static final Map<MaterialType, RegistryObject<Item>> MATERIAL_ITEMS = registerMaterialObject(ITEMS, MaterialCategory.MATERIAL, MaterialType::hasChunk, ItemMaterial::new);
    public static final Map<Component, RegistryObject<Item>> COMPONENT_ITEMS = registerComponentItems();
    public static final Map<MachineLevel, RegistryObject<Item>> UPGRADE_ITEMS = registerUpgradeItems();
    public static final Map<Enhancement, Int2ObjectMap<RegistryObject<Item>>> ENHANCEMENT_ITEMS = registerEnhancementItems();

    public static final RegistryObject<Item> ITEM_WRENCH = ITEMS.register("wrench", ItemWrench::new);
    public static final RegistryObject<Item> ITEM_PLATE_MOLD = ITEMS.register("plate_mold", ItemMold::new);
    public static final RegistryObject<Item> ITEM_GEAR_MOLD = ITEMS.register("gear_mold", ItemMold::new);
    public static final RegistryObject<Item> ITEM_ROD_MOLD = ITEMS.register("rod_mold", ItemMold::new);

    /* BLOCK ENTITY TYPES */
    public static final Map<MachineType, RegistryObject<BlockEntityType<BlockEntityMachine>>> MACHINE_BE_TYPES = registerMachineBlockEntities();

    public static final RegistryObject<BlockEntityType<BlockEntityEnergyCube>> BE_TYPE_ENERGY_CUBE = registerBlockEntity("energy_cube", BlockEntityEnergyCube::new, BLOCK_ENERGY_CUBE);
    public static final RegistryObject<BlockEntityType<BlockEntityFluidTank>> BE_TYPE_FLUID_TANK = registerBlockEntity("fluid_tank", BlockEntityFluidTank::new, BLOCK_FLUID_TANK);

    public static final RegistryObject<BlockEntityType<BlockEntityCreativeEnergySource>> BE_TYPE_CREATIVE_ENERGY_SOURCE = registerBlockEntity("creative_energy_source", BlockEntityCreativeEnergySource::new, BLOCK_CREATIVE_ENERGY_SOURCE);
    public static final RegistryObject<BlockEntityType<BlockEntityCreativeItemSource>> BE_TYPE_CREATIVE_ITEM_SOURCE = registerBlockEntity("creative_item_source", BlockEntityCreativeItemSource::new, BLOCK_CREATIVE_ITEM_SOURCE);
    public static final RegistryObject<BlockEntityType<BlockEntityCreativeFluidSource>> BE_TYPE_CREATIVE_FLUID_SOURCE = registerBlockEntity("creative_fluid_source", BlockEntityCreativeFluidSource::new, BLOCK_CREATIVE_FLUID_SOURCE);

    /* MENU TYPES */
    public static final RegistryObject<MenuType<ContainerMenuElectricFurnace>> MENU_FURNACE = registerMachineMenuType("furnace", BlockEntityElectricFurnace.class, ContainerMenuElectricFurnace::new);
    public static final RegistryObject<MenuType<ContainerMenuCrusher>> MENU_CRUSHER = registerMachineMenuType("crusher", BlockEntityCrusher.class, ContainerMenuCrusher::new);
    public static final RegistryObject<MenuType<ContainerMenuAlloySmelter>> MENU_ALLOY_SMELTER = registerMachineMenuType("alloy_smelter", BlockEntityAlloySmelter.class, ContainerMenuAlloySmelter::new);
    public static final RegistryObject<MenuType<ContainerMenuMetalPress>> MENU_METAL_PRESS = registerMachineMenuType("metal_press", BlockEntityMetalPress.class, ContainerMenuMetalPress::new);
    public static final RegistryObject<MenuType<ContainerMenuLiquifier>> MENU_LIQUIFIER = registerMachineMenuType("liquifier", BlockEntityLiquifier.class, ContainerMenuLiquifier::new);
    public static final RegistryObject<MenuType<ContainerMenuCharger>> MENU_CHARGER = registerMachineMenuType("charger", BlockEntityCharger.class, ContainerMenuCharger::new);
    public static final RegistryObject<MenuType<ContainerMenuPlanter>> MENU_PLANTER = registerMachineMenuType("planter", BlockEntityPlanter.class, ContainerMenuPlanter::new);
    public static final RegistryObject<MenuType<ContainerMenuHarvester>> MENU_HARVESTER = registerMachineMenuType("harvester", BlockEntityHarvester.class, ContainerMenuHarvester::new);
    public static final RegistryObject<MenuType<ContainerMenuFertilizer>> MENU_FERTILIZER = registerMachineMenuType("fertilizer", BlockEntityFertilizer.class, ContainerMenuFertilizer::new);

    public static final RegistryObject<MenuType<ContainerMenuBurnerGenerator>> MENU_BURNER_GENERATOR = registerMachineMenuType("burner_generator", BlockEntityBurnerGenerator.class, ContainerMenuBurnerGenerator::new);

    public static final RegistryObject<MenuType<ContainerMenuChunkLoader>> MENU_CHUNK_LOADER = registerMachineMenuType("chunk_loader", BlockEntityChunkLoader.class, ContainerMenuChunkLoader::new);
    public static final RegistryObject<MenuType<ContainerMenuItemSplitter>> MENU_ITEM_SPLITTER = registerMachineMenuType("item_sorter", BlockEntityItemSplitter.class, ContainerMenuItemSplitter::new);

    public static final RegistryObject<MenuType<ContainerMenuEnergyCube>> MENU_ENERGY_CUBE = registerMenuType("energy_cube", ContainerMenuEnergyCube::create);

    /* RECIPE SERIALZERS */
    public static final RegistryObject<RecipeSerializer<CrusherRecipe>> RECIPE_CRUSHER = RECIPE_SERIALIZERS.register("crusher", CrusherRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<AlloySmelterRecipe>> RECIPE_ALLOY_SMELTER = RECIPE_SERIALIZERS.register("alloy_smelter", AlloySmelterRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<MetalPressRecipe>> RECIPE_METAL_PRESS = RECIPE_SERIALIZERS.register("metal_press", MetalPressRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<LiquifierRecipe>> RECIPE_LIQUIFIER = RECIPE_SERIALIZERS.register("liquifier", LiquifierRecipe.Serializer::new);

    public static void init()
    {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        MENU_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();

        Stream.of(
                ORE_BLOCKS.values(),
                STORAGE_BLOCKS.values(),
                MACHINE_BLOCKS.values(),
                Arrays.asList(
                        BLOCK_ENERGY_CUBE,
                        BLOCK_FLUID_TANK,
                        BLOCK_CREATIVE_ENERGY_SOURCE,
                        BLOCK_CREATIVE_ITEM_SOURCE,
                        BLOCK_CREATIVE_FLUID_SOURCE
                )
        ).flatMap(Collection::stream)
                .map(RegistryObject::get)
                .map(block -> ((IBlockItemProvider)block).createBlockItem())
                .forEach(registry::register);

        ItemGroups.finalizeItemGroups();
    }

    private static <T extends IForgeRegistryEntry<T>> Map<MaterialType, RegistryObject<T>> registerMaterialObject(
            DeferredRegister<T> registry, MaterialCategory category, Predicate<MaterialType> filter, BiFunction<MaterialType, MaterialCategory, T> factory
    )
    {
        return Arrays.stream(MaterialType.values())
                .filter(filter)
                .map(type -> Pair.of(
                        type,
                        registry.register(
                                type.getName(category),
                                () -> factory.apply(type, category)
                        )
                ))
                .collect(Pair.toMap());
    }

    private static Map<MachineType, RegistryObject<Block>> registerMachines()
    {
        return Arrays.stream(MachineType.values())
                .map(type -> Pair.of(
                        type,
                        BLOCKS.register(type.getSerializedName(), () -> type.getBlockFactory().apply(type))
                ))
                .collect(Pair.toMap());
    }

    private static Map<Component, RegistryObject<Item>> registerComponentItems()
    {
        return Arrays.stream(Component.values())
                .map(type -> Pair.of(
                        type,
                        ITEMS.register(type.getSerializedName(), () -> (Item) new ItemComponent(type))
                ))
                .collect(Pair.toMap());
    }

    private static Map<MachineLevel, RegistryObject<Item>> registerUpgradeItems()
    {
        return Arrays.stream(MachineLevel.values())
                .filter(level -> level != MachineLevel.BASIC)
                .map(type -> Pair.of(
                        type,
                        ITEMS.register(
                                "upgrade_" + type.getSerializedName(),
                                () -> (Item) new ItemUpgrade(type)
                        )
                ))
                .collect(Pair.toMap());
    }

    private static Map<Enhancement, Int2ObjectMap<RegistryObject<Item>>> registerEnhancementItems()
    {
        return Arrays.stream(Enhancement.values())
                .map(type -> Pair.of(
                        type,
                        IntStream.range(0, type.getLevels())
                                .mapToObj(level -> Pair.of(
                                        level,
                                        ITEMS.register(
                                                type.toItemName(level),
                                                () -> (Item) new ItemEnhancement(type, level)
                                        )
                                ))
                                .collect(
                                        () -> (Int2ObjectMap<RegistryObject<Item>>) new Int2ObjectArrayMap<RegistryObject<Item>>(),
                                        (map, pair) -> map.put(pair.getFirst().intValue(), pair.getSecond()),
                                        Map::putAll
                                )
                ))
                .collect(Pair.toMap());
    }

    private static Map<MachineType, RegistryObject<BlockEntityType<BlockEntityMachine>>> registerMachineBlockEntities()
    {
        return Arrays.stream(MachineType.values())
                .filter(type -> !type.isCasing())
                .map(type -> Pair.of(
                        type,
                        registerBlockEntity(type.getSerializedName(), type.getTileFactory(), MACHINE_BLOCKS.get(type))
                ))
                .collect(Pair.toMap());
    }

    /* INTERNAL HELPERS */

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBlockEntity(
            String name, BlockEntityType.BlockEntitySupplier<T> supplier, RegistryObject<Block> block
    )
    {
        return registerBlockEntity(name, supplier, () -> new Block[] { block.get() });
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBlockEntity(
            String name, BlockEntityType.BlockEntitySupplier<T> supplier, RegistryObject<Block>[] blocks
    )
    {
        return registerBlockEntity(
                name,
                supplier,
                () -> Arrays.stream(blocks).map(RegistryObject::get).toArray(Block[]::new)
        );
    }

    @SuppressWarnings("ConstantConditions")
    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBlockEntity(
            String name, BlockEntityType.BlockEntitySupplier<T> supplier, Supplier<Block[]> blocks
    )
    {
        return BE_TYPES.register(
                name,
                () -> BlockEntityType.Builder
                        .of(
                                supplier,
                                blocks.get()
                        )
                        .build(null)
        );
    }

    private static <T extends BlockEntityMachine, M extends ContainerMenuMachine<T>> RegistryObject<MenuType<M>> registerMachineMenuType(
            String name, Class<T> machine, MachineContainerFactory<T, M> factory
    )
    {
        return registerMenuType(
                name,
                (windowId, inv, data) ->
                {
                    BlockEntity be = inv.player.level.getBlockEntity(data.readBlockPos());
                    Preconditions.checkNotNull(be);
                    Preconditions.checkState(machine.isAssignableFrom(be.getClass()));
                    //noinspection unchecked
                    return factory.create(windowId, (T)be, inv);
                }
        );
    }

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory)
    {
        return MENU_TYPES.register(
                name,
                () -> IForgeContainerType.create(factory)
        );
    }

    /* EXTERNAL HELPERS */

    public static BlockMachine machineBlock(MachineType type)
    {
        Block block = MACHINE_BLOCKS.get(type).get();
        Preconditions.checkState(block instanceof BlockMachine);
        return (BlockMachine) block;
    }

    @SuppressWarnings("unchecked")
    public static <T extends BlockEntityMachine> BlockEntityType<T> machineEntity(MachineType type) { return (BlockEntityType<T>) MACHINE_BE_TYPES.get(type).get(); }
}