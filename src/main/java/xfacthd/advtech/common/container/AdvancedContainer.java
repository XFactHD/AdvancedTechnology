package xfacthd.advtech.common.container;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.*;
import net.minecraft.util.IntReferenceHolder;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.net.packets.container.*;
import xfacthd.advtech.common.util.sync.*;

import java.util.ArrayList;
import java.util.List;

public abstract class AdvancedContainer extends Container
{
    private final List<ByteReferenceHolder> trackedByteHolders = new ArrayList<>();
    private final List<IntReferenceHolder> trackedIntHolders = new ArrayList<>();
    private final List<BoolReferenceHolder> trackedBoolHolders = new ArrayList<>();

    protected AdvancedContainer(ContainerType<?> type, int id) { super(type, id); }

    protected void trackByte(ByteReferenceHolder byteRef) { trackedByteHolders.add(byteRef); }

    protected void trackRealInt(IntReferenceHolder intRef) { trackedIntHolders.add(intRef); }

    protected void trackBool(BoolReferenceHolder boolRef) { trackedBoolHolders.add(boolRef); }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        int byteRefCount = trackedByteHolders.size();
        for (int i = 0; i < byteRefCount; i++)
        {
            ByteReferenceHolder byteRef = trackedByteHolders.get(i);
            if (byteRef.isDirty())
            {
                for(IContainerListener listener : listeners)
                {
                    if (listener instanceof ServerPlayerEntity)
                    {
                        NetworkHandler.sendToPlayer(new PacketSyncByteHolder(windowId, i, byteRef.get()), (ServerPlayerEntity) listener);
                    }
                }
            }
        }

        int intRefCount = trackedIntHolders.size();
        for (int i = 0; i < intRefCount; i++)
        {
            IntReferenceHolder intRef = trackedIntHolders.get(i);
            if (intRef.isDirty())
            {
                for(IContainerListener listener : listeners)
                {
                    if (listener instanceof ServerPlayerEntity)
                    {
                        NetworkHandler.sendToPlayer(new PacketSyncIntHolder(windowId, i, intRef.get()), (ServerPlayerEntity) listener);
                    }
                }
            }
        }

        int boolRefCount = trackedBoolHolders.size();
        for (int i = 0; i < boolRefCount; i++)
        {
            BoolReferenceHolder boolRef = trackedBoolHolders.get(i);
            if (boolRef.isDirty())
            {
                for (IContainerListener listener : listeners)
                {
                    if (listener instanceof ServerPlayerEntity)
                    {
                        NetworkHandler.sendToPlayer(new PacketSyncBoolHolder(windowId, i, boolRef.get()), (ServerPlayerEntity) listener);
                    }
                }
            }
        }
    }

    public final void handleTrackedByte(int id, byte data) { trackedByteHolders.get(id).set(data); }

    public final void handleTrackedInt(int id, int data) { trackedIntHolders.get(id).set(data); }

    public final void handleTrackedBool(int id, boolean data) { trackedBoolHolders.get(id).set(data); }
}