package xfacthd.advtech.common.data.subtypes;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum PipeType implements IStringSerializable
{
    ;

    @Override
    public String getName() { return toString().toLowerCase(Locale.ROOT); }
}