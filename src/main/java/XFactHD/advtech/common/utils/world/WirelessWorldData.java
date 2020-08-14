/*  Copyright (C) <2016>  <XFactHD>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses. */

package XFactHD.advtech.common.utils.world;

import XFactHD.advtech.api.util.DimensionalBlockPos;
import XFactHD.advtech.common.blocks.energy.TileEntityWirelessNode;
import XFactHD.advtech.common.utils.caps.energy.CombinedEnergyStorageWireless;
import XFactHD.advtech.common.utils.helpers.LogHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class WirelessWorldData extends WorldSavedData
{
    private static WirelessWorldData INSTANCE = null;
    private static final String NAME = "ADVTECH:WIRELESSDATA";
    private HashMap<UUID, CombinedEnergyStorageWireless> perPlayerStorage = new HashMap<>();
    private HashMap<UUID, ArrayList<DimensionalBlockPos>> feeders = new HashMap<>();
    private HashMap<UUID, ArrayList<DimensionalBlockPos>> nodes = new HashMap<>();

    public WirelessWorldData(String name)
    {
        super(name);
    }

    private WirelessWorldData()
    {
        super(NAME);
    }

    public static WirelessWorldData get(World world)
    {
        if (INSTANCE != null) { return INSTANCE; }
        if (world.getMapStorage() != null)
        {
            WirelessWorldData data = (WirelessWorldData) world.getMapStorage().getOrLoadData(WirelessWorldData.class, NAME);
            if (data == null)
            {
                data = new WirelessWorldData();
                world.getMapStorage().setData(NAME, data);
            }
            INSTANCE = data;
            return INSTANCE;
        }
        throw new IllegalStateException("World#getMapStorage() can't be null at this point!");
    }

    public void addWirelessFeeder(UUID owner, BlockPos pos, int dimension)
    {
        if (!feeders.containsKey(owner)){ feeders.put(owner, new ArrayList<>()); }
        if (!perPlayerStorage.containsKey(owner)) { perPlayerStorage.put(owner, new CombinedEnergyStorageWireless()); }
        feeders.get(owner).add(new DimensionalBlockPos(pos, dimension));
        perPlayerStorage.get(owner).adjustCapacity(feeders.get(owner).size());
        markDirty();
    }

    public void removeWirelessFeeder(UUID owner, BlockPos pos, int dimension)
    {
        feeders.get(owner).remove(new DimensionalBlockPos(pos, dimension));
        perPlayerStorage.get(owner).adjustCapacity(feeders.get(owner).size());
        markDirty();
    }

    public void addWirelessNode(UUID owner, BlockPos pos, int dimension)
    {
        if (!nodes.containsKey(owner)) { nodes.put(owner, new ArrayList<>()); }
        nodes.get(owner).add(new DimensionalBlockPos(pos, dimension));
        markDirty();
    }

    public void removeWirelessNode(UUID owner, BlockPos pos, int dimension)
    {
        nodes.get(owner).remove(new DimensionalBlockPos(pos, dimension));
        markDirty();
    }

    public CombinedEnergyStorageWireless getPerPlayerStorage(UUID owner)
    {
        return perPlayerStorage.get(owner);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        NBTTagList storageList = nbt.getTagList("storage", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < storageList.tagCount(); i++)
        {
            NBTTagCompound storage = storageList.getCompoundTagAt(i);
            UUID owner = UUID.fromString(storage.getString("owner"));
            CombinedEnergyStorageWireless cesw = new CombinedEnergyStorageWireless();
            cesw.deserializeNBT(storage.getCompoundTag("storage"));
            perPlayerStorage.put(owner, cesw);
        }
        NBTTagList feederList = nbt.getTagList("feeders", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < feederList.tagCount(); i++)
        {
            NBTTagCompound feeder = feederList.getCompoundTagAt(i);
            UUID owner = UUID.fromString(feeder.getString("owner"));
            ArrayList<DimensionalBlockPos> positions = new ArrayList<>();
            NBTTagList posList = feeder.getTagList("list", Constants.NBT.TAG_COMPOUND);
            for (int j = 0; j < posList.tagCount(); j++)
            {
                NBTTagCompound posTag = posList.getCompoundTagAt(j);
                DimensionalBlockPos pos = new DimensionalBlockPos(BlockPos.fromLong(posTag.getLong("pos")), posTag.getInteger("dimension"));
                positions.add(pos);
            }
            feeders.put(owner, positions);
        }
        NBTTagList nodeList = nbt.getTagList("nodes", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < nodeList.tagCount(); i++)
        {
            NBTTagCompound node = nodeList.getCompoundTagAt(i);
            UUID owner = UUID.fromString(node.getString("owner"));
            ArrayList<DimensionalBlockPos> positions = new ArrayList<>();
            NBTTagList posList = node.getTagList("list", Constants.NBT.TAG_COMPOUND);
            for (int j = 0; j < posList.tagCount(); j++)
            {
                NBTTagCompound posTag = posList.getCompoundTagAt(j);
                DimensionalBlockPos pos = new DimensionalBlockPos(BlockPos.fromLong(posTag.getLong("pos")), posTag.getInteger("dimension"));
                positions.add(pos);
            }
            nodes.put(owner, positions);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        NBTTagList storageList = new NBTTagList();
        for (UUID uuid : perPlayerStorage.keySet())
        {
            NBTTagCompound storage = new NBTTagCompound();
            storage.setTag("storage", perPlayerStorage.get(uuid).serializeNBT());
            storage.setString("owner", uuid.toString());
            storageList.appendTag(storage);
        }
        NBTTagList feederList = new NBTTagList();
        for (UUID uuid : feeders.keySet())
        {
            NBTTagCompound feeder = new NBTTagCompound();
            NBTTagList posList = new NBTTagList();
            for (DimensionalBlockPos pos : feeders.get(uuid))
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setLong("pos", pos.toLong());
                tag.setInteger("dimension", pos.getDimension());
                posList.appendTag(tag);
            }
            feeder.setTag("list", posList);
            feeder.setString("owner", uuid.toString());
            feederList.appendTag(feeder);
        }
        NBTTagList nodeList = new NBTTagList();
        for (UUID uuid : nodes.keySet())
        {
            NBTTagCompound node = new NBTTagCompound();
            NBTTagList posList = new NBTTagList();
            for (DimensionalBlockPos pos : nodes.get(uuid))
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setLong("pos", pos.toLong());
                tag.setInteger("dimension", pos.getDimension());
                posList.appendTag(tag);
            }
            node.setTag("list", posList);
            node.setString("owner", uuid.toString());
            nodeList.appendTag(node);
        }
        nbt.setTag("storage", storageList);
        nbt.setTag("feeders", feederList);
        nbt.setTag("nodes", nodeList);
        return nbt;
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (event.world != server.getEntityWorld()) { return; }
            WirelessWorldData data = get(server.getEntityWorld());
            for (UUID uuid : data.perPlayerStorage.keySet())
            {
                if (data.nodes.containsKey(uuid) && !data.nodes.get(uuid).isEmpty() && data.perPlayerStorage.containsKey(uuid))
                {
                    for (DimensionalBlockPos pos : data.nodes.get(uuid))
                    {
                        World world = server.worldServers[pos.getDimension()];
                        if (world.isBlockLoaded(pos))
                        {
                            TileEntity te = world.getTileEntity(pos);
                            if (te instanceof TileEntityWirelessNode)
                            {
                                CombinedEnergyStorageWireless storage = data.perPlayerStorage.get(uuid);
                                long toSend = ((TileEntityWirelessNode)te).receivePowerFromNetwork(storage.getMaxTransfer(), true);
                                ((TileEntityWirelessNode)te).receivePowerFromNetwork(storage.takePower(toSend, false), false);
                            }
                        }
                    }
                }
            }
        }
    }
}