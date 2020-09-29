package xfacthd.advtech.common.item.storage;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import xfacthd.advtech.client.gui.ScreenMachine;
import xfacthd.advtech.client.render.ister.RenderItemFluidTank;
import xfacthd.advtech.common.block.storage.BlockFluidTank;

import java.util.List;

public class BlockItemFluidTank extends BlockItem
{
    public BlockItemFluidTank(BlockFluidTank block, Properties props)
    {
        super(block, props.setISTER(() -> RenderItemFluidTank::new));
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        super.addInformation(stack, world, tooltip, flag);

        CompoundNBT nbt = stack.getChildTag("BlockEntityTag");
        if (nbt != null)
        {
            CompoundNBT content = nbt.getCompound("content");
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(content);

            ITextComponent fluidName = fluid.isEmpty() ? ScreenMachine.TANK_EMPTY : fluid.getDisplayName();
            int amount = fluid.getAmount();
            int capacity = content.getInt("capacity");

            tooltip.add(fluidName);
            tooltip.add(new StringTextComponent(amount + " mB / " + capacity + " mB"));
        }
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