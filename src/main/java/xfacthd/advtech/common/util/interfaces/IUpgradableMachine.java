package xfacthd.advtech.common.util.interfaces;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xfacthd.advtech.common.data.states.MachineLevel;
import xfacthd.advtech.common.item.tool.ItemUpgrade;
import xfacthd.advtech.common.util.StatusMessages;

public interface IUpgradableMachine
{
    default InteractionResult upgradeMachine(Level level, Player player, ItemStack stack)
    {
        MachineLevel upgradeLevel = ((ItemUpgrade)stack.getItem()).getLevel();
        if (upgradeLevel.isAfter(getMachineLevel()))
        {
            if (!level.isClientSide())
            {
                upgrade(upgradeLevel);
                if (!player.isCreative())
                {
                    stack.shrink(1);
                    player.getInventory().setChanged();
                }
                player.displayClientMessage(StatusMessages.UPGRADED, false);
            }
            return InteractionResult.CONSUME;
        }
        else
        {
            if (level.isClientSide())
            {
                player.displayClientMessage(StatusMessages.CANT_UPGRADE, false);
            }
            return InteractionResult.FAIL;
        }
    }

    void upgrade(MachineLevel level);

    MachineLevel getMachineLevel();
}