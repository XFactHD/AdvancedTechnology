package xfacthd.advtech.client.gui.tabs;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.gui.ScreenMachine;
import xfacthd.advtech.client.util.TextureDrawer;

public class TabMachineUpgrades extends AbstractTab<ScreenMachine<?>>
{
    public static final Component TITLE = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".tab.machine_upgrades");
    private static final ResourceLocation ICON_TEXTURE = new ResourceLocation(AdvancedTechnology.MODID, "textures/gui/tabs/icon_upgrades.png");
    private static final ResourceLocation SLOTS = new ResourceLocation(AdvancedTechnology.MODID, "textures/gui/tabs/upgrade_slots.png");

    public TabMachineUpgrades(ScreenMachine<?> screen) { super(screen, TITLE); }

    @Override
    public void drawTabBackgroundLayer(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        RenderSystem.setShaderTexture(0, SLOTS);
        TextureDrawer.drawGuiTexture(screen, x + 26, y + 22, 38, 38, 0, 1, 0, 1);
    }

    @Override
    protected int getOpenHeight() { return 70; }

    @Override
    protected ResourceLocation getIconLocation() { return ICON_TEXTURE; }
}