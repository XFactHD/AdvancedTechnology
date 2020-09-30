package xfacthd.advtech.client.render.ister;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xfacthd.advtech.client.render.ter.RenderEnergyCube;
import xfacthd.advtech.client.util.BakedModelProxy;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.item.storage.BlockItemEnergyCube;

import java.util.Map;

public class RenderItemEnergyCube extends ItemStackTileEntityRenderer
{
    private static ItemRenderer renderer = null;
    private static IBakedModel cubeModel = null;
    private static BakedModelProxy proxyModel = null;

    @Override
    public void render(ItemStack item, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        if (renderer == null) { renderer = Minecraft.getInstance().getItemRenderer(); }

        stack.pop(); //Clear changes made by the call to ItemRenderer#renderItem() that led here
        //noinspection ConstantConditions
        boolean leftHand = Minecraft.getInstance().player.getHeldItemOffhand() == item;
        renderer.renderItem(item, proxyModel.getTransform(), leftHand, stack, buffer, combinedLight, combinedOverlay, cubeModel);
        stack.push(); //Push to ensure correct stack size

        int level = BlockItemEnergyCube.getEnergyLevel(item);
        {
            cubeModel.handlePerspective(proxyModel.getTransform(), stack);
            if (proxyModel.getTransform() == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)
            {
                stack.rotate(Vector3f.YP.rotationDegrees(90));
            }
            stack.translate(-.5, -.5, -.5);
            RenderEnergyCube.renderEnergyBar(buffer, stack, level, combinedOverlay, combinedLight);
            stack.pop(); //IForgeBakedModel#handlePerspective() does the associated MatrixStack#push()
        }
    }

    public static void handleModel(Map<ResourceLocation, IBakedModel> modelRegistry)
    {
        //noinspection ConstantConditions
        ResourceLocation location = new ModelResourceLocation(ATContent.blockEnergyCube.getRegistryName(), "inventory");
        cubeModel = modelRegistry.get(location);
        proxyModel = new BakedModelProxy(cubeModel);
        modelRegistry.put(location, proxyModel);
    }
}