package xfacthd.advtech.common.item.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import xfacthd.advtech.common.data.ItemGroups;

public class ItemWrench extends Item
{
    public ItemWrench()
    {
        super(new Properties()
                .stacksTo(1)
                .tab(ItemGroups.TOOL_GROUP)
        );
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) { return true; }
}