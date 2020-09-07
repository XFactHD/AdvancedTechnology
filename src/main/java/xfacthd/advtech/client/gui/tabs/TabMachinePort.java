package xfacthd.advtech.client.gui.tabs;

import com.google.common.collect.Maps;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.gui.*;
import xfacthd.advtech.client.util.TextureDrawer;
import xfacthd.advtech.common.data.states.Side;

import java.util.EnumMap;
import java.util.Map;

public class TabMachinePort extends AbstractTab<ScreenInventoryMachine<?>>
{
    public static final ITextComponent TITLE = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".tab.machine_port");
    public static final ITextComponent BTN_TOOLTIP = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".tooltip.active_output");
    private static final ResourceLocation GRID_TEXTURE = new ResourceLocation(AdvancedTechnology.MODID, "textures/gui/tabs/port_setting.png");
    private static final ResourceLocation ICON_TEXTURE = new ResourceLocation(AdvancedTechnology.MODID, "textures/gui/tabs/icon_port_setting.png");
    private static final ResourceLocation PORT_TEXTURE = new ResourceLocation(AdvancedTechnology.MODID, "textures/block/machine/block_machine_port.png");
    private static final ResourceLocation TICKBOX_TEXTURE = new ResourceLocation("textures/gui/checkbox.png");

    private static final Map<Side, Rectangle2d> PORT_COORDS;

    private final boolean canForcePush;
    private final Side frontSide;
    private final Rectangle2d frontCoords;

    public TabMachinePort(ScreenInventoryMachine<?> screen)
    {
        super(screen, TITLE);
        canForcePush = screen.getContainer().canForcePush();
        frontSide = screen.getContainer().getFrontSide();
        frontCoords = PORT_COORDS.get(frontSide);
    }

    @Override
    public void drawTabBackgroundLayer(int mouseX, int mouseY, float partialTicks)
    {
        mc.getTextureManager().bindTexture(GRID_TEXTURE);
        TextureDrawer.drawGuiTexture(screen, x + 21, y + 21, 62, 62, 0, 1, 0, 1);

        mc.getTextureManager().bindTexture(screen.getContainer().getMachineType().getTexture());
        TextureDrawer.drawGuiTexture(screen, x + frontCoords.getX() + 2, y + frontCoords.getY() + 2, 16, 16, 0, 1, 0, 1);

        mc.getTextureManager().bindTexture(PORT_TEXTURE);
        for (Side side : Side.values())
        {
            Rectangle2d coords = PORT_COORDS.get(side);
            int color = screen.getContainer().getPortSetting(side).getColor();
            color = (color << 8) | 0xFF;
            TextureDrawer.drawGuiTexture(screen, x + coords.getX() + 2, y + coords.getY() + 2, 16, 16, 0, 1, 0, 1, color);
        }

        if (canForcePush)
        {
            mc.getTextureManager().bindTexture(TICKBOX_TEXTURE);
            float vOff = screen.getContainer().shouldForceOutput() ? (20F / 64F) : 0;
            TextureDrawer.drawGuiTexture(screen, x + 68, y + 21, 15, 15, 0, 20F / 32F, vOff, (20F / 64F) + vOff);
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY)
    {
        if (isOpen() && mouseX >= x + 68 && mouseX <= x + 83 && mouseY >= y + 21 && mouseY <= y + 36)
        {
            screen.renderTooltip(BTN_TOOLTIP.getFormattedText(), mouseX - screen.getGuiLeft(), mouseY - screen.getGuiTop());
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button)
    {
        super.onClick(mouseX, mouseY, button);

        boolean pressed = false;
        Side sideHit = null;

        int localMouseX = (int)mouseX - x;
        int localMouseY = (int)mouseY - y;
        for (Side side : Side.values())
        {
            Rectangle2d coords = PORT_COORDS.get(side);
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
                    playDownSound(mc.getSoundHandler());
                    screen.getContainer().configureSide(null, 0);
                }
            }
            else
            {
                if (Screen.hasShiftDown())
                {
                    if (button == 0)
                    {
                        screen.getContainer().configureSide(sideHit, 0);
                        playDownSound(mc.getSoundHandler());
                    }
                }
                else
                {
                    screen.getContainer().configureSide(sideHit, button == 0 ? 1 : -1);
                    playDownSound(mc.getSoundHandler());
                }
            }
        }

        if (canForcePush && mouseX >= x + 68 && mouseX <= x + 83 && mouseY >= y + 21 && mouseY <= y + 36)
        {
            playDownSound(mc.getSoundHandler());
            screen.getContainer().switchActiveOutput();
        }
    }

    @Override
    protected int getOpenHeight() { return 90; }

    @Override
    protected ResourceLocation getIconLocation() { return ICON_TEXTURE; }

    static
    {
        Map<Side, Rectangle2d> coords = new EnumMap<>(Side.class);

        coords.put(Side.LEFT,   new Rectangle2d(21, 42, 20, 20));
        coords.put(Side.TOP,    new Rectangle2d(42, 21, 20, 20));
        coords.put(Side.FRONT,  new Rectangle2d(42, 42, 20, 20));
        coords.put(Side.BOTTOM, new Rectangle2d(42, 63, 20, 20));
        coords.put(Side.RIGHT,  new Rectangle2d(63, 42, 20, 20));
        coords.put(Side.BACK,   new Rectangle2d(63, 63, 20, 20));

        //noinspection UnstableApiUsage
        PORT_COORDS = Maps.immutableEnumMap(coords);
    }
}