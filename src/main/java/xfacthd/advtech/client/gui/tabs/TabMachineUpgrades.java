package xfacthd.advtech.client.gui.tabs;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.gui.ScreenMachine;

public class TabMachineUpgrades extends AbstractTab<ScreenMachine<?>> //TODO: implement
{
    public static final ITextComponent TITLE = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".tab.machine_upgrades");
    private static final ResourceLocation ICON_TEXTURE = new ResourceLocation(AdvancedTechnology.MODID, "textures/gui/tabs/icon_upgrades.png");

    public TabMachineUpgrades(ScreenMachine<?> screen) { super(screen, TITLE); }

    @Override
    public void drawTabBackgroundLayer(int mouseX, int mouseY, float partialTicks)
    {

    }

    @Override
    protected int getOpenHeight() { return 90; }

    @Override
    protected ResourceLocation getIconLocation() { return ICON_TEXTURE; }
}