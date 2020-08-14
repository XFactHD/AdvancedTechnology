/*  Copyright (C) <2017>  <XFactHD>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses. */

package XFactHD.advtech.client.utils.event;

import XFactHD.advtech.common.blocks.energy.BlockCable;
import XFactHD.advtech.common.utils.properties.PropertyHolder;
import XFactHD.advtech.common.utils.utilClasses.ConType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("deprecation")
public class RenderBlockHighlightEventHandler
{
    private static final Map<Block, Function<IBlockState, Boolean>> customRenderedHitboxes = new HashMap<>();

    @SubscribeEvent
    @SuppressWarnings("ConstantConditions")
    public void drawBoundingBoxes(DrawBlockHighlightEvent event)
    {
        World world = Minecraft.getMinecraft().theWorld;
        BlockPos pos = event.getTarget().getBlockPos();
        if (pos == null) { return; }
        IBlockState state = world.getBlockState(pos);
        if (customRenderedHitboxes.containsKey(state.getBlock()) && customRenderedHitboxes.get(state.getBlock()).apply(state))
        {
            event.setCanceled(true);
            state = state.getBlock().getActualState(state, world, pos);
            for (AxisAlignedBB aabb : getBoundingBoxesForState(state, pos))
            {
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                                                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                                    GlStateManager.SourceFactor.ONE,
                                                    GlStateManager.DestFactor.ZERO);
                GlStateManager.glLineWidth(2.0F);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask(false);

                if (world.getWorldBorder().contains(pos))
                {
                    double x = event.getPlayer().lastTickPosX + (event.getPlayer().posX - event.getPlayer().lastTickPosX) * (double)event.getPartialTicks();
                    double y = event.getPlayer().lastTickPosY + (event.getPlayer().posY - event.getPlayer().lastTickPosY) * (double)event.getPartialTicks();
                    double z = event.getPlayer().lastTickPosZ + (event.getPlayer().posZ - event.getPlayer().lastTickPosZ) * (double)event.getPartialTicks();
                    RenderGlobal.drawSelectionBoundingBox(aabb.expandXyz(0.0020000000949949026D).offset(-x, -y, -z), 0.0F, 0.0F, 0.0F, 0.4F);
                }

                GlStateManager.depthMask(true);
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
            }
        }
    }

    private List<AxisAlignedBB> getBoundingBoxesForState(IBlockState state, BlockPos pos)
    {
        List<AxisAlignedBB> boxes = new ArrayList<>();
        AxisAlignedBB aabb_u_d = BlockCable.aabb_center;
        AxisAlignedBB aabb_n_s = BlockCable.aabb_center;
        AxisAlignedBB aabb_e_w = BlockCable.aabb_center;
        if (state.getProperties().containsKey(PropertyHolder.QUARRY_TYPE))
        {
            if (state.getValue(PropertyHolder.CONNECTED_U)) aabb_u_d = aabb_u_d.union(BlockCable.aabb_up);
            if (state.getValue(PropertyHolder.CONNECTED_D)) aabb_u_d = aabb_u_d.union(BlockCable.aabb_down);
            if (state.getValue(PropertyHolder.CONNECTED_N)) aabb_n_s = aabb_n_s.union(BlockCable.aabb_north);
            if (state.getValue(PropertyHolder.CONNECTED_E)) aabb_e_w = aabb_e_w.union(BlockCable.aabb_east);
            if (state.getValue(PropertyHolder.CONNECTED_S)) aabb_n_s = aabb_n_s.union(BlockCable.aabb_south);
            if (state.getValue(PropertyHolder.CONNECTED_W)) aabb_e_w = aabb_e_w.union(BlockCable.aabb_west);
        }
        else
        {
            if (state.getValue(PropertyHolder.CON_TYPE_U) != ConType.NONE) aabb_u_d = aabb_u_d.union(BlockCable.aabb_up);
            if (state.getValue(PropertyHolder.CON_TYPE_D) != ConType.NONE) aabb_u_d = aabb_u_d.union(BlockCable.aabb_down);
            if (state.getValue(PropertyHolder.CON_TYPE_N) != ConType.NONE) aabb_n_s = aabb_n_s.union(BlockCable.aabb_north);
            if (state.getValue(PropertyHolder.CON_TYPE_E) != ConType.NONE) aabb_e_w = aabb_e_w.union(BlockCable.aabb_east);
            if (state.getValue(PropertyHolder.CON_TYPE_S) != ConType.NONE) aabb_n_s = aabb_n_s.union(BlockCable.aabb_south);
            if (state.getValue(PropertyHolder.CON_TYPE_W) != ConType.NONE) aabb_e_w = aabb_e_w.union(BlockCable.aabb_west);
        }
        if (aabb_u_d != BlockCable.aabb_center) boxes.add(aabb_u_d.offset(pos));
        if (aabb_n_s != BlockCable.aabb_center) boxes.add(aabb_n_s.offset(pos));
        if (aabb_e_w != BlockCable.aabb_center) boxes.add(aabb_e_w.offset(pos));
        if (aabb_u_d == aabb_n_s && aabb_n_s == aabb_e_w) boxes.add(BlockCable.aabb_center.offset(pos));
        return boxes;
    }

    /**
     * @param block The Block to be added
     * @param stateValidator A function to check if a particular IBlockState needs this functionality
     * WARNING: The stateValidator does not care about values from Block#getActualState(...)!!!
     */
    public static void addBlockWithCustomHitbox(Block block, Function<IBlockState, Boolean> stateValidator)
    {
        if (customRenderedHitboxes.containsKey(block))
        {
            throw new IllegalArgumentException("Block '" + block.getRegistryName() + "' was already added!");
        }
        customRenderedHitboxes.put(block, stateValidator);
    }
}