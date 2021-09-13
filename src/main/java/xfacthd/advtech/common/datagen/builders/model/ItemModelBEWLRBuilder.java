package xfacthd.advtech.common.datagen.builders.model;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.advtech.client.util.BEWLRModelLoader;

public class ItemModelBEWLRBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
{
    private ModelBuilder<?> innerModel = null;

    public ItemModelBEWLRBuilder(T parent, ExistingFileHelper fileHelper)
    {
        super(BEWLRModelLoader.LOADER_ID, parent, fileHelper);

    }

    public ItemModelBEWLRBuilder<T> model(ModelBuilder<?> model)
    {
        innerModel = model;
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json)
    {
        Preconditions.checkNotNull(innerModel, "Contained model must be set!");

        json = super.toJson(json);
        json.add("model", innerModel.toJson());
        return json;
    }
}