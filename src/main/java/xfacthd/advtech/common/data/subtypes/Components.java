package xfacthd.advtech.common.data.subtypes;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum Components implements IStringSerializable
{
    TRANSMISSION_COIL,
    RECEPTION_COIL,
    ELECTRIC_MOTOR,
    ELECTRIC_GENERATOR;

    @Override
    public String getName() { return toString().toLowerCase(Locale.ENGLISH); }
}