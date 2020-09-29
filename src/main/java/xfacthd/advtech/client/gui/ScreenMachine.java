package xfacthd.advtech.client.gui;

import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.render.ter.RenderFluidTank;
import xfacthd.advtech.client.util.TextureDrawer;
import xfacthd.advtech.common.container.ContainerMachine;

import java.util.Arrays;
import java.util.List;

public abstract class ScreenMachine<C extends ContainerMachine<?, ?>> extends AdvancedScreen<C>
{
    public static final ITextComponent SHOW_AREA = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".show_area");
    public static final ITextComponent TANK_EMPTY = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".tank_empty");
    private static final ResourceLocation ENERGY_BAR = widget("energy_bar");
    public static final ResourceLocation TANK_MARKS = widget("tank_marks");
    private static final ResourceLocation PROGRESS_BAR = widget("progress_bar");

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

    protected void drawTankContents(int x, int y, FluidStack content, int capacity)
    {
        if (!content.isEmpty())
        {
            //noinspection deprecation
            bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

            TextureAtlasSprite sprite = RenderFluidTank.getStillTexture(content);
            int height = (int) (((float) content.getAmount() / (float) capacity) * 60F);
            for (int yOff = 0; yOff < height; yOff += 16)
            {
                int texH = Math.min(16, height - yOff);
                int texY = y + 60 - yOff - texH;
                float minV = sprite.getInterpolatedV(16 - texH);
                TextureDrawer.drawGuiTexture(this, x, texY, 16, texH, sprite.getMinU(), sprite.getMaxU(), minV, sprite.getMaxV());
            }
        }

        bindTexture(TANK_MARKS);

        TextureDrawer.drawGuiTexture(this, x, y, 16, 60, 0, 1, 0, 1);
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

    protected void drawTankTooltip(int mouseX, int mouseY, int xMin, int yMin, FluidStack content, int capacity)
    {
        int xMax = xMin + 16;
        int yMax = yMin + 60;

        if (mouseX >= xMin && mouseX <= xMax && mouseY >= yMin && mouseY <= yMax)
        {
            int stored = content.getAmount();
            ITextComponent fluidName = content.isEmpty() ? TANK_EMPTY : content.getDisplayName();
            List<String> lines = Arrays.asList(fluidName.getFormattedText(), stored + " mB / " + capacity + " mB");
            renderTooltip(lines, mouseX, mouseY);
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
}