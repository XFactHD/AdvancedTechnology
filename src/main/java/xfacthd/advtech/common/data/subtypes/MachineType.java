package xfacthd.advtech.common.data.subtypes;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import xfacthd.advtech.AdvancedTechnology;

import java.util.Locale;

public enum MachineType implements IStringSerializable
{
    CASING          (Category.CASING),

    ELECTRIC_FURNACE(Category.PRODUCER),
    CRUSHER         (Category.PRODUCER),
    ALLOY_SMELTER   (Category.PRODUCER),
    METAL_PRESS     (Category.PRODUCER),
    LIQUIFIER       (Category.PRODUCER),
    FLUID_FILLER    (Category.PRODUCER),
    FREEZER         (Category.PRODUCER),
    CHARGER         (Category.PRODUCER),
    PLANTER         (Category.PRODUCER, false),
    HARVESTER       (Category.PRODUCER),
    FERTILIZER      (Category.PRODUCER),

    BURNER_GENERATOR(Category.GENERATOR),
    STEAM_GENERATOR (Category.GENERATOR), // Use boiler upgrade to consume steam instead of burnable stuff and water, use steam upgrade to create steam instead of power
    MAGMA_GENERATOR (Category.GENERATOR),

    CHUNK_LOADER(Category.UTILITY, false);

    private final ResourceLocation texture = new ResourceLocation(AdvancedTechnology.MODID, "textures/block/machine/block_" + getName() + ".png");
    private final Category cat;
    private final boolean rotatable;

    MachineType(Category cat) { this(cat, true); }

    MachineType(Category cat, boolean rotatable)
    {
        this.cat = cat;
        this.rotatable = rotatable;
    }

    @Override
    public String getName() { return toString().toLowerCase(Locale.ENGLISH); }

    public boolean isCasing() { return this == CASING; }

    public boolean canBeRotated() { return rotatable; }

    public Category getCategory() { return cat; }

    public ResourceLocation getTexture() { return texture; }

    public enum Category
    {
        CASING,
        PRODUCER,
        GENERATOR,
        UTILITY,
        ENERGY
    }
}