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

package XFactHD.advtech.client.gui;

import XFactHD.advtech.client.gui.elements.ButtonType;
import XFactHD.advtech.client.gui.elements.GuiButtonTexture;
import XFactHD.advtech.client.gui.elements.GuiTab;
import XFactHD.advtech.client.gui.elements.TabType;
import XFactHD.advtech.common.blocks.energy.TileEntityBatteryPack;
import XFactHD.advtech.common.gui.ContainerBatteryPack;
import XFactHD.advtech.common.utils.Reference;
import XFactHD.advtech.common.utils.utilClasses.SideSetting;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiBatteryPack extends GuiContainerBase
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Reference.MOD_ID, "textures/gui/battery_pack.png");
    private TileEntityBatteryPack te;
    private ContainerBatteryPack container;
    int chargeStatus = 0;

    public GuiBatteryPack(EntityPlayer player, TileEntityBatteryPack te)
    {
        super(new ContainerBatteryPack(player, te));
        container = (ContainerBatteryPack)inventorySlots;
        setGuiSize(176, 187);
        this.te = te;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        GuiButtonTexture set_io_down  = new GuiButtonTexture(0, ButtonType.SET_IO_NONE, guiLeft + 176 + 25, guiTop + 49);
        GuiButtonTexture set_io_up    = new GuiButtonTexture(1, ButtonType.SET_IO_NONE, guiLeft + 176 + 25, guiTop +  7);
        GuiButtonTexture set_io_north = new GuiButtonTexture(2, ButtonType.SET_IO_NONE, guiLeft + 176 + 25, guiTop + 28);
        GuiButtonTexture set_io_south = new GuiButtonTexture(3, ButtonType.SET_IO_NONE, guiLeft + 176 + 46, guiTop + 49);
        GuiButtonTexture set_io_west  = new GuiButtonTexture(4, ButtonType.SET_IO_NONE, guiLeft + 176 +  4, guiTop + 28);
        GuiButtonTexture set_io_east  = new GuiButtonTexture(5, ButtonType.SET_IO_NONE, guiLeft + 176 + 46, guiTop + 28);
        set_io_down.setType(getTypeForSideSetting(te.getSetting(EnumFacing.DOWN)));
        set_io_up.setType(getTypeForSideSetting(te.getSetting(EnumFacing.UP)));
        set_io_north.setType(getTypeForSideSetting(te.getSetting(EnumFacing.NORTH)));
        set_io_south.setType(getTypeForSideSetting(te.getSetting(EnumFacing.SOUTH)));
        set_io_west.setType(getTypeForSideSetting(te.getSetting(EnumFacing.WEST)));
        set_io_east.setType(getTypeForSideSetting(te.getSetting(EnumFacing.EAST)));
        set_io_down.enabled = false;
        set_io_up.enabled = false;
        set_io_north.enabled = false;
        set_io_south.enabled = false;
        set_io_west.enabled = false;
        set_io_east.enabled = false;
        set_io_down.visible = false;
        set_io_up.visible = false;
        set_io_north.visible = false;
        set_io_south.visible = false;
        set_io_west.visible = false;
        set_io_east.visible = false;
        buttonList.add(set_io_up);
        buttonList.add(set_io_down);
        buttonList.add(set_io_north);
        buttonList.add(set_io_east);
        buttonList.add(set_io_south);
        buttonList.add(set_io_west);

        GuiTab io_tab = new GuiTab(guiLeft + 173, guiTop, TabType.IO, this);
        tabList.add(io_tab);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void updateScreen()
    {
        super.updateScreen();
        for (GuiButton button : buttonList)
        {
            if (button.id > 5) { break; }
            button.visible = tabList.get(0).isOpen();
            button.enabled = tabList.get(0).isOpen();
        }
        //Charging input slot
        if (canCharge()) { chargeStatus += 1; }
        else if (!container.getSlot(39).getHasStack() || !hasSpace(container.getSlot(39).getStack())) { chargeStatus = 0; }
        if (chargeStatus > 23) { chargeStatus = 0; }
    }

    @Override
    protected void drawContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        bindTexture(BACKGROUND);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 176, 187);
        drawVerticalEnergyBar(guiLeft + 81, guiTop + 31, (float)container.getEnergyStored() / (float) container.getCapacity());
        if (chargeStatus > 0)
        {
            bindTexture(WIDGETS);
            drawTexturedModalRect(guiLeft + 143, guiTop + 41, 34, 42, 16, chargeStatus);
        }
    }

    @Override
    protected void drawContainerForegroundLayer(int mouseX, int mouseY)
    {
        for (GuiButton button : buttonList)
        {
            if (button.id > 5) { break; }
            if (button.isMouseOver() && button.enabled && button.visible)
            {
                List<String> text = new ArrayList<>();
                text.add(getDescriptionForFacing(EnumFacing.getFront(button.id)));
                text.add(getStatusTextForButtonType(((GuiButtonTexture)button).getType()));
                drawHoveringText(text, mouseX - guiLeft, mouseY - guiTop);
            }
        }
        if (mouseX >= guiLeft + 81 && mouseX <= guiLeft + 95 && mouseY >= guiTop + 31 && mouseY <= guiTop + 73)
        {
            drawHoveringStorageInfo(mouseX - guiLeft, mouseY - guiTop, container.getEnergyStored(), container.getCapacity());
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id < 6)
        {
            ((GuiButtonTexture)button).setType(((GuiButtonTexture)button).getType().getNext());
            SideSetting setting = SideSetting.NONE;
            switch (((GuiButtonTexture)button).getType())
            {
                case SET_IO_NONE: setting = SideSetting.NONE; break;
                case SET_IO_IN:   setting = SideSetting.IN; break;
                case SET_IO_OUT:  setting = SideSetting.OUT; break;
            }
            te.setSetting(EnumFacing.getFront(button.id), setting);
        }
    }

    private String getDescriptionForFacing(EnumFacing side)
    {
        switch (side)
        {
            case DOWN:  return I18n.format("desc.advech:side.down.name");
            case UP:    return I18n.format("desc.advech:side.up.name");
            case NORTH: return I18n.format("desc.advech:side.north.name");
            case SOUTH: return I18n.format("desc.advech:side.south.name");
            case WEST:  return I18n.format("desc.advech:side.west.name");
            case EAST:  return I18n.format("desc.advech:side.east.name");
            default: return "*";
        }
    }

    private String getStatusTextForButtonType(ButtonType type)
    {
        switch (type)
        {
            case SET_IO_IN:   return I18n.format("desc.advtech:status.in.name");
            case SET_IO_OUT:  return I18n.format("desc.advtech:status.out.name");
            case SET_IO_NONE: return I18n.format("desc.advtech:status.none.name");
            default: return "*";
        }
    }

    private ButtonType getTypeForSideSetting(SideSetting setting)
    {
        switch (setting)
        {
            case NONE: return ButtonType.SET_IO_NONE;
            case IN:   return ButtonType.SET_IO_IN;
            case OUT:  return ButtonType.SET_IO_OUT;
            default: return null;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean canCharge()
    {
        if (!container.getSlot(39).getHasStack()) { return false; }
        if (container.getEnergyStored() <= 0) { return false; }
        ItemStack stack = container.getSlot(39).getStack();
        return hasSpace(stack);
    }

    private boolean hasSpace(@Nonnull ItemStack stack)
    {
        if (stack.hasCapability(CapabilityEnergy.ENERGY, null))
        {
            IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
            return storage.getEnergyStored() < storage.getMaxEnergyStored();
        }
        else if (stack.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, null))
        {
            ITeslaHolder holder = stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null);
            return holder.getStoredPower() < holder.getCapacity();
        }
        return false;
    }
}