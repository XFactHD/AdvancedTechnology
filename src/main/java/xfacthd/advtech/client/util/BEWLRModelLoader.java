package xfacthd.advtech.client.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import xfacthd.advtech.AdvancedTechnology;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class BEWLRModelLoader implements IModelLoader<BEWLRModelLoader.BEWLRModelGeometry>
{
    public static final ResourceLocation LOADER_ID = new ResourceLocation(AdvancedTechnology.MODID, "bewlr-loader");

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) { }

    @Override
    public BEWLRModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
    {
        return new BEWLRModelGeometry(
                deserializationContext.deserialize(
                        GsonHelper.getAsJsonObject(modelContents, "model"),
                        BlockModel.class
                )
        );
    }

    public record BEWLRModelGeometry(UnbakedModel innerModel) implements IModelGeometry<BEWLRModelGeometry>
    {
        @Override
        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
        {
            return innerModel.getMaterials(modelGetter, missingTextureErrors);
        }

        @Override
        public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state, ItemOverrides overrides, ResourceLocation modelLocation)
        {
            BakedModel baseModel = innerModel.bake(bakery, spriteGetter, state, modelLocation);
            return new BEWLRItemModel(baseModel);
        }
    }
}