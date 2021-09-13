package xfacthd.advtech.common.data.sorting;

import java.util.Locale;

public enum MaterialCategory
{
    ORE,
    BLOCK,
    POWDER,
    INGOT,
    NUGGET,
    GEAR,
    PLATE,
    MATERIAL,
    COMPONENT;

    public String getName() { return toString().toLowerCase(Locale.ROOT); }
}