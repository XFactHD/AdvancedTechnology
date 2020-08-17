package xfacthd.advtech.client.gui.tabs;

import com.google.common.collect.Maps;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.gui.energy.ScreenEnergyCube;
import xfacthd.advtech.client.util.TextureDrawer;
import xfacthd.advtech.client.util.Utils;
import xfacthd.advtech.common.data.states.Side;

import java.util.EnumMap;
import java.util.Map;

public class TabEnergyPort extends AbstractTab<ScreenEnergyCube>
{
    public static final ITextComponent TITLE = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".tab.machine_port");
    private static final ResourceLocation ICON_TEXTURE = Utils.modLocation("textures/gui/tabs/icon_port_setting.png");
    private static final ResourceLocation GRID_TEXTURE = Utils.modLocation("textures/gui/tabs/energy_port_setting.png");
    private static final ResourceLocation PORT_TEXTURE = Utils.modLocation("textures/block/energy/block_energy_cube_port.png");
    private static final ResourceLocation BAR_TEXTURE = Utils.modLocation("textures/gui/tabs/energy_port_bar_full.png");

    private static final Map<Side, Rectangle2d> PORT_COORDS;

    public TabEnergyPort(ScreenEnergyCube screen) { super(screen, TITLE); }

    @Override
    public void drawTabBackgroundLayer(int mouseX, int mouseY, float partialTicks)
    {
        mc.getTextureManager().bindTexture(GRID_TEXTURE);
        TextureDrawer.drawGuiTexture(screen, x + 21, y + 21, 62, 62, 0, 1, 0, 1);

        mc.getTextureManager().bindTexture(PORT_TEXTURE);
        for (Side side : Side.values())
        {
            Rectangle2d coords = PORT_COORDS.get(side);
            int color = screen.getContainer().getPortSetting(side).getColor();
            color = (color << 8) | 0xFF;
            TextureDrawer.drawGuiTexture(screen, x + coords.getX() + 2, y + coords.getY() + 2, 16, 16, 0, 1, 0, 1, color);
        }

        int level = screen.getContainer().getEnergyLevel();
        if (level > 0)
        {
            mc.getTextureManager().bindTexture(BAR_TEXTURE);
            TextureDrawer.drawGuiTexture(screen, x + 47, y + 57 - level, 10, level, 0, 1, 1F - (level / 10F), 1);
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