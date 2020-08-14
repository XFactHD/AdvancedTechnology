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

package XFactHD.advtech.client.gui.elements;

import XFactHD.advtech.common.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

public class GuiButtonTexture extends GuiButton
{
    private static final ResourceLocation WIDGETS = new ResourceLocation(Reference.MOD_ID, "textures/gui/widgets.png");
    private ButtonType type;

    public GuiButtonTexture(int buttonId, ButtonType type, int x, int y)
    {
        super(buttonId, x, y, type.getSize(), type.getSize(), "");
        this.type = type;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            mc.getTextureManager().bindTexture(WIDGETS);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = isHovered(mouseX, mouseY);

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            Pair<Integer, Integer> texOffset = enabled ? (hovered ? type.getOffsetHover() : type.getOffsetOn()) : type.getOffsetOff();
            this.drawTexturedModalRect(this.xPosition, this.yPosition, texOffset.getLeft(), texOffset.getRight(), this.width, this.height);

            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    private boolean isHovered(int mouseX, int mouseY)
    {
        return mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    }

    public void setType(ButtonType type)
    {
        this.type = type;
    }

    public ButtonType getType()
    {
        return type;
    }
}