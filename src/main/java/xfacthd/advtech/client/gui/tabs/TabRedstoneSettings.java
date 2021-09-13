package xfacthd.advtech.client.gui.tabs;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.gui.ScreenMachine;
import xfacthd.advtech.client.util.TextureDrawer;
import xfacthd.advtech.common.data.states.RedstoneMode;

public class TabRedstoneSettings extends AbstractTab<ScreenMachine<?>>
{
    public static final Component TITLE = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".tab.redstone_settings");
    private static final ResourceLocation ICON_TEXTURE = new ResourceLocation(AdvancedTechnology.MODID, "textures/gui/tabs/icon_redstone.png");
    private static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation(AdvancedTechnology.MODID, "textures/gui/widgets/redstone_mode.png");

    private final ModeButton buttonOff =  new ModeButton(screen, RedstoneMode.OFF,  this::pressButton);
    private final ModeButton buttonLow =  new ModeButton(screen, RedstoneMode.LOW,  this::pressButton);
    private final ModeButton buttonHigh = new ModeButton(screen, RedstoneMode.HIGH, this::pressButton);

    public TabRedstoneSettings(ScreenMachine<?> screen) { super(screen, TITLE); }

    @Override
    public void init()
    {
        addWidget(buttonOff,  16, 20);
        addWidget(buttonLow,  36, 20);
        addWidget(buttonHigh, 56, 20);
    }

    @Override
    public void drawTabBackgroundLayer(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) { }

    @Override
    protected int getOpenHeight() { return 50; }

    @Override
    protected ResourceLocation getIconLocation() { return ICON_TEXTURE; }

    private void pressButton(Button b)
    {
        ModeButton button = (ModeButton)b;
        if (button.isNotSelected())
        {
            screen.getMenu().setRedstoneMode(button.mode);
        }
    }

    private static class ModeButton extends Button
    {
        private static final int SIZE = 18;

        private final ScreenMachine<?> screen;
        private final RedstoneMode mode;
        private boolean selected = false;

        public ModeButton(ScreenMachine<?> screen, RedstoneMode mode, OnPress onPress)
        {
            super(0, 0, SIZE, SIZE, new TextComponent(""), onPress);
            this.screen = screen;
            this.mode = mode;
        }

        @Override
        public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
        {
            selected = screen.getMenu().getRedstoneMode() == mode;

            RenderSystem.setShaderTexture(0, BUTTON_TEXTURE);

            int offY = selected ? 2 : 0;
            if (isHovered()) { offY += 1; }

            float minU = mode.ordinal() * .25F;
            float maxU = minU + .25F;
            float minV = offY * .25F;
            float maxV = minV + .25F;

            TextureDrawer.drawGuiTexture(screen, x, y, SIZE, SIZE, minU, maxU, minV, maxV);
        }

        public boolean isNotSelected() { return !selected; }
    }
}