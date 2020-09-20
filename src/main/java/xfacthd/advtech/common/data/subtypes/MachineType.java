package xfacthd.advtech.common.data.subtypes;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.data.sorting.MachineCategory;

import java.util.Locale;

public enum MachineType implements IStringSerializable
{
    CASING          (MachineCategory.CASING),

    ELECTRIC_FURNACE(MachineCategory.PRODUCER),
    CRUSHER         (MachineCategory.PRODUCER),
    ALLOY_SMELTER   (MachineCategory.PRODUCER),
    METAL_PRESS     (MachineCategory.PRODUCER),
    LIQUIFIER       (MachineCategory.PRODUCER),
    FLUID_FILLER    (MachineCategory.PRODUCER),
    FREEZER         (MachineCategory.PRODUCER),
    CHARGER         (MachineCategory.PRODUCER),
    PLANTER         (MachineCategory.PRODUCER, false),
    HARVESTER       (MachineCategory.PRODUCER),
    FERTILIZER      (MachineCategory.PRODUCER),

    BURNER_GENERATOR(MachineCategory.GENERATOR),
    STEAM_GENERATOR (MachineCategory.GENERATOR), // Use boiler upgrade to consume steam instead of burnable stuff and water, use steam upgrade to create steam instead of power
    MAGMA_GENERATOR (MachineCategory.GENERATOR),

    CHUNK_LOADER    (MachineCategory.UTILITY, false);

    private final ResourceLocation texture = new ResourceLocation(AdvancedTechnology.MODID, "textures/block/machine/block_" + getName() + ".png");
    private final MachineCategory cat;
    private final boolean rotatable;

    MachineType(MachineCategory cat) { this(cat, true); }

    MachineType(MachineCategory cat, boolean rotatable)
    {
        this.cat = cat;
        this.rotatable = rotatable;
    }

    @Override
    public String getName() { return toString().toLowerCase(Locale.ENGLISH); }

    public boolean isCasing() { return this == CASING; }

    public boolean canBeRotated() { return rotatable; }

    public MachineCategory getCategory() { return cat; }

    public ResourceLocation getTexture() { return texture; }
}