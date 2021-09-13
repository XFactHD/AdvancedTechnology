package xfacthd.advtech.common.data.subtypes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.block.generator.*;
import xfacthd.advtech.common.block.utility.*;
import xfacthd.advtech.common.blockentity.BlockEntityMachine;
import xfacthd.advtech.common.blockentity.generator.*;
import xfacthd.advtech.common.blockentity.utility.*;
import xfacthd.advtech.common.data.sorting.MachineCategory;
import xfacthd.advtech.common.blockentity.machine.*;

import java.util.Locale;
import java.util.function.Function;

public enum MachineType implements StringRepresentable
{
    CASING          (MachineCategory.CASING, null),

    ELECTRIC_FURNACE(MachineCategory.PRODUCER, BlockEntityElectricFurnace::new),
    CRUSHER         (MachineCategory.PRODUCER, BlockEntityCrusher::new),
    ALLOY_SMELTER   (MachineCategory.PRODUCER, BlockEntityAlloySmelter::new),
    METAL_PRESS     (MachineCategory.PRODUCER, BlockEntityMetalPress::new),
    LIQUIFIER       (MachineCategory.PRODUCER, BlockEntityLiquifier::new),
    //FLUID_FILLER    (MachineCategory.PRODUCER, () -> null),
    //FREEZER         (MachineCategory.PRODUCER, () -> null),
    CHARGER         (MachineCategory.PRODUCER, BlockEntityCharger::new),
    PLANTER         (MachineCategory.PRODUCER, BlockEntityPlanter::new, false),
    HARVESTER       (MachineCategory.PRODUCER, BlockEntityHarvester::new),
    FERTILIZER      (MachineCategory.PRODUCER, BlockEntityFertilizer::new),
    //QUARRY          (MachineCategory.PRODUCER, () -> null),
    //CRAFTER         (MachineCategory.PRODUCER, () -> null),
    //FLUID_PUMP      (MachineCategory.PRODUCER, () -> null),

    BURNER_GENERATOR(MachineCategory.GENERATOR, BlockEntityBurnerGenerator::new),
    //STEAM_GENERATOR (MachineCategory.GENERATOR, () -> null), // Use boiler upgrade to consume steam instead of burnable stuff and water, use steam upgrade to create steam instead of power
    //MAGMA_GENERATOR (MachineCategory.GENERATOR, () -> null),
    SOLAR_PANEL     (MachineCategory.GENERATOR, BlockSolarPanel::new, BlockEntitySolarPanel::new, false),

    CHUNK_LOADER        (MachineCategory.UTILITY, BlockChunkLoader::new, BlockEntityChunkLoader::new, false),
    //BLOCK_BREAKER   (MachineCategory.UTILITY, () -> null),
    //BLOCK_PLACER    (MachineCategory.UTILITY, () -> null),
    /*TRASH_CAN           (MachineCategory.UTILITY, () -> null, false),
    WIRELESS_CHARGER    (MachineCategory.UTILITY, () -> null, false),
    SPATIAL_TRANSPORTER (MachineCategory.UTILITY, () -> null, false),*/
    ITEM_SPLITTER       (MachineCategory.UTILITY, BlockEntityItemSplitter::new);

    private final ResourceLocation texture = new ResourceLocation(AdvancedTechnology.MODID, "textures/block/machine/block_" + getSerializedName() + ".png");
    private final MachineCategory cat;
    private final Function<MachineType, Block> blockFactory;
    private final BlockEntityType.BlockEntitySupplier<BlockEntityMachine> tileFactory;
    private final boolean rotatable;

    MachineType(MachineCategory cat, BlockEntityType.BlockEntitySupplier<BlockEntityMachine> tileFactory) { this(cat, BlockMachine::new, tileFactory); }

    MachineType(MachineCategory cat, BlockEntityType.BlockEntitySupplier<BlockEntityMachine> tileFactory, boolean rotatable) { this(cat, BlockMachine::new, tileFactory, rotatable); }

    MachineType(MachineCategory cat, Function<MachineType, Block> blockFactory, BlockEntityType.BlockEntitySupplier<BlockEntityMachine> tileFactory) { this(cat, blockFactory, tileFactory, true); }

    MachineType(MachineCategory cat, Function<MachineType, Block> blockFactory, BlockEntityType.BlockEntitySupplier<BlockEntityMachine> tileFactory, boolean rotatable)
    {
        this.cat = cat;
        this.blockFactory = blockFactory;
        this.tileFactory = tileFactory;
        this.rotatable = rotatable;
    }

    public Function<MachineType, Block> getBlockFactory() { return blockFactory; }

    public BlockEntityType.BlockEntitySupplier<BlockEntityMachine> getTileFactory() { return tileFactory; }

    public boolean isCasing() { return this == CASING; }

    public boolean canBeRotated() { return rotatable; }

    public MachineCategory getCategory() { return cat; }

    public ResourceLocation getTexture() { return texture; }

    @Override
    public String getSerializedName() { return toString().toLowerCase(Locale.ENGLISH); }
}