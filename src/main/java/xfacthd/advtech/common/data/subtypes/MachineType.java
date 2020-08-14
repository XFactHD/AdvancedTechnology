package xfacthd.advtech.common.data.subtypes;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import xfacthd.advtech.AdvancedTechnology;

import java.util.Locale;

public enum MachineType implements IStringSerializable
{
    CASING,
    ELECTRIC_FURNACE,
    CRUSHER,
    ALLOY_SMELTER,
    METAL_PRESS,
    LIQUIFIER,
    FLUID_FILLER,
    FREEZER,
    CHARGER,
    BURNER_GENERATOR,
    STEAM_GENERATOR, // Use boiler upgrade to consume steam instead of burnable stuff and water, use steam upgrade to create steam instead of power
    MAGMA_GENERATOR;

    private final ResourceLocation texture = new ResourceLocation(AdvancedTechnology.MODID, "textures/block/machine/block_" + getName() + ".png");

    @Override
    public String getName() { return toString().toLowerCase(Locale.ENGLISH); }

    public boolean isCasing() { return this == CASING; }

    public ResourceLocation getTexture() { return texture; }
}