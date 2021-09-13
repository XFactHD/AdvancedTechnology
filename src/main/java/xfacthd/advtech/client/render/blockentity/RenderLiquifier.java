package xfacthd.advtech.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import xfacthd.advtech.common.blockentity.machine.BlockEntityLiquifier;

public class RenderLiquifier implements BlockEntityRenderer<BlockEntityLiquifier>
{
    public RenderLiquifier(BlockEntityRendererProvider.Context context) { }

    @Override
    public void render(BlockEntityLiquifier be, float v, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay)
    {
        //TODO: find a resource pack friendly way to render the fluid being produced (look at Thermal for inspiration)
    }
}