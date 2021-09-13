package xfacthd.advtech.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.gui.tabs.AbstractTab;
import xfacthd.advtech.client.gui.tabs.TabMachineUpgrades;
import xfacthd.advtech.client.util.TextureDrawer;
import xfacthd.advtech.common.menu.ContainerMenuMachine;

import java.util.ArrayList;
import java.util.List;

public abstract class AdvancedScreen<M extends AbstractContainerMenu> extends AbstractContainerScreen<M>
{
    private final List<AbstractTab<?>> tabs = new ArrayList<>();
    protected boolean renderScreenTitle = true;
    protected boolean renderInvTitle = true;

    protected AdvancedScreen(M menu, Inventory inventory, Component title, int xSize, int ySize)
    {
        super(menu, inventory, title);
        this.imageWidth = xSize;
        this.imageHeight = ySize;
    }

    @Override
    protected void init()
    {
        super.init();

        tabs.clear();
        gatherTabs();
    }

    protected void gatherTabs() { }

    protected final void addTab(AbstractTab<?> tab)
    {
        tab.x = leftPos + imageWidth - 3;
        tab.y = topPos;
        tab.offsetY(AbstractTab.SIZE_CLOSED * tabs.size());

        tab.init();
        tab.setIndex(tabs.size());

        if (!tabs.isEmpty())
        {
            tabs.get(tabs.size() - 1).setLast(false);
        }
        tab.setLast(true);

        tabs.add(tab);
        addRenderableWidget(tab);
    }

    public List<AbstractTab<?>> getTabs() { return tabs; }

    protected abstract ResourceLocation getBackground();

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, getBackground());
        TextureDrawer.drawGuiTexture(this, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY)
    {
        if (renderScreenTitle)
        {
            font.draw(pPoseStack, title, titleLabelX, titleLabelY, 0x404040);
        }
        if (renderInvTitle)
        {
            font.draw(pPoseStack, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040);
        }

        tabs.forEach(t -> t.drawForeground(pPoseStack, pMouseX, pMouseY));
    }

    public final void notifyTabChanged(AbstractTab<?> changedTab, boolean open)
    {
        int yOffset = open ? changedTab.getHeightDiff() : -changedTab.getHeightDiff();
        for (AbstractTab<?> tab : tabs)
        {
            if (open && tab.isOpen() && tab != changedTab)
            {
                tab.close();
            }

            if (tab.getIndex() > changedTab.getIndex())
            {
                tab.offsetY(yOffset);
            }
        }

        if (changedTab instanceof TabMachineUpgrades)
        {
            ContainerMenuMachine<?> cont = (ContainerMenuMachine<?>)menu;
            cont.switchEnhancementSlots(open);
        }
    }

    protected static ResourceLocation background(String name)
    {
        return new ResourceLocation(AdvancedTechnology.MODID, "textures/gui/" + name + ".png");
    }

    protected static ResourceLocation widget(String name)
    {
        return new ResourceLocation(AdvancedTechnology.MODID, "textures/gui/widgets/" + name + ".png");
    }
}