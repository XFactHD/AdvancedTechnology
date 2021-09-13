package xfacthd.advtech.common.util;

import net.minecraft.world.entity.player.Inventory;
import xfacthd.advtech.common.blockentity.BlockEntityMachine;
import xfacthd.advtech.common.menu.ContainerMenuMachine;

public interface MachineContainerFactory<T extends BlockEntityMachine, M extends ContainerMenuMachine<T>>
{
    M create(int windowId, T machine, Inventory playerInv);
}