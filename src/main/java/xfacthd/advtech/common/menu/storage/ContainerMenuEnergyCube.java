package xfacthd.advtech.common.menu.storage;

import com.google.common.base.Preconditions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.storage.BlockEntityEnergyCube;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.menu.AdvancedContainerMenu;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.net.packets.energy.PacketConfigEnergyCubeSide;
import xfacthd.advtech.common.util.sync.ByteDataSlot;

public class ContainerMenuEnergyCube extends AdvancedContainerMenu
{
    private final BlockEntityEnergyCube cube;
    private final ContainerLevelAccess levelAccess;
    private final DataSlot energyHolder = DataSlot.standalone();
    private final DataSlot capacityHolder = DataSlot.standalone();
    private final ByteDataSlot[] portArray = new ByteDataSlot[6];

    public ContainerMenuEnergyCube(int windowId, BlockEntityEnergyCube cube, Inventory inventory)
    {
        super(ATContent.MENU_ENERGY_CUBE.get(), windowId, inventory);
        this.cube = cube;

        levelAccess = ContainerLevelAccess.create(cube.level(), cube.getBlockPos());

        addIntDataSlot(energyHolder);
        addIntDataSlot(capacityHolder);
        for (int i = 0; i < 6; i++)
        {
            portArray[i] = new ByteDataSlot();
            addByteDataSlot(portArray[i]);
        }
    }

    @Override
    public void broadcastChanges()
    {
        cube.getCapability(CapabilityEnergy.ENERGY).ifPresent(energy ->
        {
            energyHolder.set(energy.getEnergyStored());
            capacityHolder.set(energy.getMaxEnergyStored());
        });

        for (Side side : Side.values())
        {
            portArray[side.ordinal()].set((byte) cube.getSidePort(side).ordinal());
        }

        super.broadcastChanges();
    }

    @Override
    public boolean stillValid(Player player) { return stillValid(levelAccess, player, ATContent.BLOCK_ENERGY_CUBE.get()); }

    public final int getStoredEnergy() { return energyHolder.get(); }

    public final int getEnergyCapacity() { return capacityHolder.get(); }

    public final int getEnergyLevel() { return cube.getEnergyLevel(); }

    public final SideAccess getPortSetting(Side side) { return SideAccess.byId(portArray[side.ordinal()].get()); }

    public void configureSide(Side side, int dir)
    {
        NetworkHandler.sendToServer(new PacketConfigEnergyCubeSide(cube.getBlockPos(), side, dir));
    }

    public static ContainerMenuEnergyCube create(int windowId, Inventory inventory, FriendlyByteBuf data)
    {
        BlockEntity be = inventory.player.level.getBlockEntity(data.readBlockPos());
        Preconditions.checkState(be instanceof BlockEntityEnergyCube);
        return new ContainerMenuEnergyCube(windowId, (BlockEntityEnergyCube) be, inventory);
    }
}