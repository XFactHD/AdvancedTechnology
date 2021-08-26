package xfacthd.advtech.client.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.model.TransformationHelper;

import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public final class BakedModelProxy implements IBakedModel
{
    private final IBakedModel original;
    private ItemCameraTransforms.TransformType transform = null;

    public BakedModelProxy(IBakedModel original) { this.original = original; }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand)
    {
        return original.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() { return original.isAmbientOcclusion(); }

    @Override
    public boolean isGui3d() { return original.isGui3d(); }

    @Override
    public boolean func_230044_c_() { return original.func_230044_c_(); }

    @Override
    public boolean isBuiltInRenderer() { return true; }

    @Override
    public TextureAtlasSprite getParticleTexture() { return original.getParticleTexture(); }

    @Override
    public ItemOverrideList getOverrides() { return original.getOverrides(); }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side,Random rand, IModelData extraData)
    {
        return original.getQuads(state, side, rand, extraData);
    }

    @Override
    public boolean isAmbientOcclusion(BlockState state) { return original.isAmbientOcclusion(state); }

    @Override
    public boolean doesHandlePerspectives() { return original.doesHandlePerspectives(); }

    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType type, MatrixStack stack)
    {
        transform = type;

        TransformationMatrix tr = TransformationHelper.toTransformation(getItemCameraTransforms().getTransform(type));
        if(!tr.isIdentity()) {
            tr.push(stack);
        }

        return this;
    }

    @Override
    public IModelData getModelData(ILightReader world, BlockPos pos, BlockState state, IModelData tileData)
    {
        return original.getModelData(world, pos, state, tileData);
    }

    @Override
    public TextureAtlasSprite getParticleTexture(IModelData data) { return original.getParticleTexture(data); }

    public IBakedModel getOriginal() { return original; }

    public ItemCameraTransforms.TransformType getTransform() { return transform; }
}