package xfacthd.advtech.client.gui.tabs;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.gui.ScreenMachine;
import xfacthd.advtech.client.util.TextureDrawer;

public class TabMachineUpgrades extends AbstractTab<ScreenMachine<?>>
{
    public static final ITextComponent TITLE = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".tab.machine_upgrades");
    private static final ResourceLocation ICON_TEXTURE = new ResourceLocation(AdvancedTechnology.MODID, "textures/gui/tabs/icon_upgrades.png");
    private static final ResourceLocation SLOTS = new ResourceLocation(AdvancedTechnology.MODID, "textures/gui/tabs/upgrade_slots.png");

    public TabMachineUpgrades(ScreenMachine<?> screen) { super(screen, TITLE); }

    @Override
    public void drawTabBackgroundLayer(int mouseX, int mouseY, float partialTicks)
    {
        mc.getTextureManager().bindTexture(SLOTS);
        TextureDrawer.drawGuiTexture(screen, x + 26, y + 26, 38, 38, 0, 1, 0, 1);
    }

    @Override
    protected int getOpenHeight() { return 90; }

    @Override
    protected ResourceLocation getIconLocation() { return ICON_TEXTURE; }
}