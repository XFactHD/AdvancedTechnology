package xfacthd.advtech.common.item.storage;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import xfacthd.advtech.client.render.ister.RenderItemFluidTank;
import xfacthd.advtech.common.block.storage.BlockFluidTank;

public class BlockItemFluidTank extends BlockItem
{
    public BlockItemFluidTank(BlockFluidTank block, Properties props)
    {
        super(block, props.setISTER(() -> RenderItemFluidTank::new));
    }

    public static FluidStack getContents(ItemStack stack)
    {
        CompoundNBT nbt = stack.getChildTag("BlockEntityTag");
        if (nbt != null)
        {
            nbt = nbt.getCompound("content");
            return FluidStack.loadFluidStackFromNBT(nbt);
        }
        return FluidStack.EMPTY;
    }

    public static float getFluidHeight(ItemStack stack)
    {
        CompoundNBT nbt = stack.getChildTag("BlockEntityTag");
        if (nbt != null)
        {
            nbt = nbt.getCompound("content");

            FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
            if (fluid.isEmpty()) { return 0; }

            int capacity = nbt.getInt("capacity");
            return (float)fluid.getAmount() / (float)capacity;
        }
        return 0;
    }
}