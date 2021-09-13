package xfacthd.advtech.common.item.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.subtypes.Enhancement;

public class ItemEnhancement extends Item
{
    private final Enhancement type;
    private final int level;

    public ItemEnhancement(Enhancement type, int level)
    {
        super(new Properties().tab(ItemGroups.TOOL_GROUP));
        this.type = type;
        this.level = level;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) { return true; }

    public Enhancement getType() { return type; }

    public int getLevel() { return level; }
}