package xfacthd.advtech.common.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.tileentity.TileEntityGenerator;

public abstract class ContainerGenerator<T extends TileEntityGenerator> extends ContainerInventoryMachine<T>
{
    protected final LazyOptional<IFluidHandler> fluidHandler;

    protected ContainerGenerator(ContainerType<?> type, int id, BlockMachine block, T machine, PlayerInventory inventory)
    {
        super(type, id, block, machine, inventory);

        fluidHandler = machine.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
    }

    public final boolean hasFluidHandler() { return fluidHandler.isPresent(); }
}