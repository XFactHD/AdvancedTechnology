package xfacthd.advtech.common.menu.utility;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.utility.BlockEntityItemSplitter;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.menu.ContainerMenuInventoryMachine;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.net.packets.machine.PacketItemSplitterSetFilter;
import xfacthd.advtech.common.net.packets.machine.PacketItemSplitterSyncFilter;

public class ContainerMenuItemSplitter extends ContainerMenuInventoryMachine<BlockEntityItemSplitter>
{
    public static final int FILTER_COUNT = BlockEntityItemSplitter.OUTPUT_COUNT * BlockEntityItemSplitter.FILTERS_PER_OUTPUT;

    private final ItemStack[] lastFilters = new ItemStack[FILTER_COUNT];

    public ContainerMenuItemSplitter(int windowId, BlockEntityItemSplitter machine, Inventory playerInv)
    {
        super(ATContent.MENU_ITEM_SPLITTER.get(), windowId, ATContent.machineBlock(MachineType.ITEM_SPLITTER), machine, playerInv);

        layoutPlayerInventorySlots(8, 105);

        machine.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .ifPresent(handler -> addSlot(new SlotItemHandler(handler, 0, 80, 35)));

        for (int i = 0; i < FILTER_COUNT; i++)
        {
            lastFilters[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public void broadcastChanges()
    {
        super.broadcastChanges();

        for (int i = 0; i < FILTER_COUNT; i++)
        {
            if (!ItemStack.matches(lastFilters[i], machine.getFilter(i)))
            {
                lastFilters[i] = machine.getFilter(i);
                NetworkHandler.sendToPlayer(new PacketItemSplitterSyncFilter(containerId, i, lastFilters[i]), (ServerPlayer) player);
            }
        }
    }

    public void sendFilterConfig(int idx, ItemStack stack)
    {
        NetworkHandler.sendToServer(new PacketItemSplitterSetFilter(machine.getBlockPos(), idx, stack));
    }

    public void receiveFilterConfig(int idx, ItemStack stack) { lastFilters[idx] = stack; }

    public ItemStack getFilter(int slot) { return lastFilters[slot]; }
}