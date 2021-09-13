package xfacthd.advtech.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.fluids.FluidStack;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.util.FluidSpriteCache;
import xfacthd.advtech.client.util.TextureDrawer;
import xfacthd.advtech.common.menu.ContainerMenuMachine;

import java.util.Arrays;
import java.util.List;

public abstract class ScreenMachine<M extends ContainerMenuMachine<?>> extends AdvancedScreen<M>
{
    public static final Component SHOW_AREA = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".show_area");
    public static final Component TANK_EMPTY = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".tank_empty");
    private static final ResourceLocation ENERGY_BAR = widget("energy_bar");
    public static final ResourceLocation TANK_MARKS = widget("tank_marks");
    private static final ResourceLocation PROGRESS_BAR = widget("progress_bar");

    protected ScreenMachine(M menu, Inventory inventory, Component title, int xSize, int ySize)
    {
        super(menu, inventory, title, xSize, ySize);
    }

    @Override
    protected void init()
    {
        super.init();
        inventoryLabelY = imageHeight - 94;
    }

    protected void drawEnergyBar(int x, int y)
    {
        RenderSystem.setShaderTexture(0, ENERGY_BAR);

        int stored = menu.getStoredEnergy();
        int capacity = menu.getEnergyCapacity();
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
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);

            TextureAtlasSprite sprite = FluidSpriteCache.getStillTexture(content);
            int height = (int) (((float) content.getAmount() / (float) capacity) * 60F);
            for (int yOff = 0; yOff < height; yOff += 16)
            {
                int texH = Math.min(16, height - yOff);
                int texY = y + 60 - yOff - texH;
                float minV = sprite.getV(16 - texH);
                TextureDrawer.drawGuiTexture(this, x, texY, 16, texH, sprite.getU0(), sprite.getU1(), minV, sprite.getV1());
            }
        }

        RenderSystem.setShaderTexture(0, TANK_MARKS);

        TextureDrawer.drawGuiTexture(this, x, y, 16, 60, 0, 1, 0, 1);
    }

    protected void drawProgressBar(int x, int y, float progress)
    {
        RenderSystem.setShaderTexture(0, PROGRESS_BAR);

        float width = Math.round(progress * 22F);
        float maxU = width / 22F;
        TextureDrawer.drawGuiTexture(this, x, y, width, 16, 0, maxU, 0, 1);
    }

    protected void drawWorkingIcon(int x, int y, float percentage, ResourceLocation texture)
    {
        if (!menu.isActive()) { return; }

        RenderSystem.setShaderTexture(0, texture);

        float height = Math.round(percentage * 14F);
        float minV = 1F - (height / 14F);

        TextureDrawer.drawGuiTexture(this, x, y + (14F - height), 14, height, 0, 1, minV, 1);
    }

    protected void drawEnergyTooltip(PoseStack poseStack, int mouseX, int mouseY, int xMin, int yMin)
    {
        int xMax = xMin + 12;
        int yMax = yMin + 60;

        if (mouseX >= xMin && mouseX <= xMax && mouseY >= yMin && mouseY <= yMax)
        {
            int stored = menu.getStoredEnergy();
            int capacity = menu.getEnergyCapacity();
            renderTooltip(poseStack, new TextComponent(stored + " RF / " + capacity + " RF"), mouseX - leftPos, mouseY - topPos);
        }
    }

    protected void drawTankTooltip(PoseStack poseStack, int mouseX, int mouseY, int xMin, int yMin, FluidStack content, int capacity)
    {
        int xMax = xMin + 16;
        int yMax = yMin + 60;

        if (mouseX >= xMin && mouseX <= xMax && mouseY >= yMin && mouseY <= yMax)
        {
            List<Component> lines = Arrays.asList(
                    content.isEmpty() ? TANK_EMPTY : content.getDisplayName(),
                    new TextComponent(content.getAmount() + " mB / " + capacity + " mB")
            );
            renderComponentTooltip(poseStack, lines, mouseX - leftPos, mouseY - topPos);
        }
    }

    protected void drawProgressTooltip(PoseStack poseStack, int mouseX, int mouseY, int xMin, int yMin, int w, int h)
    {
        int xMax = xMin + w;
        int yMax = yMin + h;

        if (mouseX >= xMin && mouseX <= xMax && mouseY >= yMin && mouseY <= yMax)
        {
            int progress = Math.round(menu.getProgress() * 100F);
            renderTooltip(poseStack, new TextComponent(progress + "%"), mouseX - leftPos, mouseY - topPos);
        }
    }
}