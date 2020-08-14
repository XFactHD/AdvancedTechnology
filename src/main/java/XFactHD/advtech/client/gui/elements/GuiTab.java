/*  Copyright (C) <2016>  <XFactHD>

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

import XFactHD.advtech.client.gui.GuiContainerBase;
import XFactHD.advtech.common.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;

public class GuiTab extends GuiScreen
{
    private static final ResourceLocation TABS = new ResourceLocation(Reference.MOD_ID, "textures/gui/tabs.png");

    private int x;
    private int y;
    private TabType type;
    private GuiContainerBase gui;
    private boolean open = false;

    public GuiTab(int x, int y, TabType type, GuiContainerBase gui)
    {
        this.x = x;
        this.y = y;
        this.type = type;
        this.gui = gui;
    }

    public void drawTab(Minecraft mc, int mouseX, int mouseY)
    {
        mc.getTextureManager().bindTexture(TABS);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Pair<Integer, Integer> coords = open ? type.getOffsetOpen() : type.getOffsetClosed();
        int size = open ? 76 : 20;
        drawTexturedModalRect(x, y, coords.getLeft(), coords.getRight(), size, size);
    }

    public boolean mousePressed(int mouseX, int mouseY)
    {
        return mouseX >= x + 3 && mouseX <= x +20 && mouseY >= y && mouseY <= y +20;
    }

    public void drawHoverMessage(int mouseX, int mouseY)
    {
        if (mouseX >= x + 3 && mouseX <= x +20 && mouseY >= y && mouseY <= y +20)
        {
            drawHoveringText(Collections.singletonList(I18n.format(type.getHoverMessage())), mouseX - gui.getGuiLeft(), mouseY - gui.getGuiTop(), gui.getFontRenderer());
        }
    }

    public void switchState()
    {
        open = !open;
    }

    public boolean isOpen()
    {
        return open;
    }
}