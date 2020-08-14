/*  Copyright (C) <2017>  <XFactHD>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses. */

package XFactHD.advtech.common.blocks;

import XFactHD.advtech.api.block.IInventoryTile;
import XFactHD.advtech.common.items.ItemBlockBase;
import XFactHD.advtech.common.items.tool.ItemTool;
import XFactHD.advtech.common.utils.Reference;
import XFactHD.advtech.common.utils.helpers.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class BlockBase extends Block
{
    private static IForgeRegistry<Block> registry;
    private static List<ItemBlock> itemBlocksToRegister = new ArrayList<>();
    private String[] subnames;

    public BlockBase(String name, Material material, CreativeTabs creativeTab, Class<? extends ItemBlockBase> itemBlockClass, String[] subnames)
    {
        super(material);
        this.subnames = subnames;
        setCreativeTab(creativeTab);
        setRegistryName(new ResourceLocation(Reference.MOD_ID, name));
        setUnlocalizedName(getRegistryName().toString());
        registry.register(this);
        registerItemBlock(itemBlockClass);
    }

    public String[] getSubnames()
    {
        return subnames;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list)
    {
        if (subnames != null)
        {
            for (int i = 0; i < subnames.length; i++)
            {
                list.add(new ItemStack(item, 1, i));
            }
        }
        else
        {
            list.add(new ItemStack(item));
        }
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return subnames != null ? state.getBlock().getMetaFromState(state) : 0;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end)
    {
        AxisAlignedBB aabb = getCollisionBoundingBox(state, world, pos);
        return rayTrace(pos, start, end, aabb != null ? aabb : Block.FULL_BLOCK_AABB);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        //noinspection ConstantConditions
        return getCollisionBoundingBox(state, world, pos).offset(pos);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (heldItem != null && heldItem.getItem() instanceof ItemTool && heldItem.getMetadata() == 0)
        {
            if (onBlockWrenched(world, pos, state, player, side, player.isSneaking()))
            {
                return true;
            }
            return onBlockActivatedSimple(world, pos, state, player, heldItem, side);
        }
        return onBlockActivatedSimple(world, pos, state, player, heldItem, side);
    }

    protected boolean onBlockActivatedSimple(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack heldItem, EnumFacing side)
    {
        return false;
    }

    //FIXME: wrenching a block doesn't notify the blocks around it leading to for example the solar panel wasting energy by not storing it
    protected boolean onBlockWrenched(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, boolean sneaking)
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return isNormalBlock(state);
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return isNormalBlock(state);
    }

    @Override
    public boolean isFullBlock(IBlockState state)
    {
        return isNormalBlock(state);
    }

    public boolean isNormalBlock(IBlockState state)
    {
        return true;
    }

    protected void dropBlockAsItemSpecial(World world, BlockPos pos, ItemStack stack)
    {
        if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops") && !world.restoringBlockSnapshots)
        {
            if (captureDrops.get()) { capturedDrops.get().add(stack); return; }
            double xOff = (double)(world.rand.nextFloat() * 0.5F) + 0.25D;
            double yOff = (double)(world.rand.nextFloat() * 0.5F) + 0.25D;
            double zOff = (double)(world.rand.nextFloat() * 0.5F) + 0.25D;
            EntityItem entity = new EntityItem(world, (double)pos.getX() + xOff, (double)pos.getY() + yOff, (double)pos.getZ() + zOff, stack);
            entity.setDefaultPickupDelay();
            world.spawnEntityInWorld(entity);
        }
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        List<ItemStack> stacks = super.getDrops(world, pos, state, fortune);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof IInventoryTile && ((IInventoryTile)te).hasInventory())
        {
            IItemHandler handler = ((IInventoryTile)te).getInventory();
            for (int i = 0; i < handler.getSlots(); i++)
            {
                stacks.add(handler.getStackInSlot(i));
            }
        }
        return stacks;
    }

    public BlockBase registerTileEntity(Class<? extends TileEntityBase> teClass)
    {
        String id = teClass.getSimpleName().replace("TileEntity", "");
        GameRegistry.registerTileEntity(teClass, Reference.MOD_ID + ":tile" + id);
        return this;
    }

    private void registerItemBlock(Class<? extends ItemBlockBase> itemBlockClass)
    {
        if (itemBlockClass == null){ return; }
        try
        {
            itemBlocksToRegister.add(itemBlockClass.getConstructor(BlockBase.class).newInstance(this));
        }
        catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            LogHelper.fatal("Failed creating an ItemBlock for Block %s!", getRegistryName().toString());
            e.printStackTrace();
        }
    }

    protected float getRandomSoundPitch(World world)
    {
        return (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F;
    }

    public static void setRegistry(IForgeRegistry<Block> registry)
    {
        BlockBase.registry = registry;
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry)
    {
        registry.registerAll(itemBlocksToRegister.toArray(new ItemBlock[itemBlocksToRegister.size()]));
        itemBlocksToRegister.clear();
    }
}