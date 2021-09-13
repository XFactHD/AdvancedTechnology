package xfacthd.advtech.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings({ "deprecation", "unused" })
public class ItemRenderHelper
{
    public static void renderFakeItemTransparent(ItemStack stack, int x, int y, float alpha)
    {
        renderFakeItemColored(stack, x, y, 1F, 1F, 1F, alpha);
    }

    public static void renderFakeItemColored(ItemStack stack, int x, int y, float red, float green, float blue, float alpha)
    {
        if (stack.isEmpty()) { return; }

        BakedModel model = itemRenderer().getModel(stack, null, Minecraft.getInstance().player, 0);
        itemRenderer().blitOffset += 50;
        renderItemModel(stack, x, y, red, green, blue, alpha, model);
        itemRenderer().blitOffset -= 50;
    }

    /**
     * {@link ItemRenderer::renderGuiItem} but with color
     * FIXME: alpha has no effect
     */
    public static void renderItemModel(ItemStack stack, int x, int y, float red, float green, float blue, float alpha, BakedModel model)
    {
        Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);

        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.translate(x, y, 100.0F + itemRenderer().blitOffset);
        modelViewStack.translate(8.0D, 8.0D, 0.0D);
        modelViewStack.scale(1.0F, -1.0F, 1.0F);
        modelViewStack.scale(16.0F, 16.0F, 16.0F);
        RenderSystem.applyModelViewMatrix();

        boolean flatLight = !model.usesBlockLight();
        if (flatLight)
        {
            Lighting.setupForFlatItems();
        }

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        MultiBufferSource wrappedBuffer = renderType -> new TintedVertexConsumer(buffer.getBuffer(renderType), red, green, blue, alpha);
        itemRenderer().render(stack, ItemTransforms.TransformType.GUI, false, new PoseStack(), wrappedBuffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, model);
        buffer.endBatch();

        RenderSystem.enableDepthTest();

        if (flatLight)
        {
            Lighting.setupFor3DItems();
        }

        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    private static ItemRenderer itemRenderer() { return Minecraft.getInstance().getItemRenderer(); }
}