package xfacthd.advtech.client.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xfacthd.advtech.client.util.TextureDrawer;
import xfacthd.advtech.common.container.ContainerMachine;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;

import java.util.function.Predicate;

public abstract class ScreenMachine<C extends ContainerMachine<?, ?>> extends AdvancedScreen<C>
{
    private static final ResourceLocation ENERGY_BAR = widget("energy_bar");
    private static final ResourceLocation PROGRESS_BAR = widget("progress_bar");
    private static final ResourceLocation SLOT_OVERLAY = widget("slot_overlay");

    protected ScreenMachine(C container, PlayerInventory inventory, ITextComponent title, int xSize, int ySize)
    {
        super(container, inventory, title, xSize, ySize);
    }

    protected void drawEnergyBar(int x, int y)
    {
        bindTexture(ENERGY_BAR);

        int stored = container.getStoredEnergy();
        int capacity = container.getEnergyCapacity();
        float energy = (float) stored / (float) capacity;

        float height = Math.round(energy * 60F);
        float minV = 1F - (height / 60F);
        TextureDrawer.drawGuiTexture(this, x, y + (60F - height), 12, height, 0, 1, minV, 1);
    }

    protected void drawProgressBar(int x, int y, float progress)
    {
        bindTexture(PROGRESS_BAR);

        float width = Math.round(progress * 22F);
        float maxU = width / 22F;
        TextureDrawer.drawGuiTexture(this, x, y, width, 16, 0, maxU, 0, 1);
    }

    protected void drawWorkingIcon(int x, int y, float percentage, ResourceLocation texture)
    {
        if (!container.isActive()) { return; }

        bindTexture(texture);

        float height = Math.round(percentage * 14F);
        float minV = 1F - (height / 14F);

        TextureDrawer.drawGuiTexture(this, x, y + (14F - height), 14, height, 0, 1, minV, 1);
    }

    protected void drawSlotOverlay(int x, int y, Slot slot)
    {
        SideAccess mode = SideAccess.NONE;

        for (Side side : Side.values())
        {
            SideAccess sideMode = container.getPortSetting(side);
            if (slot.hasEffect(sideMode) && sideMode.overrules(mode))
            {
                mode = sideMode;
            }
        }

        if (mode != SideAccess.NONE)
        {
            bindTexture(SLOT_OVERLAY);
            int color = (mode.getColor() << 8) | 0xFF;
            TextureDrawer.drawGuiTexture(this, x, y, 20, 20, 0, 1, 0, 1, color);
        }
    }

    protected void drawEnergyTooltip(int mouseX, int mouseY, int xMin, int yMin)
    {
        int xMax = xMin + 12;
        int yMax = yMin + 60;

        if (mouseX >= xMin && mouseX <= xMax && mouseY >= yMin && mouseY <= yMax)
        {
            int stored = container.getStoredEnergy();
            int capacity = container.getEnergyCapacity();
            renderTooltip(stored + " RF / " + capacity + " RF", mouseX, mouseY);
        }
    }

    protected void drawProgressTooltip(int mouseX, int mouseY, int xMin, int yMin, int w, int h)
    {
        int xMax = xMin + w;
        int yMax = yMin + h;

        if (mouseX >= xMin && mouseX <= xMax && mouseY >= yMin && mouseY <= yMax)
        {
            int progress = Math.round(container.getProgress() * 100F);
            renderTooltip(progress + "%", mouseX, mouseY);
        }
    }

    protected final void bindTexture(ResourceLocation texture)
    {
        //noinspection ConstantConditions
        minecraft.getTextureManager().bindTexture(texture);
    }

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