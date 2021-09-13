package xfacthd.advtech.client.gui.utility;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.gui.ScreenMachine;
import xfacthd.advtech.client.gui.tabs.TabMachineUpgrades;
import xfacthd.advtech.client.gui.tabs.TabRedstoneSettings;
import xfacthd.advtech.common.menu.utility.ContainerMenuChunkLoader;

public class ScreenChunkLoader extends ScreenMachine<ContainerMenuChunkLoader>
{
    private static final ResourceLocation BACKGROUND = background("chunk_loader");
    public static final Component RADIUS = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".chunk_loader.radius");
    public static final Component CHUNKS = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".chunk_loader.chunks");
    public static final Component SHOW = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".chunk_loader.show");
    public static final Component HIDE = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".chunk_loader.hide");

    public ScreenChunkLoader(ContainerMenuChunkLoader container, Inventory inventory, Component title)
    {
        super(container, inventory, title, 176, 108);
        renderInvTitle = false;
    }

    @Override
    public void init()
    {
        super.init();

        addRenderableWidget(new Button(leftPos +  40, topPos + 23, 20, 20, new TextComponent("-"), btn -> menu.changeRadius(false)));
        addRenderableWidget(new Button(leftPos + 116, topPos + 23, 20, 20, new TextComponent("+"), btn -> menu.changeRadius(true)));
        addRenderableWidget(new Button(leftPos +  40, topPos + 65, 96, 20, menu.showChunks() ? HIDE : SHOW, btn ->
        {
            boolean show = menu.switchShowChunks();
            btn.setMessage(show ? HIDE : SHOW);
        }));
    }

    @Override
    protected void gatherTabs()
    {
        addTab(new TabMachineUpgrades(this));
        addTab(new TabRedstoneSettings(this));
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(poseStack, partialTicks, mouseX, mouseY);

        drawEnergyBar(leftPos + 10, topPos + 24);

        //TODO: replace this mess with text components
        String text = RADIUS.getString() + " " + menu.getRadius();
        int textX = leftPos + (88 - (font.width(text) / 2));
        int textY = topPos + 23 + 10 - (font.lineHeight / 2);
        drawString(poseStack, font, text, textX, textY, 0xFFFFFFFF);

        int loaded = menu.isActive() ? menu.getChunkCount() : 0;
        text = CHUNKS.getString() + " " + loaded + "/" + menu.getMaxChunkCount();
        textX = leftPos + (88 - (font.width(text) / 2));
        drawString(poseStack, font, text, textX, topPos + 50, 0xFFFFFFFF);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY)
    {
        super.renderLabels(poseStack, mouseX, mouseY);

        drawEnergyTooltip(poseStack, mouseX, mouseY, leftPos + 10, topPos + 24);
    }

    @Override
    protected ResourceLocation getBackground() { return BACKGROUND; }
}