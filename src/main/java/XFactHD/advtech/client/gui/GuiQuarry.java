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

import XFactHD.advtech.AdvancedTechnology;
import XFactHD.advtech.client.gui.elements.ButtonType;
import XFactHD.advtech.client.gui.elements.GuiButtonTexture;
import XFactHD.advtech.common.blocks.machine.TileEntityQuarry;
import XFactHD.advtech.common.gui.ContainerQuarry;
import XFactHD.advtech.common.net.PacketHandleButtonQuarry;
import XFactHD.advtech.common.utils.Reference;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.Collections;

public class GuiQuarry extends GuiContainerBase
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/quarry.png");
    private static final String rangeNLocal = I18n.format("desc.advtech:rangenorth.name");
    private static final String rangeELocal = I18n.format("desc.advtech:rangeeast.name");
    private static final String startLocal = I18n.format("desc.advtech:start.name");
    private static final String stopLocal = I18n.format("desc.advtech:stop.name");
    private TileEntityQuarry te;
    private ContainerQuarry container;
    private int rangeNorth = 1;
    private int rangeEast = 1;

    public GuiQuarry(EntityPlayer player, TileEntityQuarry te)
    {
        super(new ContainerQuarry(player, te));
        this.te = te;
        this.container = (ContainerQuarry)inventorySlots;
        setGuiSize(190, 186);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        GuiButton addNorth = new GuiButtonTexture(0, ButtonType.PLUS,  guiLeft + 83, guiTop + 29);
        GuiButton remNorth = new GuiButtonTexture(1, ButtonType.MINUS, guiLeft + 37, guiTop + 29);
        GuiButton addEast  = new GuiButtonTexture(2, ButtonType.PLUS,  guiLeft + 83, guiTop + 57);
        GuiButton remEast  = new GuiButtonTexture(3, ButtonType.MINUS, guiLeft + 37, guiTop + 57);
        GuiButton confirm  = new GuiButtonTexture(4, ButtonType.HOOK,  guiLeft + 37, guiTop + 74);
        GuiButton cancel   = new GuiButtonTexture(5, ButtonType.CROSS, guiLeft + 83, guiTop + 74);
        buttonList.add(addNorth);
        buttonList.add(remNorth);
        buttonList.add(addEast);
        buttonList.add(remEast);
        buttonList.add(confirm);
        buttonList.add(cancel);
        rangeNorth = te.getRangeNorth();
        rangeEast = te.getRangeEast();
    }

    @Override
    protected void drawContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 190, 186);
        drawVerticalEnergyBar(guiLeft + 7, guiTop + 28, container.getEnergyScaled());
        drawCenteredString(rangeNLocal, guiLeft + 66, guiTop + 19);
        drawCenteredString(rangeELocal, guiLeft + 66, guiTop + 47);
        drawCenteredString(Integer.toString(rangeNorth), guiLeft + 66, guiTop + 31);
        drawCenteredString(Integer.toString(rangeEast), guiLeft + 66, guiTop + 59);
    }

    @Override
    protected void drawContainerForegroundLayer(int mouseX, int mouseY)
    {
        if (mouseX > guiLeft + 7 && mouseX < guiLeft + 21 && mouseY > guiTop + 28 && mouseY < guiTop + 70)
        {
            drawHoveringStorageInfo(mouseX - guiLeft, mouseY - guiTop, container.getEnergyStored(), container.getCapacity());
        }
        if (buttonList.get(4).isMouseOver())
        {
            drawHoveringText(Collections.singletonList(startLocal), mouseX - guiLeft, mouseY - guiTop);
        }
        else if (buttonList.get(5).isMouseOver())
        {
            drawHoveringText(Collections.singletonList(stopLocal), mouseX - guiLeft, mouseY - guiTop);
        }
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        buttonList.get(0).enabled = rangeNorth < 64;
        buttonList.get(1).enabled = rangeNorth > 0;
        buttonList.get(2).enabled = rangeEast < 64;
        buttonList.get(3).enabled = rangeEast > 0;
        buttonList.get(4).enabled = rangeNorth > 0 && rangeEast > 0 && !te.isRunning();
        if (rangeNorth < 0) { rangeNorth = 0; }
        if (rangeNorth > 64) { rangeNorth = 64; }
        if (rangeEast < 0) { rangeEast = 0; }
        if (rangeEast > 64) { rangeEast = 64; }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0) { rangeNorth += isControlDown() ? 10 : 1; }
        if (button.id == 1) { rangeNorth -= isControlDown() ? 10 : 1; }
        if (button.id == 2) { rangeEast += isControlDown() ? 10 : 1; }
        if (button.id == 3) { rangeEast -= isControlDown() ? 10 : 1; }
        if (button.id == 4)
        {
            AdvancedTechnology.NET.sendMessageToServer(new PacketHandleButtonQuarry(te.getPos(), 4, rangeNorth, rangeEast));
        }
        if (button.id == 5)
        {
            if (!te.isRunning())
            {
                mc.thePlayer.closeScreen();
            }
            else
            {
                AdvancedTechnology.NET.sendMessageToServer(new PacketHandleButtonQuarry(te.getPos(), 5));
            }
        }
    }
}