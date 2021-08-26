package xfacthd.advtech.common.data.subtypes;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.data.sorting.MachineCategory;
import xfacthd.advtech.common.tileentity.TileEntityMachine;
import xfacthd.advtech.common.tileentity.generator.*;
import xfacthd.advtech.common.tileentity.machine.*;
import xfacthd.advtech.common.tileentity.utility.*;

import java.util.Locale;
import java.util.function.Supplier;

public enum MachineType implements IStringSerializable
{
    CASING          (MachineCategory.CASING, () -> null),

    ELECTRIC_FURNACE(MachineCategory.PRODUCER, TileEntityElectricFurnace::new),
    CRUSHER         (MachineCategory.PRODUCER, TileEntityCrusher::new),
    ALLOY_SMELTER   (MachineCategory.PRODUCER, TileEntityAlloySmelter::new),
    METAL_PRESS     (MachineCategory.PRODUCER, TileEntityMetalPress::new),
    LIQUIFIER       (MachineCategory.PRODUCER, TileEntityLiquifier::new),
    FLUID_FILLER    (MachineCategory.PRODUCER, () -> null),
    FREEZER         (MachineCategory.PRODUCER, () -> null),
    CHARGER         (MachineCategory.PRODUCER, TileEntityCharger::new),
    PLANTER         (MachineCategory.PRODUCER, TileEntityPlanter::new, false),
    HARVESTER       (MachineCategory.PRODUCER, TileEntityHarvester::new),
    FERTILIZER      (MachineCategory.PRODUCER, TileEntityFertilizer::new),
    QUARRY          (MachineCategory.PRODUCER, () -> null),
    CRAFTER         (MachineCategory.PRODUCER, () -> null),
    FLUID_PUMP      (MachineCategory.PRODUCER, () -> null),

    BURNER_GENERATOR(MachineCategory.GENERATOR, TileEntityBurnerGenerator::new),
    STEAM_GENERATOR (MachineCategory.GENERATOR, () -> null), // Use boiler upgrade to consume steam instead of burnable stuff and water, use steam upgrade to create steam instead of power
    MAGMA_GENERATOR (MachineCategory.GENERATOR, () -> null),
    SOLAR_PANEL     (MachineCategory.GENERATOR, TileEntitySolarPanel::new, false),

    CHUNK_LOADER        (MachineCategory.UTILITY, TileEntityChunkLoader::new, false),
    TRASH_CAN           (MachineCategory.UTILITY, () -> null, false),
    WIRELESS_CHARGER    (MachineCategory.UTILITY, () -> null, false),
    SPATIAL_TRANSPORTER (MachineCategory.UTILITY, () -> null, false);

    private final ResourceLocation texture = new ResourceLocation(AdvancedTechnology.MODID, "textures/block/machine/block_" + getName() + ".png");
    private final MachineCategory cat;
    private final Supplier<TileEntityMachine> tileFactory;
    private final boolean rotatable;

    MachineType(MachineCategory cat, Supplier<TileEntityMachine> tileFactory) { this(cat, tileFactory, true); }

    MachineType(MachineCategory cat, Supplier<TileEntityMachine> tileFactory, boolean rotatable)
    {
        this.cat = cat;
        this.tileFactory = tileFactory;
        this.rotatable = rotatable;
    }

    @Override
    public String getName() { return toString().toLowerCase(Locale.ENGLISH); }

    public boolean isCasing() { return this == CASING; }

    public Supplier<TileEntityMachine> getTileFactory() { return tileFactory; }

    public boolean canBeRotated() { return rotatable; }

    public MachineCategory getCategory() { return cat; }

    public ResourceLocation getTexture() { return texture; }
}