package xfacthd.advtech.common.container.energy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.*;
import net.minecraftforge.energy.CapabilityEnergy;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.container.AdvancedContainer;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.data.types.ContainerTypes;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.net.packets.energy.PacketConfigEnergyCubeSide;
import xfacthd.advtech.common.tileentity.energy.TileEntityEnergyCube;

public class ContainerEnergyCube extends AdvancedContainer
{
    private final TileEntityEnergyCube cube;
    private final IWorldPosCallable worldPosCallable;
    private final IntReferenceHolder energyHolder = IntReferenceHolder.single();
    private final IntReferenceHolder capacityHolder = IntReferenceHolder.single();
    private final IIntArray portArray = new IntArray(6);

    public ContainerEnergyCube(int id, TileEntityEnergyCube cube, PlayerInventory inventory)
    {
        super(ContainerTypes.containerTypeEnergyCube, id, inventory);
        this.cube = cube;

        //noinspection ConstantConditions
        worldPosCallable = IWorldPosCallable.of(cube.getWorld(), cube.getPos());

        trackRealInt(energyHolder);
        trackRealInt(capacityHolder);
        trackIntArray(portArray);
    }

    @Override
    public void detectAndSendChanges()
    {
        cube.getCapability(CapabilityEnergy.ENERGY).ifPresent(energy ->
        {
            energyHolder.set(energy.getEnergyStored());
            capacityHolder.set(energy.getMaxEnergyStored());
        });

        for (Side side : Side.values())
        {
            portArray.set(side.ordinal(), cube.getSidePort(side).ordinal());
        }

        super.detectAndSendChanges();
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) { return isWithinUsableDistance(worldPosCallable, player, ATContent.blockEnergyCube); }

    public final int getStoredEnergy() { return energyHolder.get(); }

    public final int getEnergyCapacity() { return capacityHolder.get(); }

    public final int getEnergyLevel() { return cube.getEnergyLevel(); }

    public final SideAccess getPortSetting(Side side) { return SideAccess.values()[portArray.get(side.ordinal())]; }

    public void configureSide(Side side, int dir)
    {
        NetworkHandler.sendToServer(new PacketConfigEnergyCubeSide(cube.getPos(), side, dir));
    }
}