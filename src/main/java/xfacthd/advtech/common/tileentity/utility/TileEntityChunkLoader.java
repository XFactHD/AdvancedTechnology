package xfacthd.advtech.common.tileentity.utility;

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
import xfacthd.advtech.common.net.packets.debug.PacketDebugForcedChunks;
import xfacthd.advtech.common.tileentity.TileEntityMachine;

import java.util.ArrayList;
import java.util.List;

public class TileEntityChunkLoader extends TileEntityMachine
{
    private static final AxisAlignedBB NULL_AABB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    public static final ITextComponent TITLE = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".chunk_loader");
    private static final int BASE_CAPACITY = 100000;
    private static final int BASE_CONSUMPTION = 900;
    private static final int PER_CHUNK_CONSUMPTION = 100;

    private int maxChunks = 0;
    private int perChunkConsumption = 0;

    private final List<ChunkPos> loadedChunks = new ArrayList<>();
    private int radius = 0;
    private int count = 0;
    private boolean chunksLoaded = false;
    private boolean showChunks = false; //Client side only

    public TileEntityChunkLoader() { super(TileEntityTypes.tileTypeChunkLoader, true); }

    @Override
    public void tick()
    {
        //noinspection ConstantConditions
        if (!world.isRemote())
        {
            if (!active && energyHandler.getEnergyStored() >= BASE_CONSUMPTION && canRun(true))
            {
                setActive(true);
            }
            else if (active && (!canRun(true) || energyHandler.getEnergyStored() < BASE_CONSUMPTION))
            {
                setActive(false);
            }

            if (active)
            {
                energyHandler.extractEnergyInternal(perChunkConsumption * loadedChunks.size(), false);
            }
        }

        super.tick();
    }

    @Override
    protected void firstTick()
    {
        onLevelChanged();
        count = (radius * 2 + 1) * (radius * 2 + 1);
        markFullUpdate();
    }

    @Override
    protected void setActive(boolean active)
    {
        super.setActive(active);

        //noinspection ConstantConditions
        if (active != chunksLoaded && !world.isRemote())
        {
            ServerWorld sWorld = (ServerWorld)world;

            if (active)
            {
                int centerX = pos.getX() >> 4;
                int centerZ = pos.getZ() >> 4;

                int startX = centerX - radius;
                int startZ = centerZ - radius;
                int endX = centerX + radius;
                int endZ = centerZ + radius;
                for (int x = startX; x <= endX; x++)
                {
                    for (int z = startZ; z <= endZ; z++)
                    {
                        if (sWorld.forceChunk(x, z, true))
                        {
                            loadedChunks.add(new ChunkPos(x, z));
                        }
                    }
                }
            }
            else
            {
                loadedChunks.forEach(cpos -> sWorld.forceChunk(cpos.x, cpos.z, false));
                loadedChunks.clear();
            }

            chunksLoaded = active;
            markDirty();

            PacketDebugForcedChunks.sendToClient(sWorld);
        }
    }

    public void setRadius(int radius)
    {
        int count = (radius * 2 + 1) * (radius * 2 + 1);
        if (count > maxChunks) { return; }
        this.count = count;

        //noinspection ConstantConditions
        if (chunksLoaded && radius != this.radius && !world.isRemote())
        {
            ServerWorld sWorld = (ServerWorld)world;

            int minRad = Math.min(radius, this.radius);
            int maxRad = Math.max(radius, this.radius);
            boolean bigger = radius > this.radius;

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
                    if (Math.abs(x - centerX) > minRad || Math.abs(z - centerZ) > minRad)
                    {
                        if (bigger && sWorld.forceChunk(x, z, true))
                        {
                            loadedChunks.add(new ChunkPos(x, z));
                        }
                        else if (!bigger)
                        {
                            ChunkPos cpos = new ChunkPos(x, z);
                            if (loadedChunks.remove(cpos))
                            {
                                sWorld.forceChunk(x, z, false);
                            }
                        }
                    }
                }
            }

            PacketDebugForcedChunks.sendToClient(sWorld);
        }

        this.radius = radius;
        markForSync();
    }

    public int getRadius() { return radius; }

    /**
     * Returns the amount of chunks actually loaded by this loader in the area given by the set {@link TileEntityChunkLoader#radius}
     * This value can be different to {@link TileEntityChunkLoader#getMaxChunkCount()} due to the area overlapping with another loader
     */
    public int getLoadedChunkCount() { return loadedChunks.size(); }

    /**
     * Returns the amount of chunks covered by the set {@link TileEntityChunkLoader#radius}
     */
    public int getMaxChunkCount() { return count; }

    public int getMaxChunks() { return maxChunks; }

    public List<ChunkPos> getChunkList() { return loadedChunks; }

    public boolean showChunks() { return showChunks; }

    public boolean switchShowChunks() { return (showChunks = !showChunks); }

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

            loadedChunks.forEach(cpos -> sWorld.forceChunk(cpos.x, cpos.z, false));
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
        nbt.getList("chunks", Constants.NBT.TAG_COMPOUND).stream().map(tag -> (CompoundNBT)tag).forEach(tag ->
        {
            ChunkPos cpos = new ChunkPos(tag.getLong("cpos"));
            loadedChunks.add(cpos);
        });
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putInt("radius", radius);
        nbt.putInt("count", count);
        nbt.putBoolean("active", active);
        nbt.putBoolean("loaded", chunksLoaded);

        ListNBT list = new ListNBT();
        loadedChunks.forEach(cpos ->
        {
            CompoundNBT tag = new CompoundNBT();
            tag.putLong("cpos", cpos.asLong());
            list.add(tag);
        });
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
    public AxisAlignedBB getRenderBoundingBox() { return showChunks ? INFINITE_EXTENT_AABB : NULL_AABB; }
}