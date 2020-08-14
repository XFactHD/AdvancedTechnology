package xfacthd.advtech.common.item.debug;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.loading.FMLEnvironment;
import xfacthd.advtech.common.block.material.BlockOre;
import xfacthd.advtech.common.item.ItemBase;

import java.util.List;

public class ItemBlockRemover extends ItemBase
{
    public ItemBlockRemover() { super("item_block_remover", null, new Properties().maxStackSize(1)); }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
    {
        if (!FMLEnvironment.production && group == ItemGroup.TOOLS)
        {
            items.add(new ItemStack(this));
        }
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player)
    {
        World world = player.world;
        if (!world.isRemote() && player.hasPermissionLevel(2))
        {
            int range = stack.getOrCreateTag().getInt("range");
            Mode mode = Mode.fromStack(stack);

            if (range > 0)
            {
                BlockPos.getAllInBox(pos.add(-range, 0, -range), pos.add(range, 0, range)).forEach(bpos ->
                {
                    BlockState state = world.getBlockState(bpos);
                    if (mode != Mode.SKIP_ORE || !(state.getBlock() instanceof BlockOre))
                    {
                        world.removeBlock(bpos, false);
                    }
                });
            }
        }
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote())
        {
            if (player.isSneaking())
            {
                int mode = stack.getOrCreateTag().getInt("mode");
                mode = (mode + 1) % Mode.values().length;
                //noinspection ConstantConditions
                stack.getTag().putInt("mode", mode);
            }
            else
            {
                int range = stack.getOrCreateTag().getInt("range");

                range++;
                if (range > 5) { range = 1; }

                //noinspection ConstantConditions
                stack.getTag().putInt("range", range);
            }
        }

        return ActionResult.resultSuccess(stack);
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        super.addInformation(stack, world, tooltip, flag);

        int range = stack.getOrCreateTag().getInt("range");
        Mode mode = Mode.fromStack(stack);

        tooltip.add(new StringTextComponent("Radius: " + range));
        tooltip.add(new StringTextComponent("Mode: " + mode.getInfo()));
    }

    private enum Mode
    {
        ALL("Remove all"),
        SKIP_ORE("Skip AT Ores");

        private final String info;

        Mode(String info) { this.info = info; }

        public String getInfo() { return info; }

        public static Mode fromStack(ItemStack stack)
        {
            //noinspection ConstantConditions
            return values()[stack.getTag().getInt("mode")];
        }
    }
}