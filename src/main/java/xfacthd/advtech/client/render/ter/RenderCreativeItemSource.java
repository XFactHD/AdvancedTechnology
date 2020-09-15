package xfacthd.advtech.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import xfacthd.advtech.common.tileentity.debug.TileEntityCreativeItemSource;

public class RenderCreativeItemSource extends TileEntityRenderer<TileEntityCreativeItemSource>
{
    private final ItemRenderer renderer;

    public RenderCreativeItemSource(TileEntityRendererDispatcher dispatcher)
    {
        super(dispatcher);
        renderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(TileEntityCreativeItemSource te, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        ItemStack item = te.getItem();
        if (item.isEmpty()) { return; }

        stack.push();

        //noinspection ConstantConditions
        float rot = ((te.getWorld().getGameTime() + partialTicks) % 180F) * 2;

        stack.translate(.5, 0, .5);
        stack.rotate(Vector3f.YP.rotationDegrees(rot));

        stack.translate(0, 1.4, 0);

        renderer.renderItem(item, ItemCameraTransforms.TransformType.GROUND, 240, combinedOverlay, stack, buffer);

        stack.pop();
    }
}