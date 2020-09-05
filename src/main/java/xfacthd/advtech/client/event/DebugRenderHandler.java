package xfacthd.advtech.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.advtech.AdvancedTechnology;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = AdvancedTechnology.MODID)
public class DebugRenderHandler
{
    private static final List<ChunkPos> forcedChunks = new ArrayList<>();

    @SubscribeEvent
    public static void onRenderWorldLast(final RenderWorldLastEvent event)
    {
        drawDebugChunks(event.getMatrixStack());
    }

    private static void drawDebugChunks(MatrixStack mstack)
    {
        if (mc().gameSettings.showDebugInfo && !forcedChunks.isEmpty())
        {
            mstack.push();

            Vec3d playerPos = mc().gameRenderer.getActiveRenderInfo().getProjectedView();
            mstack.translate(-playerPos.x, -playerPos.y, -playerPos.z);

            IRenderTypeBuffer.Impl buffer = mc().getRenderTypeBuffers().getBufferSource();
            IVertexBuilder builder = buffer.getBuffer(RenderType.LINES);

            forcedChunks.forEach(pos ->
                    WorldRenderer.drawBoundingBox(mstack, builder, pos.getXStart(), 0, pos.getZStart(), pos.getXEnd() + 1, 255, pos.getZEnd() + 1, 1, 0, 0, 1)
            );

            buffer.finish(RenderType.LINES);

            mstack.pop();
        }
    }



    public static void onForcedChunksPacket(LongSet chunks)
    {
        forcedChunks.clear();
        forcedChunks.addAll(chunks.stream().map(ChunkPos::new).collect(Collectors.toList()));
    }



    private static Minecraft mc() { return Minecraft.getInstance(); }
}