package xfacthd.advtech.common.data.subtypes;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum Materials implements IStringSerializable
{
    //NAME       COLOR       METAL  ORE    BLOCK  DUST   INGOT  NUGGET GEAR   PLATE
    IRON        (0xB6B6B6,  true, false, false,  true, false, false,  true,  true),
    GOLD        (0xDEDE00,  true, false, false,  true, false, false,  true,  true),
    COPPER      (0xB4713D,  true,  true,  true,  true,  true,  true,  true,  true),
    TIN         (0xE8E8E8,  true,  true,  true,  true,  true,  true,  true,  true),
    SILVER      (0xDBF0F3,  true,  true,  true,  true,  true,  true,  true,  true),
    LEAD        (0x818EBE,  true,  true,  true,  true,  true,  true,  true,  true),
    ALUMINUM    (0xCADBDC,  true,  true,  true,  true,  true,  true,  true,  true),
    NICKEL      (0xD3D8EC,  true,  true,  true,  true,  true,  true,  true,  true),
    PLATINUM    (0xB3E9F7,  true,  true,  true,  true,  true,  true,  true,  true),

    STEEL       (0xA4A4A4,  true, false,  true,  true,  true,  true,  true,  true),
    ELECTRUM    (0xF3E656,  true, false,  true,  true,  true,  true,  true,  true),
    BRONZE      (0xC89042,  true, false,  true,  true,  true,  true,  true,  true),
    CONSTANTAN  (0xE7B165,  true, false,  true,  true,  true,  true,  true,  true),

    COAL        (0x2B2B2B, false, false, false,  true, false, false, false, false),
    CHARCOAL    (0x37342B, false, false,  true,  true, false, false, false, false),
    COKE        (0x4F4F4F, false, false,  true,  true,  true, false, false, false),
    SULFUR      (0x9FA100, false,  true,  true,  true, false, false, false, false),
    OBSIDIAN    (0x8500CB, false, false, false,  true, false, false, false, false);

    private final int color;
    private final boolean isMetal;
    private final boolean hasOre;
    private final boolean hasBlock;
    private final boolean hasPowder;
    private final boolean hasIngot;
    private final boolean hasNugget;
    private final boolean hasGear;
    private final boolean hasPlate;

    Materials(int color, boolean isMetal, boolean hasOre, boolean hasBlock, boolean hasPowder, boolean hasIngot, boolean hasNugget, boolean hasGear, boolean hasPlate)
    {
        this.color = color;
        this.isMetal = isMetal;
        this.hasOre = hasOre;
        this.hasBlock = hasBlock;
        this.hasPowder = hasPowder;
        this.hasIngot = hasIngot;
        this.hasNugget = hasNugget;
        this.hasGear = hasGear;
        this.hasPlate = hasPlate;
    }

    public int getTintColor() { return color; }

    public boolean isMetal() { return isMetal; }

    public boolean hasOre() { return hasOre; }

    public boolean hasBlock() { return hasBlock; }

    public boolean hasPowder() { return hasPowder; }

    public boolean hasIngot() { return hasIngot; }

    public boolean hasNugget() { return hasNugget; }

    public boolean hasGear() { return hasGear; }

    public boolean hasPlate() { return hasPlate; }

    @Override
    public String getName() { return toString().toLowerCase(Locale.ENGLISH); }
}