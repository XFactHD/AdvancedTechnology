package xfacthd.advtech.common.util;

import net.minecraft.util.text.*;

import static xfacthd.advtech.AdvancedTechnology.MODID;

public class StatusMessages
{
    public static final ITextComponent UPGRADED = new TranslationTextComponent("msg." + MODID + ".upgrade.success").applyTextStyle(TextFormatting.GREEN);
    public static final ITextComponent CANT_UPGRADE = new TranslationTextComponent("msg." + MODID + ".upgrade.failed").applyTextStyle(TextFormatting.RED);
    public static final ITextComponent INSTALLED = new TranslationTextComponent("msg." + MODID + ".enhancement_installed").applyTextStyle(TextFormatting.GREEN);
    public static final ITextComponent CANT_INSTALL = new TranslationTextComponent("msg." + MODID + ".enhancement_failed").applyTextStyle(TextFormatting.RED);
    public static final ITextComponent NO_SUPPORT = new TranslationTextComponent("msg." + MODID + ".enhancement_unsupported").applyTextStyle(TextFormatting.RED);
}