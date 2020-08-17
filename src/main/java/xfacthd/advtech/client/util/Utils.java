package xfacthd.advtech.client.util;

import net.minecraft.util.ResourceLocation;
import xfacthd.advtech.AdvancedTechnology;

public class Utils
{
    public static ResourceLocation modLocation(String name) { return new ResourceLocation(AdvancedTechnology.MODID, name); }
}