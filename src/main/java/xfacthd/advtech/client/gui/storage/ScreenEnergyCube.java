package xfacthd.advtech.client.gui.storage;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import xfacthd.advtech.client.gui.AdvancedScreen;
import xfacthd.advtech.client.gui.tabs.TabEnergyPort;
import xfacthd.advtech.client.util.TextureDrawer;
import xfacthd.advtech.common.menu.storage.ContainerMenuEnergyCube;

public class ScreenEnergyCube extends AdvancedScreen<ContainerMenuEnergyCube>
{
    private static final ResourceLocation BACKGROUND = background("energy_cube");
    private static final ResourceLocation ENERGY_BAR = widget("energy_bar");

    public ScreenEnergyCube(ContainerMenuEnergyCube container, Inventory inventory, Component title)
    {
        super(container, inventory, title, 176, 104);
        renderInvTitle = false;
    }

    @Override
    protected void gatherTabs() { addTab(new TabEnergyPort(this)); }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(poseStack, partialTicks, mouseX, mouseY);

        RenderSystem.setShaderTexture(0, ENERGY_BAR);

        int stored = menu.getStoredEnergy();
        int capacity = menu.getEnergyCapacity();
        float energy = (float) stored / (float) capacity;

        float height = Math.round(energy * 60F);
        float minV = 1F - (height / 60F);
        TextureDrawer.drawGuiTexture(this, leftPos + 82, topPos + 22 + (60F - height), 12, height, 0, 1, minV, 1);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY)
    {
        super.renderLabels(poseStack, mouseX, mouseY);

        if (mouseX >= leftPos + 82 && mouseX < leftPos + 94 && mouseY >= topPos + 22 && mouseY < topPos + 82)
        {
            int stored = menu.getStoredEnergy();
            int capacity = menu.getEnergyCapacity();
            renderTooltip(poseStack, new TextComponent(stored + " RF / " + capacity + " RF"), mouseX - leftPos, mouseY - topPos);
        }
    }

    @Override
    protected ResourceLocation getBackground() { return BACKGROUND; }
}