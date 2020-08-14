package xfacthd.advtech.common.item.tool;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.states.MachineLevel;
import xfacthd.advtech.common.item.ItemBase;

import java.util.*;

public class ItemUpgrade extends ItemBase
{
    private final MachineLevel level;

    public ItemUpgrade(MachineLevel level)
    {
        super("item_upgrade_" + level.getName(), ItemGroups.TOOL_GROUP);
        this.level = level;
    }

    public MachineLevel getLevel() { return level; }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) { return true; }

    public static ItemUpgrade[] registerItems()
    {
        ATContent.itemUpgrade = new EnumMap<>(MachineLevel.class);
        List<ItemUpgrade> items = new ArrayList<>();

        for (MachineLevel level : MachineLevel.values())
        {
            if (level == MachineLevel.BASIC) { continue; }

            ItemUpgrade item = new ItemUpgrade(level);
            ATContent.itemUpgrade.put(level, item);
            items.add(item);
        }

        return items.toArray(items.toArray(new ItemUpgrade[0]));
    }
}