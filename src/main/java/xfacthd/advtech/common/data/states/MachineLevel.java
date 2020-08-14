package xfacthd.advtech.common.data.states;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum MachineLevel implements IStringSerializable
{
    BASIC(0xFFFFFF),
    ADVANCED(0x00FF00),
    ELITE(0xFF0000),
    ULTIMATE(0x0000FF);

    private final int color;

    MachineLevel(int color) { this.color = color; }

    public int getColor() { return color; }

    public boolean isNextTo(MachineLevel other)
    {
        switch (this)
        {
            case BASIC: return false;
            case ADVANCED: return other == BASIC;
            case ELITE: return other == ADVANCED;
            case ULTIMATE: return other == ELITE;
            default: throw new IllegalArgumentException("Unknown MachineLevel!");
        }
    }

    @Override
    public String getName() { return toString().toLowerCase(Locale.ENGLISH); }
}