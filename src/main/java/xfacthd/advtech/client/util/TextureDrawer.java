package xfacthd.advtech.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class TextureDrawer
{
    private static BufferBuilder buffer;

    public static void drawTexture(float x, float y, float w, float h, float minU, float maxU, float minV, float maxV)
    {
        start();
        fillBuffer(x, y, w, h, minU, maxU, minV, maxV);
        end();
    }

    public static void drawTexture(float x, float y, float w, float h, float minU, float maxU, float minV, float maxV, int color)
    {
        startColored();
        fillBuffer(x, y, w, h, minU, maxU, minV, maxV, color);
        end();
    }

    public static void drawGuiTexture(float x, float y, float texX, float texY, float w, float h)
    {
        start();
        fillGuiBuffer(x, y, texX, texY, w, h);
        end();
    }

    public static void drawGuiTexture(float x, float y, float texX, float texY, float w, float h, int color)
    {
        startColored();
        fillGuiBuffer(x, y, texX, texY, w, h, color);
        end();
    }

    public static void start()
    {
        if (buffer != null) { throw new IllegalStateException("Last drawing operation not finished!"); }

        buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
    }

    public static void startColored()
    {
        if (buffer != null) { throw new IllegalStateException("Last drawing operation not finished!"); }

        buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
    }

    public static void fillBuffer(float x, float y, float w, float h, float minU, float maxU, float minV, float maxV)
    {
        if (buffer == null) { throw new IllegalStateException("Drawing operation not started!"); }

        buffer.pos(x,     y + h, -120).tex(minU, maxV).endVertex();
        buffer.pos(x + w, y + h, -120).tex(maxU, maxV).endVertex();
        buffer.pos(x + w, y,     -120).tex(maxU, minV).endVertex();
        buffer.pos(x,     y,     -120).tex(minU, minV).endVertex();
    }

    public static void fillBuffer(float x, float y, float w, float h, float minU, float maxU, float minV, float maxV, int color)
    {
        if (buffer == null) { throw new IllegalStateException("Drawing operation not started!"); }

        int[] colors = getRGBAArrayFromHexColor(color);
        buffer.pos(x,     y + h, -90).color(colors[0], colors[1], colors[2], colors[3]).tex(minU, maxV).endVertex();
        buffer.pos(x + w, y + h, -90).color(colors[0], colors[1], colors[2], colors[3]).tex(maxU, maxV).endVertex();
        buffer.pos(x + w, y,     -90).color(colors[0], colors[1], colors[2], colors[3]).tex(maxU, minV).endVertex();
        buffer.pos(x,     y,     -90).color(colors[0], colors[1], colors[2], colors[3]).tex(minU, minV).endVertex();
    }

    public static void fillGuiBuffer(float x, float y, float texX, float texY, float w, float h)
    {
        float minU = texX / 256F;
        float maxU = minU + (w / 256F);
        float minV = texY / 256F;
        float maxV = minV + (h / 256F);
        fillBuffer(x, y, w, h, minU, maxU, minV, maxV);
    }

    public static void fillGuiBuffer(float x, float y, float texX, float texY, float w, float h, int color)
    {
        float minU = texX / 256F;
        float maxU = minU + (w / 256F);
        float minV = texY / 256F;
        float maxV = minV + (h / 256F);
        fillBuffer(x, y, w, h, minU, maxU, minV, maxV, color);
    }

    public static void end()
    {
        if (buffer == null) { throw new IllegalStateException("Drawing operation not started!"); }

        buffer.finishDrawing();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(buffer);

        buffer = null;
    }

    private static int[] getRGBAArrayFromHexColor(int color)
    {
        int[] ints = new int[4];
        ints[0] = (color >> 24 & 255);
        ints[1] = (color >> 16 & 255);
        ints[2] = (color >>  8 & 255);
        ints[3] = (color       & 255);
        return ints;
    }
}