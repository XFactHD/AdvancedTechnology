package xfacthd.advtech.client.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.geometry.ISimpleModelGeometry;

import java.util.*;
import java.util.function.Function;

public class ISTERModelLoader implements IModelLoader<ISTERModelLoader.ISTERModelGeometry>
{
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) { }

    @Override
    public ISTERModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
    {
        return new ISTERModelGeometry(ModelLoaderRegistry.VanillaProxy.Loader.INSTANCE.read(deserializationContext, modelContents));
    }

    public static class ISTERModelGeometry implements ISimpleModelGeometry<ISTERModelGeometry>
    {
        private final ModelLoaderRegistry.VanillaProxy proxy;

        public ISTERModelGeometry(ModelLoaderRegistry.VanillaProxy proxy) { this.proxy = proxy; }

        @Override
        public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation)
        {
            proxy.addQuads(owner, modelBuilder, bakery, spriteGetter, modelTransform, modelLocation);
        }

        @Override
        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
        {
            return proxy.getTextures(owner, modelGetter, missingTextureErrors);
        }

        @Override
        public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation)
        {
            IBakedModel baseModel = proxy.bake(owner, bakery, spriteGetter, modelTransform, overrides, modelLocation);
            return new BakedModelProxy(baseModel);
        }
    }
}