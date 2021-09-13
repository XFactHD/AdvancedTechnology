package xfacthd.advtech.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.blockentity.BlockEntityGenerator;

public abstract class ContainerMenuGenerator<T extends BlockEntityGenerator> extends ContainerMenuInventoryMachine<T>
{
    protected final LazyOptional<IFluidHandler> fluidHandler;

    protected ContainerMenuGenerator(MenuType<?> type, int id, BlockMachine block, T machine, Inventory inventory)
    {
        super(type, id, block, machine, inventory);

        fluidHandler = machine.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
    }

    public final boolean hasFluidHandler() { return fluidHandler.isPresent(); }
}