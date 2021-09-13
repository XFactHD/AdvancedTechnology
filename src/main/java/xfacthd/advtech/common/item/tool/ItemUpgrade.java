package xfacthd.advtech.common.item.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.states.MachineLevel;

public class ItemUpgrade extends Item
{
    private final MachineLevel level;

    public ItemUpgrade(MachineLevel level)
    {
        super(new Properties().tab(ItemGroups.TOOL_GROUP));
        this.level = level;
    }

    public MachineLevel getLevel() { return level; }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) { return true; }
}