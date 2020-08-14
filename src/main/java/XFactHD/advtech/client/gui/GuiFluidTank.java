/*  Copyright (C) <2016>  <XFactHD, DrakoAlcarus>

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

import XFactHD.advtech.common.blocks.storage.TileEntityFluidTank;
import XFactHD.advtech.common.gui.ContainerFluidTank;
import XFactHD.advtech.common.utils.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiFluidTank extends GuiContainerBase
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Reference.MOD_ID, "textures/gui/fluid_tank.png");
    private ContainerFluidTank container;

    public GuiFluidTank(EntityPlayer player, TileEntityFluidTank te)
    {
        super(new ContainerFluidTank(player, te));
        container = (ContainerFluidTank)inventorySlots;
        setGuiSize(34, 77);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        guiLeft += 72;
        guiTop += 44;
    }

    @Override
    protected void drawContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        bindTexture(BACKGROUND);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 34, 77);
        drawFluidTank(guiLeft + 8, guiTop + 8, container.getAmount(), container.getCapacity(), container.getFluid());
    }

    @Override
    protected void drawContainerForegroundLayer(int mouseX, int mouseY)
    {
        if (mouseX > guiLeft + 8 && mouseX < guiLeft + 26 && mouseY > guiTop + 8 && mouseY < guiTop + 69)
        {
            drawHoveringFluidTankInfo(mouseX - guiLeft, mouseY - guiTop, container.getAmount(), container.getCapacity(), container.getFluid());
        }
    }
}