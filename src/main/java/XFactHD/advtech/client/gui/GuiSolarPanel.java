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

import XFactHD.advtech.common.blocks.energy.TileEntitySolarPanel;
import XFactHD.advtech.common.gui.ContainerSolarPanel;
import XFactHD.advtech.common.utils.Reference;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

public class GuiSolarPanel extends GuiContainerBase
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Reference.MOD_ID, "textures/gui/solar_panel.png");
    private ContainerSolarPanel container;

    public GuiSolarPanel(TileEntitySolarPanel te)
    {
        super(new ContainerSolarPanel(te));
        this.container = (ContainerSolarPanel) inventorySlots;
        setGuiSize(54, 55);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        guiLeft += 27;
        guiTop += 55;
    }

    @Override
    protected void drawContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        //Background
        bindTexture(BACKGROUND);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 126, 55);
        //Energy Bar
        drawHorizontalEnergyBar(guiLeft + 13, guiTop + 13, container.getEnergyStoredScaled());
        //Sun Indicator
        bindTexture(BACKGROUND);
        drawTexturedModalRect(guiLeft + 62, guiTop + 16, 126, container.canSeeTheSun() ? 0 : 8, 8, 8);
        //Info Text
        int generating = container.canSeeTheSun() ? container.isAdvanced() ? 100 : 25 : 0;
        drawIntegerInfoString(EnumInfoType.GENERATING, guiLeft + 13, guiTop + 33, generating, 100);
    }

    @Override
    protected void drawContainerForegroundLayer(int mouseX, int mouseY)
    {
        if (mouseX > guiLeft + 13 && mouseY > guiTop + 13 && mouseX < guiLeft + 55 && mouseY < guiTop + 27)
        {
            List<String> list = Collections.singletonList(EnumInfoType.STORED.getTranslation() + " " + container.getEnergyStored() + "/" + container.getCapacity() + " T");
            drawHoveringText(list, mouseX - guiLeft, mouseY - guiTop);
        }
    }
}