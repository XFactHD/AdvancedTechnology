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

import XFactHD.advtech.common.Content;
import XFactHD.advtech.common.blocks.light.TileEntityChemoLuminator;
import XFactHD.advtech.common.gui.ContainerChemoLuminator;
import XFactHD.advtech.common.utils.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiChemoLuminator extends GuiContainerBase
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Reference.MOD_ID, "textures/gui/chemo_luminator.png");
    private EntityPlayer player;
    private ContainerChemoLuminator container;

    public GuiChemoLuminator(EntityPlayer player, TileEntityChemoLuminator te)
    {
        super(new ContainerChemoLuminator(player, te));
        container = (ContainerChemoLuminator) inventorySlots;
        setGuiSize(75, 75);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        guiLeft += 51;
        guiTop += 45;
    }

    @Override
    protected void drawContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        bindTexture(BACKGROUND);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 75, 75);
        drawFluidTank(guiLeft + 14, guiTop + 7, container.getAmountHydrogenPeroxide(), 5000, Content.fluidHydrogenPeroxide);
        drawFluidTank(guiLeft + 43, guiTop + 7, container.getAmountOxalAcid(), 5000, Content.fluidOxalAcid);
    }

    @Override
    protected void drawContainerForegroundLayer(int mouseX, int mouseY)
    {
        if (mouseX >= guiLeft + 14 && mouseX <= guiLeft + 32 && mouseY >= guiTop + 7 && mouseY <= guiTop + 68)
        {
            drawHoveringFluidTankInfo(mouseX - guiLeft, mouseY - guiTop, container.getAmountHydrogenPeroxide(), 5000, Content.fluidHydrogenPeroxide);
        }
        else if (mouseX >= guiLeft + 43 && mouseX <= guiLeft + 61 && mouseY >= guiTop + 7 && mouseY <= guiTop + 68)
        {
            drawHoveringFluidTankInfo(mouseX - guiLeft, mouseY - guiTop, container.getAmountOxalAcid(), 5000, Content.fluidOxalAcid);
        }
    }
}