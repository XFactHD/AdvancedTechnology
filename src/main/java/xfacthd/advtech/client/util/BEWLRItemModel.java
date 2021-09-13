package xfacthd.advtech.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.model.TransformationHelper;

import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public record BEWLRItemModel(BakedModel original) implements BakedModel
{
    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand)
    {
        return original.getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion() { return original.useAmbientOcclusion(); }

    @Override
    public boolean isGui3d() { return original.isGui3d(); }

    @Override
    public boolean usesBlockLight() { return original.usesBlockLight(); }

    @Override
    public boolean isCustomRenderer() { return true; }

    @Override
    public TextureAtlasSprite getParticleIcon() { return original.getParticleIcon(); }

    @Override
    public ItemOverrides getOverrides() { return original.getOverrides(); }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData)
    {
        return original.getQuads(state, side, rand, extraData);
    }

    @Override
    public boolean isAmbientOcclusion(BlockState state) { return original.isAmbientOcclusion(state); }

    @Override
    public boolean doesHandlePerspectives() { return original.doesHandlePerspectives(); }

    @Override
    public BakedModel handlePerspective(ItemTransforms.TransformType type, PoseStack stack)
    {
        Transformation tr = TransformationHelper.toTransformation(getTransforms().getTransform(type));
        if (!tr.isIdentity())
        {
            tr.push(stack);
        }

        return this;
    }

    @Override
    public IModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, IModelData tileData)
    {
        return original.getModelData(level, pos, state, tileData);
    }

    public BakedModel getOriginal() { return original; }
}