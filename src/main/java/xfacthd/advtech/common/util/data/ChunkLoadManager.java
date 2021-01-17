package xfacthd.advtech.common.util.data;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.*;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.tileentity.utility.TileEntityChunkLoader;

import java.util.Comparator;
import java.util.Set;
import java.util.function.LongConsumer;

/*
 * Inspired by https://github.com/mekanism/Mekanism/blob/1.15.x/src/main/java/mekanism/common/chunkloading/ChunkManager.java
 */

@Mod.EventBusSubscriber(modid = AdvancedTechnology.MODID)
public class ChunkLoadManager extends WorldSavedData
{
    private static final String NAME = AdvancedTechnology.MODID + "_chunk_loaders";
    private static final int TICKET_DISTANCE = 2; //No idea what this does but it leads to equivalent capabilities as a forced chunk
    private static final TicketType<ChunkPos> STARTUP_TICKET_TYPE = TicketType.create(
            AdvancedTechnology.MODID + ":initial_chunkload",
            Comparator.comparingLong(ChunkPos::asLong),
            10
    );
    private static final TicketType<TileEntityChunkLoader> LOADER_TICKET_TYPE = TicketType.create(
            AdvancedTechnology.MODID + ":chunk_loader",
            Comparator.comparing(TileEntity::getPos)
    );

    private final Long2ObjectOpenHashMap<Set<BlockPos>> chunks = new Long2ObjectOpenHashMap<>();

    private ChunkLoadManager() { super(NAME); }

    public static ChunkLoadManager get(ServerWorld world)
    {
        return world.getSavedData().getOrCreate(ChunkLoadManager::new, NAME);
    }



    public void registerChunk(ServerWorld world, TileEntityChunkLoader loader, long chunk)
    {
        world.getChunkProvider().registerTicket(LOADER_TICKET_TYPE, new ChunkPos(chunk), TICKET_DISTANCE, loader);

        chunks.computeIfAbsent(chunk, k -> new ObjectOpenHashSet<>()).add(loader.getPos());
        markDirty();
    }

    public void releaseChunk(ServerWorld world, TileEntityChunkLoader loader, long chunk)
    {
        world.getChunkProvider().releaseTicket(LOADER_TICKET_TYPE, new ChunkPos(chunk), TICKET_DISTANCE, loader);

        if (chunks.containsKey(chunk))
        {
            chunks.get(chunk).remove(loader.getPos());
            if (chunks.get(chunk).isEmpty())
            {
                chunks.remove(chunk);
            }
            markDirty();
        }
    }

    public boolean isChunkLoadedBy(TileEntityChunkLoader loader, long chunk)
    {
        if (!chunks.containsKey(chunk)) { return false; }
        return chunks.get(chunk).contains(loader.getPos());
    }

    public void loadChunksInitial(ServerWorld world, TileEntityChunkLoader loader, LongSet chunks)
    {
        ServerChunkProvider provider = world.getChunkProvider();
        chunks.forEach((LongConsumer) (chunk -> provider.registerTicket(LOADER_TICKET_TYPE, new ChunkPos(chunk), TICKET_DISTANCE, loader)));
    }



    private void load(ServerWorld world)
    {
        chunks.long2ObjectEntrySet().fastForEach(entry ->
        {
            /*
             * Load all chunks that contain a chunk loader
             */
            ChunkPos chunk = new ChunkPos(entry.getLongKey());
            world.getChunkProvider().registerTicket(STARTUP_TICKET_TYPE, chunk, TICKET_DISTANCE, chunk);
        });
    }

    private void tick(ServerWorld world)
    {
        if (!chunks.isEmpty())
        {
            //This causes roughly the same behaviour as forced chunks in vanilla
            //See ServerWorld line 338 for context
            world.resetUpdateEntityTick();
        }
    }



    @Override
    public void read(CompoundNBT nbt)
    {
        ListNBT entries = nbt.getList("chunks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < entries.size(); i++)
        {
            CompoundNBT tag = entries.getCompound(i);

            long key = tag.getLong("chunk_pos");
            Set<BlockPos> loaderSet = new ObjectOpenHashSet<>();
            chunks.put(key, loaderSet);

            ListNBT loaders = tag.getList("loaders", Constants.NBT.TAG_COMPOUND);
            for (int j = 0; j < loaders.size(); j++)
            {
                loaderSet.add(NBTUtil.readBlockPos(loaders.getCompound(j)));
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        ListNBT entries = new ListNBT();

        chunks.long2ObjectEntrySet().fastForEach(entry ->
        {
            CompoundNBT tag = new CompoundNBT();
            tag.putLong("chunk_pos", entry.getLongKey());

            ListNBT loaders = new ListNBT();
            for (BlockPos loader : entry.getValue())
            {
                loaders.add(NBTUtil.writeBlockPos(loader));
            }
            tag.put("loaders", loaders);

            entries.add(tag);
        });

        nbt.put("chunks", entries);
        return nbt;
    }



    @SubscribeEvent
    public static void onWorldLoad(final WorldEvent.Load event)
    {
        if (!event.getWorld().isRemote())
        {
            ServerWorld world = (ServerWorld) event.getWorld();
            get(world).load(world);
        }
    }

    @SubscribeEvent
    public static void onWorldTick(final TickEvent.WorldTickEvent event)
    {
        if (!event.world.isRemote() && event.phase == TickEvent.Phase.END)
        {
            ServerWorld world = (ServerWorld) event.world;
            get(world).tick(world);
        }
    }
}