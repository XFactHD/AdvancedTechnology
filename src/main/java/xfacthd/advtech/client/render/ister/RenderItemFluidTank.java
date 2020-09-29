package xfacthd.advtech.client.render.ister;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import xfacthd.advtech.client.render.ter.RenderFluidTank;
import xfacthd.advtech.client.util.BakedModelProxy;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.item.storage.BlockItemFluidTank;

import java.util.Map;

public class RenderItemFluidTank extends ItemStackTileEntityRenderer
{
    private static ItemRenderer renderer = null;
    private static IBakedModel tankModel = null;
    private static BakedModelProxy proxyModel = null;

    @Override
    public void render(ItemStack item, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        if (renderer == null) { renderer = Minecraft.getInstance().getItemRenderer(); }

        stack.pop(); //Clear changes made by the call to ItemRenderer#renderItem() that led here
        //noinspection ConstantConditions
        boolean leftHand = Minecraft.getInstance().player.getHeldItemOffhand() == item;
        renderer.renderItem(item, proxyModel.getTransform(), leftHand, stack, buffer, combinedLight, combinedOverlay, tankModel);
        stack.push(); //Push to ensure correct stack size

        FluidStack fluid = BlockItemFluidTank.getContents(item);
        float height = BlockItemFluidTank.getFluidHeight(item);
        if (!fluid.isEmpty())
        {
            tankModel.handlePerspective(proxyModel.getTransform(), stack);
            stack.translate(-.5, -.5, -.5);
            RenderFluidTank.renderContents(fluid, height, stack, buffer, combinedLight, combinedOverlay);
            stack.pop(); //IForgeBakedModel#handlePerspective() does the associated MatrixStack#push
        }
    }

    public static void handleModel(Map<ResourceLocation, IBakedModel> modelRegistry)
    {
        //noinspection ConstantConditions
        ResourceLocation location = new ModelResourceLocation(ATContent.blockFluidTank.getRegistryName(), "inventory");
        tankModel = modelRegistry.get(location);
        proxyModel = new BakedModelProxy(tankModel);
        modelRegistry.put(location, proxyModel);
    }
}