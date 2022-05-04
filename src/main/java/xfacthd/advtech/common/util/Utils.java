package xfacthd.advtech.common.util;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;
import xfacthd.advtech.AdvancedTechnology;

public class Utils
{
    public static final AABB NULL_AABB = new AABB(0, 0, 0, 0, 0, 0);

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape)
    {
        if (from.getAxis() == Direction.Axis.Y || to.getAxis() == Direction.Axis.Y) { throw new IllegalArgumentException("Invalid Direction!"); }
        if (from == to) { return shape; }

        VoxelShape[] buffer = new VoxelShape[] { shape, Shapes.empty() };

        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++)
        {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(
                    buffer[1],
                    Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)
            ));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    public static JsonObject writeFluidStackToJson(FluidStack stack)
    {
        JsonObject json = new JsonObject();

        //noinspection ConstantConditions
        json.addProperty("name", stack.getFluid().getRegistryName().toString());
        json.addProperty("amount", stack.getAmount());

        return json;
    }

    public static FluidStack readFluidStackFromJson(JsonObject json)
    {
        ResourceLocation fluidName = new ResourceLocation(json.get("name").getAsString());
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidName);
        if (fluid == null) { throw new IllegalArgumentException("Invalid fluid: " + fluidName); }

        int amount = json.get("amount").getAsInt();
        return new FluidStack(fluid, amount);
    }

    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createBlockEntityTicker(
            BlockEntityType<A> type, BlockEntityType<E> actualType, BlockEntityTicker<? super E> ticker
    )
    {
        return actualType == type ? (BlockEntityTicker<A>)ticker : null;
    }

    public static ResourceLocation modLocation(String name) { return new ResourceLocation(AdvancedTechnology.MODID, name); }

    public static boolean tagContains(TagKey<Block> tag, Block item)
    {
        ITagManager<Block> tagManager = ForgeRegistries.BLOCKS.tags();
        Preconditions.checkNotNull(tagManager, "ForgeRegistries.ITEMS is missing its ITagManager?!");
        return tagManager.getTag(tag).contains(item);
    }

    public static boolean tagContains(TagKey<Item> tag, Item item)
    {
        ITagManager<Item> tagManager = ForgeRegistries.ITEMS.tags();
        Preconditions.checkNotNull(tagManager, "ForgeRegistries.ITEMS is missing its ITagManager?!");
        return tagManager.getTag(tag).contains(item);
    }
}