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
import xfacthd.advtech.client.gui.storage.ScreenEnergyCube;
import xfacthd.advtech.client.util.TextureDrawer;
import xfacthd.advtech.common.util.Utils;
import xfacthd.advtech.common.data.states.Side;

import java.util.EnumMap;
import java.util.Map;

public class TabEnergyPort extends AbstractTab<ScreenEnergyCube>
{
    public static final Component TITLE = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".tab.machine_port");
    private static final ResourceLocation ICON_TEXTURE = Utils.modLocation("textures/gui/tabs/icon_port_setting.png");
    private static final ResourceLocation GRID_TEXTURE = Utils.modLocation("textures/gui/tabs/energy_port_setting.png");
    private static final ResourceLocation PORT_TEXTURE = Utils.modLocation("textures/block/storage/block_energy_cube_port.png");
    private static final ResourceLocation BAR_TEXTURE = Utils.modLocation("textures/gui/tabs/energy_port_bar_full.png");

    private static final Map<Side, Rect2i> PORT_COORDS;

    public TabEnergyPort(ScreenEnergyCube screen) { super(screen, TITLE); }

    @Override
    public void drawTabBackgroundLayer(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        RenderSystem.setShaderTexture(0, GRID_TEXTURE);
        TextureDrawer.drawGuiTexture(screen, x + 21, y + 21, 62, 62, 0, 1, 0, 1);

        RenderSystem.setShaderTexture(0, PORT_TEXTURE);
        for (Side side : Side.values())
        {
            Rect2i coords = PORT_COORDS.get(side);
            int color = screen.getMenu().getPortSetting(side).getColor();
            color = (color << 8) | 0xFF;
            TextureDrawer.drawGuiTexture(screen, x + coords.getX() + 2, y + coords.getY() + 2, 16, 16, 0, 1, 0, 1, color);
        }

        int level = screen.getMenu().getEnergyLevel();
        if (level > 0)
        {
            RenderSystem.setShaderTexture(0, BAR_TEXTURE);
            TextureDrawer.drawGuiTexture(screen, x + 47, y + 57 - level, 10, level, 0, 1, 1F - (level / 10F), 1);
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

        return pressed;
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