package xfacthd.advtech.client.util;

import net.minecraft.util.ResourceLocation;
import xfacthd.advtech.AdvancedTechnology;

public class Utils
{
    public static ResourceLocation modLocation(String name) { return new ResourceLocation(AdvancedTechnology.MODID, name); }

    public static int[] splitRGBA(int color)
    {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8)  & 0xFF;
        int b =  color        & 0xFF;
        int a = (color >> 24) & 0xFF;

        return new int[] { r, g, b, a };
    }
}