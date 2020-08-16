package xfacthd.advtech.client.gui;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.gui.tabs.AbstractTab;
import xfacthd.advtech.client.util.TextureDrawer;

import java.util.ArrayList;
import java.util.List;

public abstract class AdvancedScreen<T extends Container> extends ContainerScreen<T>
{
    private List<AbstractTab<?>> tabs;

    protected AdvancedScreen(T container, PlayerInventory inventory, ITextComponent title, int xSize, int ySize)
    {
        super(container, inventory, title);
        this.xSize = xSize;
        this.ySize = ySize;
    }

    @Override
    public void init(Minecraft mc, int width, int height)
    {
        super.init(mc, width, height);

        this.guiLeft = (this.width / 2) - (xSize / 2);
        this.guiTop = (this.height / 2) - (ySize / 2);

        tabs = new ArrayList<>();
        gatherTabs();
        tabs = ImmutableList.copyOf(tabs);
    }

    @Override
    protected final void init() { super.init(); }

    protected void gatherTabs() { }

    protected final void addTab(AbstractTab<?> tab)
    {
        tab.x = guiLeft + xSize - 3;
        tab.y = guiTop;
        tab.offsetY(AbstractTab.SIZE_CLOSED * tabs.size());

        tab.init();

        tab.setIndex(tabs.size());

        if (!tabs.isEmpty()) { tabs.get(tabs.size() - 1).setLast(false); }
        tab.setLast(true);

        tabs.add(tab);
        children.add(tab);
    }

    public List<AbstractTab<?>> getTabs() { return tabs; }

    protected abstract ResourceLocation getBackground();

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        //noinspection ConstantConditions
        minecraft.getTextureManager().bindTexture(getBackground());
        TextureDrawer.drawGuiTexture(this, guiLeft, guiTop, 0, 0, xSize, ySize);

        tabs.forEach(t -> t.render(mouseX, mouseY, partialTicks));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        tabs.forEach(t -> t.drawForeground(mouseX, mouseY));
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
    }

    protected final void bindTexture(ResourceLocation texture)
    {
        //noinspection ConstantConditions
        minecraft.getTextureManager().bindTexture(texture);
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