package xfacthd.advtech.common.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import static xfacthd.advtech.AdvancedTechnology.MODID;

public class StatusMessages
{
    public static final Component UPGRADED = new TranslatableComponent("msg." + MODID + ".upgrade.success").withStyle(ChatFormatting.GREEN);
    public static final Component CANT_UPGRADE = new TranslatableComponent("msg." + MODID + ".upgrade.failed").withStyle(ChatFormatting.RED);
    public static final Component INSTALLED = new TranslatableComponent("msg." + MODID + ".enhancement_installed").withStyle(ChatFormatting.GREEN);
    public static final Component CANT_INSTALL = new TranslatableComponent("msg." + MODID + ".enhancement_failed").withStyle(ChatFormatting.RED);
    public static final Component NO_SUPPORT = new TranslatableComponent("msg." + MODID + ".enhancement_unsupported").withStyle(ChatFormatting.RED);
}