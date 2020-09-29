package xfacthd.advtech.common.util;

import com.google.gson.JsonObject;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class Utils
{
    public static final AxisAlignedBB NULL_AABB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape)
    {
        if (from.getAxis() == Direction.Axis.Y || to.getAxis() == Direction.Axis.Y) { throw new IllegalArgumentException("Invalid Direction!"); }
        if (from == to) { return shape; }

        VoxelShape[] buffer = new VoxelShape[] { shape, VoxelShapes.empty() };

        int times = (to.getHorizontalIndex() - from.getHorizontalIndex() + 4) % 4;
        for (int i = 0; i < times; i++)
        {
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.or(
                    buffer[1],
                    VoxelShapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)
            ));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
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
}