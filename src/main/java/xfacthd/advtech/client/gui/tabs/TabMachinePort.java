package xfacthd.advtech.client.gui.tabs;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.gui.ScreenInventoryMachine;
import xfacthd.advtech.client.util.ClientUtils;
import xfacthd.advtech.client.util.TextureDrawer;
import xfacthd.advtech.common.data.states.Side;

import java.util.EnumMap;
import java.util.Map;

public class TabMachinePort extends AbstractTab<ScreenInventoryMachine<?>>
{
    public static final Component TITLE = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".tab.machine_port");
    public static final Component BTN_TOOLTIP = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".tooltip.active_output");
    private static final ResourceLocation GRID_TEXTURE = new ResourceLocation(AdvancedTechnology.MODID, "textures/gui/tabs/port_setting.png");
    private static final ResourceLocation ICON_TEXTURE = new ResourceLocation(AdvancedTechnology.MODID, "textures/gui/tabs/icon_port_setting.png");
    private static final ResourceLocation PORT_TEXTURE = new ResourceLocation(AdvancedTechnology.MODID, "textures/block/machine/block_machine_port.png");
    private static final ResourceLocation TICKBOX_TEXTURE = new ResourceLocation("textures/gui/checkbox.png");

    private static final Map<Side, Rect2i> PORT_COORDS;

    private final boolean canForcePush;
    private final Side frontSide;
    private final Rect2i frontCoords;

    public TabMachinePort(ScreenInventoryMachine<?> screen)
    {
        super(screen, TITLE);
        canForcePush = screen.getMenu().canForcePush();
        frontSide = screen.getMenu().getFrontSide();
        frontCoords = PORT_COORDS.get(frontSide);
    }

    @Override
    public void drawTabBackgroundLayer(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

        RenderSystem.setShaderTexture(0, GRID_TEXTURE);
        TextureDrawer.drawGuiTexture(screen, x + 21, y + 21, 62, 62, 0, 1, 0, 1);

        RenderSystem.setShaderTexture(0, screen.getMenu().getMachineType().getTexture());
        TextureDrawer.drawGuiTexture(screen, x + frontCoords.getX() + 2, y + frontCoords.getY() + 2, 16, 16, 0, 1, 0, 1);

        RenderSystem.setShaderTexture(0, PORT_TEXTURE);
        for (Side side : Side.values())
        {
            Rect2i coords = PORT_COORDS.get(side);
            int color = screen.getMenu().getPortSetting(side).getColor();
            color = (color << 8) | 0xFF;
            TextureDrawer.drawGuiTexture(screen, x + coords.getX() + 2, y + coords.getY() + 2, 16, 16, 0, 1, 0, 1, color);
        }

        if (canForcePush)
        {
            RenderSystem.setShaderTexture(0, TICKBOX_TEXTURE);
            float vOff = screen.getMenu().shouldForceOutput() ? (20F / 64F) : 0;
            TextureDrawer.drawGuiTexture(screen, x + 68, y + 21, 15, 15, 0, 10F / 32F, vOff, (20F / 64F) + vOff);
        }
    }

    @Override
    public void drawForeground(PoseStack poseStack, int mouseX, int mouseY)
    {
        if (isOpen() && mouseX >= x + 68 && mouseX <= x + 83 && mouseY >= y + 21 && mouseY <= y + 36)
        {
            screen.renderTooltip(poseStack, BTN_TOOLTIP, mouseX - screen.getGuiLeft(), mouseY - screen.getGuiTop());
        }
    }

    @Override
    public boolean onClick(double mouseX, double mouseY, int button)
    {
        if (super.onClick(mouseX, mouseY, button)) { return true; }

        boolean pressed = false;
        Side sideHit = null;

        int localMouseX = (int)mouseX - x;
        int localMouseY = (int)mouseY - y;
        for (Side side : Side.values())
        {
            Rect2i coords = PORT_COORDS.get(side);
            if (coords.contains(localMouseX, localMouseY))
            {
                pressed = true;
                sideHit = side;
                break;
            }
        }

        if (pressed)
        {
            if (sideHit == frontSide)
            {
                if (Screen.hasShiftDown() && button == 0)
                {
                    playDownSound(mc.getSoundManager());
                    screen.getMenu().configureSide(null, 0);
                }
            }
            else
            {
                if (Screen.hasShiftDown())
                {
                    if (button == 0)
                    {
                        screen.getMenu().configureSide(sideHit, 0);
                        playDownSound(mc.getSoundManager());
                    }
                }
                else
                {
                    screen.getMenu().configureSide(sideHit, button == 0 ? 1 : -1);
                    playDownSound(mc.getSoundManager());
                }
            }
            return true;
        }

        if (canForcePush && mouseX >= x + 68 && mouseX <= x + 83 && mouseY >= y + 21 && mouseY <= y + 36)
        {
            playDownSound(mc.getSoundManager());
            screen.getMenu().switchActiveOutput();
            return true;
        }
        return false;
    }

    @Override
    protected int getOpenHeight() { return 90; }

    @Override
    protected ResourceLocation getIconLocation() { return ICON_TEXTURE; }

    static
    {
        Map<Side, Rect2i> coords = new EnumMap<>(Side.class);

        coords.put(Side.LEFT,   new Rect2i(21, 42, 20, 20));
        coords.put(Side.TOP,    new Rect2i(42, 21, 20, 20));
        coords.put(Side.FRONT,  new Rect2i(42, 42, 20, 20));
        coords.put(Side.BOTTOM, new Rect2i(42, 63, 20, 20));
        coords.put(Side.RIGHT,  new Rect2i(63, 42, 20, 20));
        coords.put(Side.BACK,   new Rect2i(63, 63, 20, 20));

        //noinspection UnstableApiUsage
        PORT_COORDS = Maps.immutableEnumMap(coords);
    }
}