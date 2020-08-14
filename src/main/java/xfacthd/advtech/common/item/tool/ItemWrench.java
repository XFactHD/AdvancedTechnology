package xfacthd.advtech.common.item.tool;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.ToolType;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.item.ItemBase;

public class ItemWrench extends ItemBase
{
    public static final ToolType TOOL_WRENCH = ToolType.get("wrench");

    public ItemWrench()
    {
        super("item_wrench",
                ItemGroups.TOOL_GROUP,
                new Properties()
                        .maxStackSize(1)
                        .addToolType(TOOL_WRENCH, 1));
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) { return true; }
}