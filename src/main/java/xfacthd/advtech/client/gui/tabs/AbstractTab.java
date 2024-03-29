package xfacthd.advtech.client.gui.tabs;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.gui.AdvancedScreen;
import xfacthd.advtech.client.util.TextureDrawer;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTab<T extends AdvancedScreen<?>> extends AbstractWidget
{
    private static final ResourceLocation BACKGROUND_OPEN = new ResourceLocation(AdvancedTechnology.MODID, "textures/gui/tabs/tab_open.png");
    private static final ResourceLocation BACKGROUND_CLOSED = new ResourceLocation(AdvancedTechnology.MODID, "textures/gui/tabs/tab_closed.png");
    public static final int SIZE_CLOSED = 20;
    private static final int SIZE_OPEN = 90;
    private static final int BUTTON_SIZE = 20;

    protected final T screen;
    private final List<AbstractWidget> widgets = new ArrayList<>();
    protected final Minecraft mc;
    private int index = -1;
    private boolean last = false;
    private boolean open = false;
    private Rect2i keepout;

    public AbstractTab(T screen, Component title)
    {
        super(0, 0, SIZE_CLOSED, SIZE_CLOSED, title);
        this.screen = screen;
        this.mc = Minecraft.getInstance();
    }

    public void init() { }

    public void setIndex(int idx)
    {
        Preconditions.checkState(index == -1, "Tab index must only be set by AdvancedScreen::addTab()!");
        this.index = idx;
    }

    public void setLast(boolean last) { this.last = last; }

    public int getIndex() { return index; }

    public final void offsetY(int offset)
    {
        this.y += offset;
        widgets.forEach(w -> w.y += offset);

        calculateKeepOut();
    }

    public boolean isOpen() { return open; }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        if (open)
        {
            float vOff = last ? .5F : 0;

            RenderSystem.setShaderTexture(0, BACKGROUND_OPEN);

            TextureDrawer.start();
            TextureDrawer.fillGuiBuffer(screen, x, y, SIZE_OPEN, 3, 0, 1, 0 + vOff, (3F / 80F) + vOff);

            int height = getOpenHeight() - 7;
            for (int h = 0; h < height; h += 33)
            {
                int segH = Math.min(height - h, 33);
                float maxV = (3F/80F) + ((33F/80F) * ((float)segH / 33F));
                TextureDrawer.fillGuiBuffer(screen, x, y + 3 + h, 90, segH, 0, 1, (3F / 80F) + vOff, maxV + vOff);
            }

            TextureDrawer.fillGuiBuffer(screen, x, y + getOpenHeight() - 4, SIZE_OPEN, 4, 0, 1, (36F / 80F) + vOff, .5F + vOff);
            TextureDrawer.end();
        }
        else
        {
            RenderSystem.setShaderTexture(0, BACKGROUND_CLOSED);
            TextureDrawer.drawGuiTexture(screen, x, y, SIZE_CLOSED, SIZE_CLOSED, 0, 1, last ? .5F : 0, last ? 1 : .5F);
        }

        RenderSystem.setShaderTexture(0, getIconLocation());
        TextureDrawer.drawGuiTexture(screen, x, y, 20, 20, 0, 1, 0, 1);

        if (open)
        {
            drawString(poseStack, mc.font, getMessage(), x + 20, y + 6, 0xFFFFFFFF);

            drawTabBackgroundLayer(poseStack, mouseX, mouseY, partialTicks);
            widgets.forEach(w -> w.render(poseStack, mouseX, mouseY, partialTicks));
        }
    }

    public abstract void drawTabBackgroundLayer(PoseStack poseStack, int mouseX, int mouseY, float partialTicks);

    public void drawForeground(PoseStack poseStack, int mouseX, int mouseY) { }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (active && visible)
        {
            boolean clicked = clicked(mouseX, mouseY);
            if (clicked)
            {
                return onClick(mouseX, mouseY, button);
            }
        }
        return false;
    }

    public boolean onClick(double mouseX, double mouseY, int button)
    {
        if (button == 0 && mouseX >= x && mouseX <= x + BUTTON_SIZE && mouseY >= y && mouseY <= y + BUTTON_SIZE)
        {
            open = !open;

            width = open ? SIZE_OPEN : SIZE_CLOSED;
            height = open ? getOpenHeight() : SIZE_CLOSED;
            calculateKeepOut();

            screen.notifyTabChanged(this, open);
            playDownSound(mc.getSoundManager());

            return true;
        }

        for (AbstractWidget w : widgets)
        {
            if (w.mouseClicked(mouseX, mouseY, button))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public final void onClick(double mouseX, double mouseY) { }

    public final int getHeightDiff() { return getOpenHeight() - SIZE_CLOSED; }

    protected abstract int getOpenHeight();

    protected abstract ResourceLocation getIconLocation();

    public final void close()
    {
        if (open)
        {
            open = false;

            width = SIZE_CLOSED;
            height = SIZE_CLOSED;
            calculateKeepOut();

            screen.notifyTabChanged(this, false);
        }
    }

    protected final void addWidget(AbstractWidget widget, int localX, int localY)
    {
        Preconditions.checkArgument(!(widget instanceof AbstractTab), "Can't add a tab to a tab!");
        widget.x = x + localX;
        widget.y = y + localY;
        widgets.add(widget);
    }

    private void calculateKeepOut() { keepout = new Rect2i(x, y, width, height); }

    public final Rect2i getKeepOut() { return keepout; }

    @Override
    public void updateNarration(NarrationElementOutput output) { }
}