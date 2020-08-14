package xfacthd.advtech.common.data.states;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum SideAccess implements IStringSerializable
{
    NONE         (0x7C7E7E),
    INPUT_ALL    (0x0000FF),
    INPUT_MAIN   (0xA700FF),
    INPUT_SECOND (0x00A900),
    OUTPUT_ALL   (0xFF7700),
    OUTPUT_MAIN  (0xFF0000),
    OUTPUT_SECOND(0xFFFF00);

    private final int color;

    SideAccess(int color) { this.color = color; }

    @Override
    public String getName() { return toString().toLowerCase(Locale.ENGLISH); }

    public boolean isDisabled() { return this == NONE; }

    public boolean isInput() { return this == INPUT_ALL || this == INPUT_MAIN || this == INPUT_SECOND; }

    public boolean isOutput() { return this == OUTPUT_ALL || this == OUTPUT_MAIN || this == OUTPUT_SECOND; }

    public boolean overrules(SideAccess mode)
    {
        if (mode == NONE) { return true; }

        if (this == INPUT_ALL) { return mode == INPUT_MAIN || mode == INPUT_SECOND; }
        else if (this == OUTPUT_ALL) { return mode == OUTPUT_MAIN || mode == OUTPUT_SECOND; }

        return false;
    }

    public int getColor() { return color; }
}