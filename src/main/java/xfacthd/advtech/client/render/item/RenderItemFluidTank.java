package xfacthd.advtech.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import xfacthd.advtech.client.render.blockentity.RenderFluidTank;
import xfacthd.advtech.client.util.BEWLRItemModel;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.item.storage.BlockItemFluidTank;

import java.util.Map;

public class RenderItemFluidTank extends BlockEntityWithoutLevelRenderer
{
    private static ItemRenderer renderer = null;
    private static BakedModel tankModel = null;

    public RenderItemFluidTank(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet)
    {
        super(dispatcher, modelSet);
    }

    @Override
    public void renderByItem(ItemStack item, ItemTransforms.TransformType transformType, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        if (renderer == null) { renderer = Minecraft.getInstance().getItemRenderer(); }

        stack.popPose(); //Clear changes made by the call to ItemRenderer#renderItem() that led here
        //noinspection ConstantConditions
        boolean offHand = Minecraft.getInstance().player.getOffhandItem() == item;
        boolean rightMain = Minecraft.getInstance().player.getMainArm() == HumanoidArm.RIGHT;
        renderer.render(item, transformType, offHand == rightMain, stack, buffer, combinedLight, combinedOverlay, tankModel);
        stack.pushPose(); //Push to ensure correct stack size

        FluidStack fluid = BlockItemFluidTank.getContents(item);
        float height = BlockItemFluidTank.getFluidHeight(item);
        if (!fluid.isEmpty())
        {
            tankModel.handlePerspective(transformType, stack);
            stack.translate(-.5, -.5, -.5);
            RenderFluidTank.renderContents(fluid, height, stack, buffer, combinedLight, combinedOverlay);
            stack.popPose(); //IForgeBakedModel#handlePerspective() does the associated MatrixStack#push
        }
    }

    public static void handleModel(Map<ResourceLocation, BakedModel> modelRegistry)
    {
        //noinspection ConstantConditions
        ResourceLocation location = new ModelResourceLocation(ATContent.BLOCK_FLUID_TANK.get().getRegistryName(), "inventory");
        tankModel = ((BEWLRItemModel) modelRegistry.get(location)).getOriginal();
    }
}