package xfacthd.advtech.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import xfacthd.advtech.common.tileentity.machine.TileEntityLiquifier;

public class RenderLiquifier extends TileEntityRenderer<TileEntityLiquifier>
{
    public RenderLiquifier(TileEntityRendererDispatcher dispatcher) { super(dispatcher); }

    @Override
    public void render(TileEntityLiquifier tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay)
    {
        //TODO: find a resource pack friendly way to render the fluid being produced (look at Thermal for inspiration)
    }
}