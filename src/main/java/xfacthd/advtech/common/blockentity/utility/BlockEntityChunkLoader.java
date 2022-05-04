package xfacthd.advtech.common.blockentity.utility;

import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.world.ForgeChunkManager;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.BlockEntityMachine;
import xfacthd.advtech.common.capability.energy.EnergySink;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.menu.utility.ContainerMenuChunkLoader;
import xfacthd.advtech.common.util.Utils;

import java.util.function.LongConsumer;

public class BlockEntityChunkLoader extends BlockEntityMachine
{
    public static final Component TITLE = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".chunk_loader");
    private static final int BASE_CAPACITY = 100000;
    private static final int BASE_CONSUMPTION = 900;
    private static final int PER_CHUNK_CONSUMPTION = 100;

    private int maxChunks = 0;
    private int perChunkConsumption = 0;

    private final LongSet loadedChunks = new LongArraySet();
    private int radius = 0;
    private int count = 0;
    private boolean chunksLoaded = false;
    private boolean showChunks = false; //Client side only

    public BlockEntityChunkLoader(BlockPos pos, BlockState state)
    {
        super(ATContent.machineEntity(MachineType.CHUNK_LOADER), pos, state, true);
    }

    @Override
    public void tickInternal()
    {
        if (!level().isClientSide())
        {
            if (!active && canRun(true))
            {
                setActive(true);
            }
            else if (active && !canRun(true))
            {
                setActive(false);
            }

            if (active)
            {
                energyHandler.extractEnergyInternal(perChunkConsumption * count, false);
            }
        }
    }

    @Override
    public void onLoad()
    {
        super.onLoad();

        onLevelChanged();
        count = (radius * 2 + 1) * (radius * 2 + 1);
        markFullUpdate();

        //Drop tickets on initial load if the chunk loader can't work
        if (!active || !canRun(true))
        {
            loadedChunks.forEach((LongConsumer) (chunk -> forceChunk(chunk, false)));
            loadedChunks.clear();
        }
    }

    @Override
    protected void setActive(boolean active)
    {
        super.setActive(active);

        if (active != chunksLoaded && !level().isClientSide())
        {
            loadChunks(active ? LoadMode.LOAD : LoadMode.UNLOAD, radius);

            chunksLoaded = active;
            setChanged();
        }
    }

    public void setRadius(int radius)
    {
        if (radius < 0) { return; }

        int count = (radius * 2 + 1) * (radius * 2 + 1);
        if (count > maxChunks) { return; }
        this.count = count;

        if (chunksLoaded && radius != this.radius && !level().isClientSide())
        {
            loadChunks(LoadMode.CHANGE_RADIUS, radius);
        }

        this.radius = radius;
        markForSync();
    }

    private void loadChunks(LoadMode mode, int newRadius)
    {
        int minRad = Math.min(newRadius, radius);
        int maxRad = Math.max(newRadius, radius);
        boolean bigger = newRadius > radius;

        int centerX = worldPosition.getX() >> 4;
        int centerZ = worldPosition.getZ() >> 4;

        int startX = centerX - maxRad;
        int startZ = centerZ - maxRad;
        int endX = centerX + maxRad;
        int endZ = centerZ + maxRad;

        for (int x = startX; x <= endX; x++)
        {
            for (int z = startZ; z <= endZ; z++)
            {
                long chunk = ChunkPos.asLong(x, z);

                if (mode == LoadMode.LOAD)
                {
                    forceChunk(chunk, true);
                    loadedChunks.add(chunk);
                }
                else if (mode == LoadMode.CHANGE_RADIUS)
                {
                    if (Math.abs(x - centerX) > minRad || Math.abs(z - centerZ) > minRad)
                    {
                        if (bigger)
                        {
                            forceChunk(chunk, true);
                            loadedChunks.add(chunk);
                        }
                        else
                        {
                            forceChunk(chunk, false);
                            loadedChunks.remove(chunk);
                        }
                    }
                }
                else if (mode == LoadMode.UNLOAD)
                {
                    forceChunk(chunk, false);
                    loadedChunks.remove(chunk);
                }
            }
        }
    }

    public int getRadius() { return radius; }

    /**
     * Returns the amount of chunks covered by the set {@link BlockEntityChunkLoader#radius}
     */
    public int getMaxChunksInRadius() { return count; }

    public int getMaxChunks() { return maxChunks; }

    public boolean showChunks() { return showChunks; }

    public boolean switchShowChunks() { return (showChunks = !showChunks); }

    @Override
    protected boolean canRun(boolean cycleStart)
    {
        if (!super.canRun(cycleStart)) { return false; }
        return energyHandler.getEnergyStored() >= (perChunkConsumption * count);
    }

    @Override
    public float getProgress() { return 0; }

    @Override
    public void onLevelChanged()
    {
        int lvl = this.level.ordinal();
        int mult = (int)Math.pow(2, lvl);
        perChunkConsumption = PER_CHUNK_CONSUMPTION * mult;

        int capacity = getBaseEnergyCapacity() * mult;
        int maxReceive = BASE_CONSUMPTION * mult * 10;
        energyHandler.reconfigure(capacity, maxReceive, 0);

        maxChunks = ((lvl + 1) * 2 + 1) * ((lvl + 1) * 2 + 1);
    }

    public void onBlockBroken()
    {
        if (!level().isClientSide() && chunksLoaded)
        {
            loadedChunks.forEach((LongConsumer) (chunk -> forceChunk(chunk, false)));
            loadedChunks.clear();
        }
    }

    private void forceChunk(long chunk, boolean add)
    {
        ChunkPos pos = new ChunkPos(chunk);
        ForgeChunkManager.forceChunk(
                (ServerLevel) level(),
                AdvancedTechnology.MODID,
                worldPosition,
                pos.x,
                pos.z,
                add,
                true
        );
    }

    @Override
    protected void initCapabilities() { energyHandler = new EnergySink(BASE_CAPACITY, BASE_CONSUMPTION * 10); }

    @Override
    protected int getBaseEnergyCapacity() { return BASE_CAPACITY; }

    @Override
    public boolean supportsEnhancements() { return false; }

    @Override
    public void readNetworkNBT(CompoundTag nbt)
    {
        super.readNetworkNBT(nbt);
        radius = nbt.getInt("radius");
    }

    @Override
    public void writeNetworkNBT(CompoundTag nbt)
    {
        super.writeNetworkNBT(nbt);
        nbt.putInt("radius", radius);
    }

    @Override
    public void writeSyncPacket(FriendlyByteBuf buffer)
    {
        super.writeSyncPacket(buffer);
        buffer.writeInt(radius);
    }

    @Override
    public void readSyncPacket(FriendlyByteBuf buffer)
    {
        super.readSyncPacket(buffer);
        radius = buffer.readInt();
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);
        radius = nbt.getInt("radius");
        count = nbt.getInt("count");
        active = nbt.getBoolean("active");
        chunksLoaded = nbt.getBoolean("loaded");

        loadedChunks.clear();
        nbt.getList("chunks", Tag.TAG_COMPOUND)
                .stream()
                .map(tag -> (CompoundTag)tag)
                .forEach(tag -> loadedChunks.add(tag.getLong("cpos")));
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);

        nbt.putInt("radius", radius);
        nbt.putInt("count", count);
        nbt.putBoolean("active", active);
        nbt.putBoolean("loaded", chunksLoaded);

        ListTag list = new ListTag();
        loadedChunks.forEach((LongConsumer) (cpos ->
        {
            CompoundTag tag = new CompoundTag();
            tag.putLong("cpos", cpos);
            list.add(tag);
        }));
        nbt.put("chunks", list);
    }

    @Override
    public Component getDisplayName() { return TITLE; }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player)
    {
        return new ContainerMenuChunkLoader(windowId, this, inventory);
    }

    @Override
    public AABB getRenderBoundingBox() { return showChunks ? INFINITE_EXTENT_AABB : Utils.NULL_AABB; }

    private enum LoadMode
    {
        LOAD,
        CHANGE_RADIUS,
        UNLOAD
    }
}