package xfacthd.advtech.common.menu;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.net.packets.container.*;
import xfacthd.advtech.common.util.sync.BoolDataSlot;
import xfacthd.advtech.common.util.sync.ByteDataSlot;

import java.util.ArrayList;
import java.util.List;

public abstract class AdvancedContainerMenu extends AbstractContainerMenu
{
    private final List<ByteDataSlot> byteSlots = new ArrayList<>();
    private final List<DataSlot> intSlots = new ArrayList<>();
    private final List<BoolDataSlot> boolSlots = new ArrayList<>();

    protected final Player player;
    protected final IItemHandler playerInventory;

    protected AdvancedContainerMenu(MenuType<?> type, int windowId, Inventory playerInv)
    {
        super(type, windowId);
        this.player = playerInv.player;
        this.playerInventory = new InvWrapper(playerInv);
    }

    protected void addByteDataSlot(ByteDataSlot byteRef) { byteSlots.add(byteRef); }

    protected void addIntDataSlot(DataSlot intRef) { intSlots.add(intRef); }

    protected void addBoolDataSlot(BoolDataSlot boolRef) { boolSlots.add(boolRef); }

    @Override
    public void broadcastChanges()
    {
        super.broadcastChanges();

        int byteRefCount = byteSlots.size();
        for (int i = 0; i < byteRefCount; i++)
        {
            ByteDataSlot byteSlot = byteSlots.get(i);
            if (byteSlot.checkAndClearUpdateFlag())
            {
                NetworkHandler.sendToPlayer(new PacketSyncByteSlot(containerId, i, byteSlot.get()), (ServerPlayer) player);
            }
        }

        int intRefCount = intSlots.size();
        for (int i = 0; i < intRefCount; i++)
        {
            DataSlot intSlot = intSlots.get(i);
            if (intSlot.checkAndClearUpdateFlag())
            {
                NetworkHandler.sendToPlayer(new PacketSyncIntSlot(containerId, i, intSlot.get()), (ServerPlayer) player);
            }
        }

        int boolRefCount = boolSlots.size();
        for (int i = 0; i < boolRefCount; i++)
        {
            BoolDataSlot boolSlot = boolSlots.get(i);
            if (boolSlot.checkAndClearUpdateFlag())
            {
                NetworkHandler.sendToPlayer(new PacketSyncBoolSlot(containerId, i, boolSlot.get()), (ServerPlayer) player);
            }
        }
    }

    public final void handleByteSlot(int id, byte data) { byteSlots.get(id).set(data); }

    public final void handleIntSlot(int id, int data) { intSlots.get(id).set(data); }

    public final void handleBoolSlot(int id, boolean data) { boolSlots.get(id).set(data); }

    protected void layoutPlayerInventorySlots(int posX, int posY)
    {
        // Player inventory
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                addSlot(new SlotItemHandler(playerInventory, (y * 9) + x + 9, posX + (x * 18), posY + (y * 18)));
            }
        }

        // Hotbar
        posY += 58;
        for (int x = 0; x < 9; x++)
        {
            addSlot(new SlotItemHandler(playerInventory, x, posX + (x * 18), posY));
        }
    }
}