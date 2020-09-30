package xfacthd.advtech.client.gui.storage;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xfacthd.advtech.client.gui.AdvancedScreen;
import xfacthd.advtech.client.gui.tabs.TabEnergyPort;
import xfacthd.advtech.client.util.TextureDrawer;
import xfacthd.advtech.common.container.storage.ContainerEnergyCube;

public class ScreenEnergyCube extends AdvancedScreen<ContainerEnergyCube>
{
    private static final ResourceLocation BACKGROUND = background("energy_cube");
    private static final ResourceLocation ENERGY_BAR = widget("energy_bar");

    public ScreenEnergyCube(ContainerEnergyCube container, PlayerInventory inventory, ITextComponent title)
    {
        super(container, inventory, title, 176, 104);
    }

    @Override
    protected void gatherTabs() { addTab(new TabEnergyPort(this)); }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        bindTexture(ENERGY_BAR);

        int stored = container.getStoredEnergy();
        int capacity = container.getEnergyCapacity();
        float energy = (float) stored / (float) capacity;

        float height = Math.round(energy * 60F);
        float minV = 1F - (height / 60F);
        TextureDrawer.drawGuiTexture(this, guiLeft + 82, guiTop + 22 + (60F - height), 12, height, 0, 1, minV, 1);
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY)
    {
        super.renderHoveredToolTip(mouseX, mouseY);

        if (mouseX >= guiLeft + 82 && mouseX < guiLeft + 94 && mouseY >= guiTop + 22 && mouseY < guiTop + 82)
        {
            int stored = container.getStoredEnergy();
            int capacity = container.getEnergyCapacity();
            renderTooltip(stored + " RF / " + capacity + " RF", mouseX, mouseY);
        }
    }

    @Override
    protected ResourceLocation getBackground() { return BACKGROUND; }
}