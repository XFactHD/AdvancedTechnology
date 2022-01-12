package xfacthd.advtech.common.blockentity.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.BlockEntityInventoryMachine;
import xfacthd.advtech.common.capability.item.MachineItemStackHandler;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.menu.utility.ContainerMenuItemSplitter;
import xfacthd.advtech.common.util.data.PropertyHolder;

public class BlockEntityItemSplitter extends BlockEntityInventoryMachine //TODO: taller UI, show output slots, allow "locking" slots (puts barrier items in the slots)
{
    public static final Component TITLE = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".item_sorter");
    public static final int OUTPUT_COUNT = 3;
    public static final int FILTERS_PER_OUTPUT = 3;

    private final Side[] outputMap = new Side[] { Side.RIGHT, Side.BACK, Side.LEFT };
    private final ItemStack[][] filters = new ItemStack[OUTPUT_COUNT][FILTERS_PER_OUTPUT];

    public BlockEntityItemSplitter(BlockPos pos, BlockState state)
    {
        super(ATContent.machineEntity(MachineType.ITEM_SPLITTER), pos, state);
        for (int i = 0; i < OUTPUT_COUNT; i++)
        {
            for (int j = 0; j < FILTERS_PER_OUTPUT; j++)
            {
                filters[i][j] = ItemStack.EMPTY;
            }
        }

        ports.put(Side.FRONT, SideAccess.INPUT_ALL);
        ports.put(Side.RIGHT, SideAccess.OUTPUT_ALL);
        ports.put(Side.LEFT, SideAccess.OUTPUT_ALL);
        ports.put(Side.BACK, SideAccess.OUTPUT_ALL);
    }

    @Override
    protected void tickInternal()
    {
        if (!level().isClientSide() && canRun(true))
        {
            ItemStack input = internalItemHandler.getStackInSlot(0);
            if (!input.isEmpty())
            {
                for (int output = 0; output < OUTPUT_COUNT; output++)
                {
                    int targetSlot = testSlotFilters(output, input);
                    if (tryMoveInput(input, targetSlot)) { break; }
                }

                setCycleComplete();
            }
        }

        super.tickInternal();
    }

    private int testSlotFilters(int slot, ItemStack input)
    {
        int targetSlot = -1;
        for (int filter = 0; filter < FILTERS_PER_OUTPUT; filter++)
        {
            if (filters[slot][filter].isEmpty() && targetSlot == -1)
            {
                targetSlot = slot;
            }
            else if (filters[slot][filter].sameItemStackIgnoreDurability(input))
            {
                targetSlot = slot;
                break;
            }
        }
        return targetSlot;
    }

    private boolean tryMoveInput(ItemStack input, int targetSlot)
    {
        targetSlot++; //Output slots start at 1
        if (targetSlot > 0)
        {
            ItemStack sim = internalItemHandler.insertItem(targetSlot, input, true);
            if (sim.isEmpty() || sim.getCount() < input.getCount())
            {
                int amount = input.getCount();
                if (!sim.isEmpty()) { amount -= sim.getCount(); }
                ItemStack out = internalItemHandler.extractItem(0, amount, false);
                internalItemHandler.insertItem(targetSlot, out, false);

                return true;
            }
        }

        return false;
    }

    public void setFilter(int slot, ItemStack stack)
    {
        int output = slot / FILTERS_PER_OUTPUT;
        int filter = slot % FILTERS_PER_OUTPUT;

        if (output >= 0 && output < OUTPUT_COUNT)
        {
            filters[output][filter] = stack;
            setChanged();
        }
    }

    public ItemStack getFilter(int slot)
    {
        int output = slot / FILTERS_PER_OUTPUT;
        int filter = slot % FILTERS_PER_OUTPUT;

        if (output >= 0 && output < OUTPUT_COUNT)
        {
            return filters[output][filter];
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        active = true; //Item sorter is always active
    }

    @Override
    public boolean canForcePush() { return true; }

    @Override
    protected void pushOutputs()
    {
        Direction facing = getBlockState().getValue(PropertyHolder.FACING_HOR);
        boolean worked = false;
        for (int i = 0; i < 3; i++)
        {
            Direction side = outputMap[i].mapFacing(facing);
            int slot = i + 1;
            if (!internalItemHandler.getStackInSlot(slot).isEmpty())
            {
                worked |= getNeighboringHandler(side).map(handler ->
                {
                    ItemStack stack = internalItemHandler.getStackInSlot(slot);
                    if (!stack.isEmpty())
                    {
                        ItemStack remainder = ItemHandlerHelper.insertItemStacked(handler, stack, false);
                        internalItemHandler.setStackInSlot(slot, remainder);
                        return remainder.isEmpty() || remainder.getCount() < stack.getCount();
                    }
                    return false;
                }).orElse(false);
            }
        }

        if (worked) { setChanged(); }
    }

    @Override
    public boolean canInsert(Direction side, int slot) { return cardinalPorts.get(side).isInput() && slot == 0; }

    @Override
    public boolean canExtract(Direction side, int slot)
    {
        return cardinalPorts.get(side).isOutput() && slot > 0 && slot < 4;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (slot == 0)
        {
            for (int output = 0; output < OUTPUT_COUNT; output++)
            {
                if (testSlotFilters(output, stack) != -1)
                {
                    return true;
                }
            }
            return false;
        }

        //Output slots start at 1
        return testSlotFilters(slot - 1, stack) != -1;
    }

    @Override
    public SideAccess getNextPortSetting(Side side) { return ports.get(side); }

    @Override
    public SideAccess getPriorPortSetting(Side side) { return ports.get(side); }

    @Override
    protected boolean needSlotNotification() { return false; }

    @Override
    protected void onSlotChangedInternal(int slot) { }

    @Override
    protected void initCapabilities()
    {
        internalItemHandler = new MachineItemStackHandler(this, 4);

        super.initCapabilities();
    }

    @Override
    public float getProgress() { return 0; }

    @Override
    public boolean supportsEnhancements() { return false; }

    @Override
    public boolean canUpgrade() { return false; }

    @Override
    public void onLevelChanged() { }

    @Override
    protected boolean needPower() { return false; }

    @Override
    protected int getBaseEnergyCapacity() { return 0; }

    @Override
    public Side getFrontSide() { return Side.TOP; }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        ListTag filterList = new ListTag();
        for (int i = 0; i < (OUTPUT_COUNT * FILTERS_PER_OUTPUT); i++)
        {
            int output = i / FILTERS_PER_OUTPUT;
            int idx = i % FILTERS_PER_OUTPUT;

            CompoundTag filter = filters[output][idx].save(new CompoundTag());
            filterList.addTag(i, filter);
        }
        nbt.put("filters", filterList);

        return super.save(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);

        ListTag filterList = nbt.getList("filters", Tag.TAG_COMPOUND);
        for (int i = 0; i < (OUTPUT_COUNT * FILTERS_PER_OUTPUT); i++)
        {
            int output = i / FILTERS_PER_OUTPUT;
            int idx = i % FILTERS_PER_OUTPUT;

            CompoundTag filter = filterList.getCompound(i);
            filters[output][idx] = ItemStack.of(filter);
        }
    }

    @Override
    public Component getDisplayName() { return TITLE; }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        return new ContainerMenuItemSplitter(containerId, this, inventory);
    }
}