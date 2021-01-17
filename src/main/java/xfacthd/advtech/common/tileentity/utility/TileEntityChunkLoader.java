package xfacthd.advtech.common.tileentity.utility;

import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.capability.energy.EnergySink;
import xfacthd.advtech.common.container.utility.ContainerChunkLoader;
import xfacthd.advtech.common.data.types.TileEntityTypes;
import xfacthd.advtech.common.tileentity.TileEntityMachine;
import xfacthd.advtech.common.util.Utils;
import xfacthd.advtech.common.util.data.ChunkLoadManager;

import java.util.function.LongConsumer;

public class TileEntityChunkLoader extends TileEntityMachine
{
    public static final ITextComponent TITLE = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".chunk_loader");
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

    public TileEntityChunkLoader() { super(TileEntityTypes.tileTypeChunkLoader, true); }

    @Override
    public void tick()
    {
        super.tick();

        //noinspection ConstantConditions
        if (!world.isRemote())
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
    protected void firstTick()
    {
        onLevelChanged();
        count = (radius * 2 + 1) * (radius * 2 + 1);
        markFullUpdate();

        //Aquire tickets on initial load if possible, else drop them
        ServerWorld sWorld = (ServerWorld) world;
        //noinspection ConstantConditions
        ChunkLoadManager manager = ChunkLoadManager.get(sWorld);
        if (active && canRun(true))
        {
            manager.loadChunksInitial(sWorld, this, loadedChunks);
        }
        else
        {
            loadedChunks.forEach((LongConsumer) (chunk -> manager.releaseChunk(sWorld, this, chunk)));
            loadedChunks.clear();
        }
    }

    @Override
    protected void setActive(boolean active)
    {
        super.setActive(active);

        //noinspection ConstantConditions
        if (active != chunksLoaded && !world.isRemote())
        {
            loadChunks(active ? LoadMode.LOAD : LoadMode.UNLOAD, radius);

            chunksLoaded = active;
            markDirty();
        }
    }

    public void setRadius(int radius)
    {
        if (radius < 0) { return; }

        int count = (radius * 2 + 1) * (radius * 2 + 1);
        if (count > maxChunks) { return; }
        this.count = count;

        //noinspection ConstantConditions
        if (chunksLoaded && radius != this.radius && !world.isRemote())
        {
            loadChunks(LoadMode.CHANGE_RADIUS, radius);
        }

        this.radius = radius;
        markForSync();
    }

    private void loadChunks(LoadMode mode, int newRadius)
    {
        ServerWorld sWorld = (ServerWorld)world;
        //noinspection ConstantConditions
        ChunkLoadManager manager = ChunkLoadManager.get(sWorld);

        int minRad = Math.min(newRadius, radius);
        int maxRad = Math.max(newRadius, radius);
        boolean bigger = newRadius > radius;

        int centerX = pos.getX() >> 4;
        int centerZ = pos.getZ() >> 4;

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
                    manager.registerChunk(sWorld, this, chunk);
                    loadedChunks.add(chunk);
                }
                else if (mode == LoadMode.CHANGE_RADIUS)
                {
                    if (Math.abs(x - centerX) > minRad || Math.abs(z - centerZ) > minRad)
                    {
                        if (bigger)
                        {
                            manager.registerChunk(sWorld, this, chunk);
                            loadedChunks.add(chunk);
                        }
                        else
                        {
                            manager.releaseChunk(sWorld, this, chunk);
                            loadedChunks.remove(chunk);
                        }
                    }
                }
                else if (mode == LoadMode.UNLOAD)
                {
                    manager.releaseChunk(sWorld, this, chunk);
                    loadedChunks.remove(chunk);
                }
            }
        }
    }

    public int getRadius() { return radius; }

    /**
     * Returns the amount of chunks covered by the set {@link TileEntityChunkLoader#radius}
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
        //noinspection ConstantConditions
        if (!world.isRemote() && chunksLoaded)
        {
            ServerWorld sWorld = (ServerWorld)world;
            ChunkLoadManager manager = ChunkLoadManager.get(sWorld);

            loadedChunks.forEach((LongConsumer) (chunk -> manager.releaseChunk(sWorld, this, chunk)));
            loadedChunks.clear();
        }
    }

    @Override
    protected void initCapabilities() { energyHandler = new EnergySink(BASE_CAPACITY, BASE_CONSUMPTION * 10); }

    @Override
    protected int getBaseEnergyCapacity() { return BASE_CAPACITY; }

    @Override
    public boolean supportsEnhancements() { return false; }

    @Override
    public void readNetworkNBT(CompoundNBT nbt)
    {
        super.readNetworkNBT(nbt);
        radius = nbt.getInt("radius");
    }

    @Override
    public void writeNetworkNBT(CompoundNBT nbt)
    {
        super.writeNetworkNBT(nbt);
        nbt.putInt("radius", radius);
    }

    @Override
    public void writeSyncPacket(PacketBuffer buffer)
    {
        super.writeSyncPacket(buffer);
        buffer.writeInt(radius);
    }

    @Override
    public void readSyncPacket(PacketBuffer buffer)
    {
        super.readSyncPacket(buffer);
        radius = buffer.readInt();
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        radius = nbt.getInt("radius");
        count = nbt.getInt("count");
        active = nbt.getBoolean("active");
        chunksLoaded = nbt.getBoolean("loaded");

        loadedChunks.clear();
        nbt.getList("chunks", Constants.NBT.TAG_COMPOUND)
                .stream()
                .map(tag -> (CompoundNBT)tag)
                .forEach(tag -> loadedChunks.add(tag.getLong("cpos")));
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putInt("radius", radius);
        nbt.putInt("count", count);
        nbt.putBoolean("active", active);
        nbt.putBoolean("loaded", chunksLoaded);

        ListNBT list = new ListNBT();
        loadedChunks.forEach((LongConsumer) (cpos ->
        {
            CompoundNBT tag = new CompoundNBT();
            tag.putLong("cpos", cpos);
            list.add(tag);
        }));
        nbt.put("chunks", list);

        return super.write(nbt);
    }

    @Override
    public ITextComponent getDisplayName() { return TITLE; }

    @Override
    public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player)
    {
        return new ContainerChunkLoader(windowId, this, inventory);
    }

    @Override
    public double getMaxRenderDistanceSquared() { return showChunks ? Double.MAX_VALUE : 0; }

    @Override
    public AxisAlignedBB getRenderBoundingBox() { return showChunks ? INFINITE_EXTENT_AABB : Utils.NULL_AABB; }

    private enum LoadMode
    {
        LOAD,
        CHANGE_RADIUS,
        UNLOAD
    }
}