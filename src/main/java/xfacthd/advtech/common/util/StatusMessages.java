package xfacthd.advtech.common.util;

import net.minecraft.util.text.*;
import xfacthd.advtech.AdvancedTechnology;

public class StatusMessages
{
    public static final ITextComponent UPGRADED = new TranslationTextComponent("msg." + AdvancedTechnology.MODID + ".upgrade.success").applyTextStyle(TextFormatting.GREEN);
    public static final ITextComponent CANT_UPGRADE = new TranslationTextComponent("msg." + AdvancedTechnology.MODID + ".upgrade.failed").applyTextStyle(TextFormatting.RED);
}