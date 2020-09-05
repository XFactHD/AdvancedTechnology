package xfacthd.advtech.client.gui.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.gui.ScreenMachine;
import xfacthd.advtech.client.gui.tabs.TabRedstoneSettings;
import xfacthd.advtech.common.container.utility.ContainerChunkLoader;

public class ScreenChunkLoader extends ScreenMachine<ContainerChunkLoader>
{
    private static final ResourceLocation BACKGROUND = background("chunk_loader");
    public static final ITextComponent RADIUS = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".chunk_loader.radius");
    public static final ITextComponent CHUNKS = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".chunk_loader.chunks");
    public static final ITextComponent SHOW = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".chunk_loader.show");
    public static final ITextComponent HIDE = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".chunk_loader.hide");

    public ScreenChunkLoader(ContainerChunkLoader container, PlayerInventory inventory, ITextComponent title)
    {
        super(container, inventory, title, 176, 108);
    }

    @Override
    public void init(Minecraft mc, int width, int height)
    {
        super.init(mc, width, height);

        //addTab(new TabRedstoneSettings(this)); //TODO: activate when implemented

        addButton(new Button(guiLeft +  40, guiTop + 23, 20, 20, "-", btn -> container.changeRadius(false)));
        addButton(new Button(guiLeft + 116, guiTop + 23, 20, 20, "+", btn -> container.changeRadius(true)));
        addButton(new Button(guiLeft +  40, guiTop + 65, 96, 20, container.showChunks() ? HIDE.getFormattedText() : SHOW.getFormattedText(), btn ->
        {
            boolean show = container.switchShowChunks();
            btn.setMessage(show ? HIDE.getFormattedText() : SHOW.getFormattedText());
        }));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        drawEnergyBar(guiLeft + 10, guiTop + 24);

        String text = RADIUS.getFormattedText() + " " + container.getRadius();
        int textX = guiLeft + (88 - (font.getStringWidth(text) / 2));
        int textY = guiTop + 23 + 10 - (font.FONT_HEIGHT / 2);
        drawString(font, text, textX, textY, 0xFFFFFFFF);

        text = CHUNKS.getFormattedText() + " " + container.getChunkCount() + "/" + container.getMaxChunkCount();
        textX = guiLeft + (88 - (font.getStringWidth(text) / 2));
        drawString(font, text, textX, guiTop + 50, 0xFFFFFFFF);
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY)
    {
        super.renderHoveredToolTip(mouseX, mouseY);

        drawEnergyTooltip(mouseX, mouseY, guiLeft + 10, guiTop + 24);
    }

    @Override
    protected ResourceLocation getBackground() { return BACKGROUND; }
}