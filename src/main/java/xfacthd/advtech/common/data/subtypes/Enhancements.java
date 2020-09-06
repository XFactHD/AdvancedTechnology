package xfacthd.advtech.common.data.subtypes;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum Enhancements implements IStringSerializable
{
    RANGE(4);

    private final int levels;

    Enhancements(int levels) { this.levels = levels; }

    public int getLevels() { return levels; }

    @Override
    public String getName() { return toString().toLowerCase(Locale.ENGLISH); }
}