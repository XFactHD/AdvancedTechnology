package xfacthd.advtech.common.data.subtypes;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum Component implements StringRepresentable
{
    TRANSMISSION_COIL,
    RECEPTION_COIL,
    ELECTRIC_MOTOR,
    ELECTRIC_GENERATOR;

    @Override
    public String getSerializedName() { return toString().toLowerCase(Locale.ENGLISH); }
}