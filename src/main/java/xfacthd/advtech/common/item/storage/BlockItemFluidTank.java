package xfacthd.advtech.common.item.storage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.fluids.FluidStack;
import xfacthd.advtech.client.gui.ScreenMachine;
import xfacthd.advtech.client.render.item.RenderItemFluidTank;
import xfacthd.advtech.common.block.storage.BlockFluidTank;

import java.util.List;
import java.util.function.Consumer;

public class BlockItemFluidTank extends BlockItem
{
    public BlockItemFluidTank(BlockFluidTank block, Properties props) { super(block, props);}

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag)
    {
        super.appendHoverText(stack, level, tooltip, flag);

        CompoundTag nbt = stack.getTagElement("BlockEntityTag");
        if (nbt != null)
        {
            CompoundTag content = nbt.getCompound("content");
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(content);

            Component fluidName = fluid.isEmpty() ? ScreenMachine.TANK_EMPTY : fluid.getDisplayName();
            int amount = fluid.getAmount();
            int capacity = content.getInt("capacity");

            tooltip.add(fluidName);
            tooltip.add(new TextComponent(amount + " mB / " + capacity + " mB"));
        }
    }

    public static FluidStack getContents(ItemStack stack)
    {
        CompoundTag nbt = stack.getTagElement("BlockEntityTag");
        if (nbt != null)
        {
            nbt = nbt.getCompound("content");
            return FluidStack.loadFluidStackFromNBT(nbt);
        }
        return FluidStack.EMPTY;
    }

    public static float getFluidHeight(ItemStack stack)
    {
        CompoundTag nbt = stack.getTagElement("BlockEntityTag");
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

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer)
    {
        consumer.accept(new IItemRenderProperties()
        {
            private final RenderItemFluidTank stackRenderer = Minecraft.getInstance() != null ? new RenderItemFluidTank(
                    Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                    Minecraft.getInstance().getEntityModels()
            ) : null;

            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() { return stackRenderer; }
        });
    }
}