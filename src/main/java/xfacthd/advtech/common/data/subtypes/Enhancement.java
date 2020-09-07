package xfacthd.advtech.common.data.subtypes;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum Enhancement implements IStringSerializable
{
    RANGE(4, true);

    private final int levels;
    private final boolean single;

    Enhancement(int levels, boolean single)
    {
        this.levels = levels;
        this.single = single;
    }

    public int getLevels() { return levels; }

    public boolean singleInstance() { return single; }

    @Override
    public String getName() { return toString().toLowerCase(Locale.ENGLISH); }
}