package xfacthd.advtech.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import xfacthd.advtech.common.blockentity.debug.BlockEntityCreativeItemSource;

public class RenderCreativeItemSource implements BlockEntityRenderer<BlockEntityCreativeItemSource>
{
    private final ItemRenderer renderer;

    public RenderCreativeItemSource(BlockEntityRendererProvider.Context context)
    {
        renderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(BlockEntityCreativeItemSource te, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        ItemStack item = te.getItem();
        if (item.isEmpty()) { return; }

        stack.pushPose();

        float rot = ((te.level().getGameTime() + partialTicks) % 180F) * 2;

        stack.translate(.5, 0, .5);
        stack.mulPose(Vector3f.YP.rotationDegrees(rot));

        stack.translate(0, 1.4, 0);

        renderer.renderStatic(item, ItemTransforms.TransformType.GROUND, 240, combinedOverlay, stack, buffer, 0);

        stack.popPose();
    }
}