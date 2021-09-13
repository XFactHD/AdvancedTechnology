package xfacthd.advtech.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
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
import net.minecraft.world.item.ItemStack;
import xfacthd.advtech.client.render.blockentity.RenderEnergyCube;
import xfacthd.advtech.client.util.BEWLRItemModel;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.item.storage.BlockItemEnergyCube;

import java.util.Map;

public class RenderItemEnergyCube extends BlockEntityWithoutLevelRenderer
{
    private static ItemRenderer renderer = null;
    private static BakedModel cubeModel = null;

    public RenderItemEnergyCube(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet)
    {
        super(dispatcher, modelSet);
    }

    @Override
    public void renderByItem(ItemStack item, ItemTransforms.TransformType transformType, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        if (renderer == null) { renderer = Minecraft.getInstance().getItemRenderer(); }

        stack.popPose(); //Clear changes made by the call to ItemRenderer#renderItem() that led here
        //noinspection ConstantConditions
        boolean leftHand = Minecraft.getInstance().player.getOffhandItem() == item;
        renderer.render(item, transformType, leftHand, stack, buffer, combinedLight, combinedOverlay, cubeModel); //FIXME: redstone parts don't render in fabulous
        stack.pushPose(); //Push to ensure correct stack size

        int level = BlockItemEnergyCube.getEnergyLevel(item);

        cubeModel.handlePerspective(transformType, stack);
        if (transformType == ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)
        {
            stack.mulPose(Vector3f.YP.rotationDegrees(90));
        }
        stack.translate(-.5, -.5, -.5);
        RenderEnergyCube.renderEnergyBar(buffer, stack, level, combinedOverlay, combinedLight);
        stack.popPose(); //IForgeBakedModel#handlePerspective() does the associated MatrixStack#push()
    }

    public static void handleModel(Map<ResourceLocation, BakedModel> modelRegistry)
    {
        //noinspection ConstantConditions
        ResourceLocation location = new ModelResourceLocation(ATContent.BLOCK_ENERGY_CUBE.get().getRegistryName(), "inventory");
        cubeModel = ((BEWLRItemModel) modelRegistry.get(location)).getOriginal();
        //modelRegistry.put(location, new BakedModelProxy(cubeModel)); //This is still necessary here because stacking loaders is not possible
    }
}