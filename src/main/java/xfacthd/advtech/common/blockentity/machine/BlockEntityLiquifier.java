package xfacthd.advtech.common.blockentity.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.BlockEntityProducer;
import xfacthd.advtech.common.capability.fluid.MachineFluidHandler;
import xfacthd.advtech.common.capability.fluid.SidedFluidHandler;
import xfacthd.advtech.common.capability.item.MachineItemStackHandler;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.menu.machine.ContainerMenuLiquifier;
import xfacthd.advtech.common.data.recipes.LiquifierRecipe;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.util.interfaces.ITileFluidHandler;
import xfacthd.advtech.common.util.inventory.RecipeSearchInventory;

import java.util.EnumMap;
import java.util.Map;

public class BlockEntityLiquifier extends BlockEntityProducer implements ITileFluidHandler
{
    public static final Component TITLE = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".liquifier");
    private static final int BASE_CAPACITY = 50000;
    private static final int BASE_CONSUMPTION = 100;
    private static final int BASE_FLUID_CAPACITY = 5000;

    private MachineFluidHandler internalFluidHandler;
    private LazyOptional<MachineFluidHandler> lazyInternalFluidHandler = LazyOptional.empty();
    private final Map<Direction, SidedFluidHandler> fluidHandlers = new EnumMap<>(Direction.class);
    private final Map<Direction, LazyOptional<SidedFluidHandler>> lazyFluidHandlers = new EnumMap<>(Direction.class);
    private final Map<Direction, LazyOptional<IFluidHandler>> fluidNeighborCache = new EnumMap<>(Direction.class);

    private Item lastInput = Items.AIR;
    private int energy = 0;

    public BlockEntityLiquifier(BlockPos pos, BlockState state)
    {
        super(ATContent.machineEntity(MachineType.LIQUIFIER), pos, state, LiquifierRecipe.TYPE);

        for (Direction side : Direction.values())
        {
            fluidHandlers.put(side, new SidedFluidHandler(this, internalFluidHandler, side));
            lazyFluidHandlers.put(side, LazyOptional.empty());
            fluidNeighborCache.put(side, LazyOptional.empty());
        }
    }

    @Override
    public void tickInternal()
    {
        if (!level().isClientSide())
        {
            if (recipe == null && slotNotEmpty(0))
            {
                recipe = findRecipe();
            }

            if (recipe != null && hasEnoughEnergy() && canRun(progress == -1) && slotNotEmpty(0))
            {
                if (!outputFits())
                {
                    if (active) { setActive(false); }
                }
                else if (!active || progress == -1)
                {
                    if (progress == -1)
                    {
                        progress = 0;
                        energy = ((LiquifierRecipe)recipe).getEnergy();
                    }

                    if (!active)
                    {
                        setActive(true);
                    }
                }
                else if (progress >= energy)
                {
                    FluidStack result = ((LiquifierRecipe)recipe).getOutput().copy();
                    internalFluidHandler.fill(result, IFluidHandler.FluidAction.EXECUTE);

                    internalItemHandler.extractItem(0, 1, false);

                    progress = -1;
                    setCycleComplete();
                }
            }
            else if (active)
            {
                progress = -1;
                setActive(false);
            }
        }

        super.tickInternal();
    }

    @Override
    public void onLoad()
    {
        super.onLoad();

        for (Direction dir : Direction.values())
        {
            SideAccess setting = cardinalPorts.get(dir);
            if (!lazyFluidHandlers.get(dir).isPresent() && !setting.isDisabled())
            {
                lazyFluidHandlers.put(dir, LazyOptional.of(() -> fluidHandlers.get(dir)));
            }
        }

        lazyInternalFluidHandler = LazyOptional.of(() -> internalFluidHandler);
    }

    @Override
    protected void pushOutputs()
    {
        if (internalFluidHandler.getFluidInTank(0).isEmpty()) { return; }

        boolean worked = false;
        for (Direction dir : Direction.values())
        {
            SideAccess mode = cardinalPorts.get(dir);
            if (mode.isOutput())
            {
                LazyOptional<IFluidHandler> adj = getNeighboringFluidHandler(dir);
                worked |= adj.map(handler ->
                {
                    FluidStack fluid = internalFluidHandler.getFluidInTank(0);
                    FluidStack result = FluidUtil.tryFluidTransfer(handler, internalFluidHandler, fluid, true);
                    return result.getAmount() != 0;
                }).orElse(false);
                if (internalFluidHandler.getFluidInTank(0).isEmpty()) { break; }
            }
        }
        if (worked) { setChanged(); }
    }

    private LazyOptional<IFluidHandler> getNeighboringFluidHandler(Direction side)
    {
        LazyOptional<IFluidHandler> handler = fluidNeighborCache.getOrDefault(side, LazyOptional.empty());
        if (!handler.isPresent())
        {
            BlockEntity te = level().getBlockEntity(worldPosition.relative(side));
            if (te != null)
            {
                handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
                if (handler.isPresent())
                {
                    handler.addListener(h -> fluidNeighborCache.put(side, LazyOptional.empty()));
                    fluidNeighborCache.put(side, handler);
                }
            }
        }
        return handler;
    }

    @Override
    public boolean canInsert(Direction side, int slot) { return slot == 0 && cardinalPorts.get(side).isInput(); }

    @Override
    public boolean canExtract(Direction side, int slot) { return false; }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (slot == 0)
        {
            if (recipe != null)
            {
                return ((LiquifierRecipe)recipe).getInput().test(stack);
            }

            Container inv = new RecipeSearchInventory(stack);
            return findRecipeWithInv(inv, false) != null;
        }
        return false;
    }

    @Override
    protected void onSlotChangedInternal(int slot)
    {
        if (slot == 0)
        {
            ItemStack input = internalItemHandler.getStackInSlot(0);
            if (input.isEmpty() && lastInput != Items.AIR)
            {
                recipe = null;
                progress = -1;
                energy = 0;
                lastInput = Items.AIR;
                setActive(false);
            }
            else if (input.getItem() != lastInput)
            {
                lastInput = input.getItem();
                recipe = findRecipe();
            }
        }
    }

    @Override
    public SideAccess getNextPortSetting(Side side)
    {
        if (side == Side.FRONT) { return SideAccess.NONE; }

        SideAccess port = ports.get(side);
        return switch (port)
        {
            case NONE -> SideAccess.INPUT_ALL;
            case INPUT_ALL -> SideAccess.OUTPUT_ALL;
            case OUTPUT_ALL -> SideAccess.NONE;
            default -> throw new IllegalStateException("Invalid port setting: " + port.getSerializedName());
        };
    }

    @Override
    public SideAccess getPriorPortSetting(Side side)
    {
        if (side == Side.FRONT) { return SideAccess.NONE; }

        SideAccess port = ports.get(side);
        return switch (port)
        {
            case NONE -> SideAccess.OUTPUT_ALL;
            case INPUT_ALL -> SideAccess.NONE;
            case OUTPUT_ALL -> SideAccess.INPUT_ALL;
            default -> throw new IllegalStateException("Invalid port setting: " + port.getSerializedName());
        };
    }

    @Override
    public boolean canFill(Direction side) { return false; }

    @Override
    public boolean canDrain(Direction side) { return cardinalPorts.get(side).isOutput(); }

    private boolean outputFits()
    {
        if (recipe == null) { return false; }

        FluidStack output = ((LiquifierRecipe)recipe).getOutput();
        return internalFluidHandler.fill(output, IFluidHandler.FluidAction.SIMULATE) == output.getAmount();
    }

    @Override
    protected void initCapabilities()
    {
        internalItemHandler = new MachineItemStackHandler(this, 1);
        internalFluidHandler = new MachineFluidHandler(1, BASE_FLUID_CAPACITY, (fluid, tank) -> true);

        super.initCapabilities();
    }

    @Override
    public <C> LazyOptional<C> getCapability(Capability<C> cap, Direction side)
    {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            if (side == null) { return lazyInternalFluidHandler.cast(); }

            if (cardinalPorts.get(side).isDisabled()) { return LazyOptional.empty(); }
            return lazyFluidHandlers.get(side).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved()
    {
        super.setRemoved();

        for (Direction dir : Direction.values())
        {
            LazyOptional<SidedFluidHandler> handler = lazyFluidHandlers.get(dir);
            if (handler.isPresent()) { handler.invalidate(); }
        }

        lazyInternalFluidHandler.invalidate();
        lazyInternalFluidHandler = LazyOptional.empty();
    }

    @Override
    public void onLevelChanged()
    {
        super.onLevelChanged();

        int mult = (int)Math.pow(2, level.ordinal());
        internalFluidHandler.setCapacity(BASE_FLUID_CAPACITY * mult);
    }

    @Override
    protected int getBaseEnergyCapacity() { return BASE_CAPACITY; }

    @Override
    protected int getBaseConsumption() { return BASE_CONSUMPTION; }

    @Override
    protected int getEnergyRequired() { return energy; }

    public int getFluidCapacity() { return internalFluidHandler.getTankCapacity(0); }

    public FluidStack getFluidStored() { return internalFluidHandler.getFluidInTank(0); }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        nbt.put("tank", internalFluidHandler.serializeNBT());
        return super.save(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);
        internalFluidHandler.deserializeNBT(nbt.getCompound("tank"));
    }

    @Override
    public Component getDisplayName() { return TITLE; }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player)
    {
        return new ContainerMenuLiquifier(windowId, this, inventory);
    }
}