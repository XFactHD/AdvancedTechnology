package xfacthd.advtech.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import xfacthd.advtech.client.gui.widgets.FilterSlot;
import xfacthd.advtech.client.util.TextureDrawer;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.menu.ContainerMenuInventoryMachine;

import java.util.function.Predicate;

public abstract class ScreenInventoryMachine<M extends ContainerMenuInventoryMachine<?>> extends ScreenMachine<M>
{
    private static final ResourceLocation SLOT_OVERLAY = widget("slot_overlay");
    private static final ResourceLocation TANK_OVERLAY = widget("tank_overlay");

    protected ScreenInventoryMachine(M menu, Inventory inventory, Component title, int xSize, int ySize)
    {
        super(menu, inventory, title, xSize, ySize);
    }

    protected void drawSlotOverlay(int x, int y, Slot slot)
    {
        SideAccess mode = SideAccess.NONE;

        for (Side side : Side.values())
        {
            SideAccess sideMode = menu.getPortSetting(side);
            if (slot.hasEffect(sideMode) && sideMode.overrules(mode))
            {
                mode = sideMode;
            }
        }

        if (mode != SideAccess.NONE)
        {
            RenderSystem.setShaderTexture(0, SLOT_OVERLAY);
            int color = (mode.getColor() << 8) | 0xFF;
            TextureDrawer.drawGuiTexture(this, x, y, 20, 20, 0, 1, 0, 1, color);
        }
    }

    protected void drawTankOverlay(int x, int y, Slot slot)
    {
        SideAccess mode = SideAccess.NONE;

        for (Side side : Side.values())
        {
            SideAccess sideMode = menu.getPortSetting(side);
            if (slot.hasEffect(sideMode) && sideMode.overrules(mode))
            {
                mode = sideMode;
            }
        }

        if (mode != SideAccess.NONE)
        {
            RenderSystem.setShaderTexture(0, TANK_OVERLAY);
            int color = (mode.getColor() << 8) | 0xFF;
            TextureDrawer.drawGuiTexture(this, x, y, 20, 64, 0, 1, 0, 1, color);
        }
    }

    public void handleFilterSlotClicked(FilterSlot slot, int idx, int button) { }

    protected enum Slot
    {
        IN_MAIN((mode) -> mode == SideAccess.INPUT_ALL || mode == SideAccess.INPUT_MAIN),
        IN_SECOND((mode) -> mode == SideAccess.INPUT_ALL || mode == SideAccess.INPUT_SECOND),
        OUT_MAIN((mode) -> mode == SideAccess.OUTPUT_ALL || mode == SideAccess.OUTPUT_MAIN),
        OUT_SECOND((mode) -> mode == SideAccess.OUTPUT_ALL || mode == SideAccess.OUTPUT_SECOND);

        private final Predicate<SideAccess> effects;

        Slot(Predicate<SideAccess> effects) { this.effects = effects; }

        public boolean hasEffect(SideAccess mode) { return effects.test(mode); }
    }
}