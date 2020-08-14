package xfacthd.advtech.client.gui.tabs;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.gui.AdvancedScreen;

public class TabRedstoneSettings extends AbstractTab<AdvancedScreen<?>> //TODO: implement
{
    public static final ITextComponent TITLE = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".tab.redstone_settings");
    private static final ResourceLocation ICON_TEXTURE = new ResourceLocation(AdvancedTechnology.MODID, "textures/gui/tabs/icon_redstone.png");

    public TabRedstoneSettings(AdvancedScreen<?> screen) { super(screen, TITLE); }

    @Override
    public void drawTabBackgroundLayer(int mouseX, int mouseY, float partialTicks)
    {

    }

    @Override
    protected int getOpenHeight() { return 50; }

    @Override
    protected ResourceLocation getIconLocation() { return ICON_TEXTURE; }
}