/*  Copyright (C) <2017>  <XFactHD>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses. */

package XFactHD.advtech.client.gui;

import XFactHD.advtech.client.gui.elements.GuiTab;
import XFactHD.advtech.common.utils.Reference;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class GuiContainerBase extends GuiContainer
{
    protected static final ResourceLocation WIDGETS = new ResourceLocation(Reference.MOD_ID, "textures/gui/widgets.png");

    protected List<GuiTab> tabList = new ArrayList<>();

    public GuiContainerBase(Container container)
    {
        super(container);
    }

    protected void bindTexture(ResourceLocation resLoc)
    {
        mc.getTextureManager().bindTexture(resLoc);
    }

    protected void drawHorizontalEnergyBar(int x, int y, float scale)
    {
        bindTexture(WIDGETS);
        int scaledWidth = (int)(40 * (scale));
        drawTexturedModalRect(x, y, 28,  0, 42, 14);
        drawTexturedModalRect(x, y, 28, 14, 1 + scaledWidth, 14);
    }

    protected final void drawVerticalEnergyBar(int x, int y, float scale)
    {
        bindTexture(WIDGETS);
        int scaledHeight = (int)(40 * scale);
        drawTexturedModalRect(x, y,  0, 0, 14, 42);
        int yOff = 40 - scaledHeight;
        drawTexturedModalRect(x, y + yOff, 14, yOff, 14, 41 - yOff);
    }

    protected final void drawFluidTank(int x, int y, int amount, int maxValue, Fluid fluid)
    {
        bindTexture(WIDGETS);
        int scaledHeight = (int)(59 * (1 - ((float) amount / (float) maxValue)));
        drawTexturedModalRect(x, y, 0, 42, 18, 61);
        drawFluidTexture(x + 1, y + 1, scaledHeight, 59, fluid);
        bindTexture(WIDGETS);
        drawTexturedModalRect(x + 1, y + 1, 18, 42, 16, 59);
    }

    protected final void drawHoveringStorageInfo(int x, int y, int value, int maxValue)
    {
        drawHoveringText(Collections.singletonList(EnumInfoType.STORED.getTranslation() + " " + value + "/" + maxValue + "T"), x, y);
    }

    protected final void drawHoveringFluidTankInfo(int x, int y, int amount, int max, Fluid fluid)
    {
        List<String> strings = new ArrayList<>();
        strings.add(EnumInfoType.STORED.getTranslation() + " " + amount + "/" + max + "mb");
        strings.add(EnumInfoType.FLUID.getTranslation() + " " + (fluid == null ? "desc.advtech:empty.name" : fluid.getLocalizedName(new FluidStack(fluid, amount))));
        drawHoveringText(strings, x, y);
    }

    protected final void drawFluidTexture(int xLeft, int yTop, int height, int maxHeight, Fluid fluid)
    {
        if(fluid != null)
        {
            GL11.glColor3ub((byte)(fluid.getColor() >> 16 & 255), (byte)(fluid.getColor() >> 8 & 255), (byte)(fluid.getColor() & 255));
            ResourceLocation location = new ResourceLocation(fluid.getStill().getResourceDomain(), "textures/" + fluid.getStill().getResourcePath() + ".png");
            drawTiledTexture(xLeft, yTop + maxHeight, location, 16, height, zLevel);
        }
    }

    protected final void drawTiledTexture(int x, int y, ResourceLocation location, int width, int height, float zLevel)
    {
        bindTexture(location);
        for(int tileWidth = 0; tileWidth < width; tileWidth += 16)
        {
            for(int tileHeight = 0; tileHeight < height; tileHeight += 16)
            {
                int actualWidth = Math.min(width - tileWidth, 16);
                int actualHeight = Math.min(height - tileHeight, 16);
                drawScaledTexturedModalRect(x + tileWidth, y + tileHeight, actualWidth, actualHeight, zLevel);
            }
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected final void drawScaledTexturedModalRect(int x, int y, int width, int height, float zLevel)
    {
        Tessellator tess = Tessellator.getInstance();
        VertexBuffer buffer = tess.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x,         y + height, zLevel).tex(0,                          16 * (double)height / 16.0D);
        buffer.pos(x + width, y + height, zLevel).tex(16 * (double)width / 16.0D, 16 * (double)height / 16.0D);
        buffer.pos(x + width, y,          zLevel).tex(16 * (double)width / 16.0D, 0);
        buffer.pos(x,         y,          zLevel).tex(0,                          0);
        tess.draw();
    }

    protected final void drawScaledTexturedModalRectFromSprite(int x, int y, TextureAtlasSprite sprite, int width, int height, float zLevel)
    {
        if(sprite != null)
        {
            double minU = (double)sprite.getMinU();
            double maxU = (double)sprite.getMaxU();
            double minV = (double)sprite.getMinV();
            double maxV = (double)sprite.getMaxV();
            Tessellator tess = Tessellator.getInstance();
            VertexBuffer buffer = tess.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(x,         y + height, zLevel).tex(minU,                                         minV + (maxV - minV) * (double)height / 16.0D);
            buffer.pos(x + width, y + height, zLevel).tex(minU + (maxU - minU) * (double)width / 16.0D, minV + (maxV - minV) * (double)height / 16.0D);
            buffer.pos(x + width, y,          zLevel).tex(minU + (maxU - minU) * (double)width / 16.0D, minV);
            buffer.pos(x,         y,          zLevel).tex(minU,                                         minV);
            tess.draw();
        }
    }

    protected final void drawIntegerInfoString(EnumInfoType type, int x, int y, int value, int maxValue)
    {
        this.drawIntegerInfoStringWithColor(type, x, y, value, maxValue, 14737632);
    }

    protected final void drawIntegerInfoStringWithColor(EnumInfoType type, int x, int y, int value, int maxValue, int color)
    {
        fontRendererObj.drawString(type.getTranslation(), x, y, color);
        int pos = 60;
        pos += (getStringPixelOffset(Integer.toString(maxValue)) - getStringPixelOffset(Integer.toString(value)));
        switch (type)
        {
            case GENERATING: fontRendererObj.drawString(value + " T/t", x + pos, y, color); break;
            case CONSUMING:  fontRendererObj.drawString(value + " T/t", x + pos, y, color); break;
            case STORED:     fontRendererObj.drawString(value + " T",   x + pos, y, color); break;
            case PROGRESS:   fontRendererObj.drawString(value + " %",   x + pos, y, color);
        }
    }

    protected final void drawCenteredStringWithColor(String text, int x, int y, int color)
    {
        drawCenteredString(fontRendererObj, text, x, y, color);
    }

    protected final void drawCenteredString(String text, int x, int y)
    {
        drawCenteredStringWithColor(text, x, y, 14737632);
    }

    private int getStringPixelOffset(String s)
    {
        int length = s.length();
        return length * 5 + (length - 1);
    }

    protected boolean isControlDown()
    {
        return Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
    }

    protected boolean isShiftDown()
    {
        return Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
    }

    protected boolean isAltDown()
    {
        return Keyboard.isKeyDown(Keyboard.KEY_RMENU) || Keyboard.isKeyDown(Keyboard.KEY_LMENU);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        tabList.clear();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0)
        {
            for (GuiTab tab : tabList)
            {
                if (tab.mousePressed(mouseX, mouseY))
                {
                    mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    tab.switchState();
                }
            }
        }
    }

    protected abstract void drawContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY);

    @Override
    protected final void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        drawContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        for (GuiTab tab : tabList)
        {
            tab.drawTab(mc, mouseX, mouseY);
        }
    }

    protected void drawContainerForegroundLayer(int mouseX, int mouseY){}

    @Override
    protected final void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        for (GuiTab tab : tabList)
        {
            tab.drawHoverMessage(mouseX, mouseY);
        }
        drawContainerForegroundLayer(mouseX, mouseY);
    }

    public int getGuiTop()
    {
        return guiTop;
    }

    public int getGuiLeft()
    {
        return guiLeft;
    }

    public FontRenderer getFontRenderer()
    {
        return fontRendererObj;
    }

    protected enum EnumInfoType
    {
        GENERATING("desc.advtech:generating.name"),
        CONSUMING("desc.advtech:consuming.name"),
        STORED("desc.advtech:stored.name"),
        PROGRESS("desc.advtech:progress.name"),
        FLUID("desc.advtech:fluid.name");

        private String translationKey;

        EnumInfoType(String translationKey)
        {
            this.translationKey = translationKey;
        }

        public String getTranslation()
        {
            return I18n.format(translationKey);
        }
    }
}