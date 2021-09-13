package xfacthd.advtech.common.data.states;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum MachineLevel implements StringRepresentable
{
    BASIC(0xFFFFFF),
    ADVANCED(0x00FF00),
    ELITE(0xFF0000),
    ULTIMATE(0x0000FF);

    private final int color;

    MachineLevel(int color) { this.color = color; }

    public int getColor() { return color; }

    public boolean isAfter(MachineLevel other)
    {
        return switch (this)
        {
            case BASIC -> false;
            case ADVANCED -> other == BASIC;
            case ELITE -> other == ADVANCED;
            case ULTIMATE -> other == ELITE;
        };
    }

    @Override
    public String getSerializedName() { return toString().toLowerCase(Locale.ENGLISH); }

    public static MachineLevel byId(int id) { return values()[id]; }
}