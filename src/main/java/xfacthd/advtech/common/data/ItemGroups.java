package xfacthd.advtech.common.data;

import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.block.storage.*;
import xfacthd.advtech.common.block.material.*;
import xfacthd.advtech.common.data.sorting.*;
import xfacthd.advtech.common.data.subtypes.*;
import xfacthd.advtech.common.item.material.*;
import xfacthd.advtech.common.item.tool.*;

import java.util.Comparator;

public class ItemGroups
{
    public static final ATItemGroup MATERIAL_GROUP = new ATItemGroup("materials", new MaterialComparator());
    public static final ATItemGroup TOOL_GROUP = new ATItemGroup("tools", new ToolComparator());
    public static final ATItemGroup MACHINE_GROUP = new ATItemGroup("machines", new MachineComparator());

    public static void finalizeItemGroups()
    {
        MATERIAL_GROUP.setIconStack(new ItemStack(ATContent.blockStorage.get(Materials.COPPER)));
        TOOL_GROUP.setIconStack(new ItemStack(ATContent.itemWrench));
        MACHINE_GROUP.setIconStack(new ItemStack(ATContent.blockElectricFurnace));
    }

    public static final class ATItemGroup extends ItemGroup
    {
        private ItemStack iconStack = ItemStack.EMPTY;
        private final Comparator<ItemStack> comp;

        private ATItemGroup(String name) { this(name, null); }

        private ATItemGroup(String name, Comparator<ItemStack> comp)
        {
            super("group." + AdvancedTechnology.MODID + "." + name);
            this.comp = comp;
        }

        @Override
        public void fill(NonNullList<ItemStack> items)
        {
            super.fill(items);
            if (comp != null) { items.sort(comp); }
        }

        private void setIconStack(ItemStack stack) { this.iconStack = stack; }

        @Override
        public ItemStack createIcon() { return iconStack; }
    }

    private static final class MaterialComparator implements Comparator<ItemStack>
    {
        @Override
        public int compare(ItemStack s1, ItemStack s2)
        {
            if (s1.getItem() instanceof BlockItem && s2.getItem() instanceof BlockItem)
            {
                Block b1 = ((BlockItem)s1.getItem()).getBlock();
                Block b2 = ((BlockItem)s2.getItem()).getBlock();

                if (b1 instanceof BlockOre && b2 instanceof BlockOre)
                {
                    return ((BlockOre)b1).getMaterial().compareTo(((BlockOre)b2).getMaterial());
                }
                else if (b1 instanceof BlockStorage && b2 instanceof BlockStorage)
                {
                    return ((BlockStorage)b1).getMaterial().compareTo(((BlockStorage)b2).getMaterial());
                }
                else if (b1 instanceof BlockOre && b2 instanceof BlockStorage)
                {
                    return -1;
                }
                else if (b1 instanceof BlockStorage && b2 instanceof BlockOre)
                {
                    return 1;
                }
            }
            else if (s1.getItem() instanceof ItemPowder && s2.getItem() instanceof ItemPowder)
            {
                return ((ItemPowder)s1.getItem()).getMaterial().compareTo(((ItemPowder)s2.getItem()).getMaterial());
            }
            else if (s1.getItem() instanceof ItemIngot && s2.getItem() instanceof ItemIngot)
            {
                return ((ItemIngot)s1.getItem()).getMaterial().compareTo(((ItemIngot)s2.getItem()).getMaterial());
            }
            else if (s1.getItem() instanceof ItemMaterial && s2.getItem() instanceof ItemIngot)
            {
                return ((ItemMaterial)s1.getItem()).getMaterial().compareTo(((ItemIngot)s2.getItem()).getMaterial());
            }
            else if (s1.getItem() instanceof ItemIngot && s2.getItem() instanceof ItemMaterial)
            {
                return ((ItemIngot)s1.getItem()).getMaterial().compareTo(((ItemMaterial)s2.getItem()).getMaterial());
            }
            else if (s1.getItem() instanceof ItemMaterial && s2.getItem() instanceof ItemMaterial)
            {
                return ((ItemMaterial)s1.getItem()).getMaterial().compareTo(((ItemMaterial)s2.getItem()).getMaterial());
            }
            else if (s1.getItem() instanceof ItemNugget && s2.getItem() instanceof ItemNugget)
            {
                return ((ItemNugget)s1.getItem()).getMaterial().compareTo(((ItemNugget)s2.getItem()).getMaterial());
            }
            else if (s1.getItem() instanceof ItemGear && s2.getItem() instanceof ItemGear)
            {
                return ((ItemGear)s1.getItem()).getMaterial().compareTo(((ItemGear)s2.getItem()).getMaterial());
            }
            else if (s1.getItem() instanceof ItemPlate && s2.getItem() instanceof ItemPlate)
            {
                return ((ItemPlate)s1.getItem()).getMaterial().compareTo(((ItemPlate)s2.getItem()).getMaterial());
            }
            else if (s1.getItem() instanceof ItemComponent && s2.getItem() instanceof ItemComponent)
            {
                return ((ItemComponent)s1.getItem()).getComponent().compareTo(((ItemComponent)s2.getItem()).getComponent());
            }
            return type(s1).compareTo(type(s2));
        }

        private Type type(ItemStack stack)
        {
            if (stack.getItem() instanceof BlockItem)
            {
                Block b = ((BlockItem)stack.getItem()).getBlock();
                if (b instanceof BlockOre) { return Type.ORE; }
                else if (b instanceof BlockStorage) { return Type.STORAGE; }
            }
            else if (stack.getItem() instanceof ItemPowder) { return Type.POWDER; }
            else if (stack.getItem() instanceof ItemIngot || stack.getItem() instanceof ItemMaterial)
            {
                return Type.INGOT;
            }
            else if (stack.getItem() instanceof ItemNugget) { return Type.NUGGET; }
            else if (stack.getItem() instanceof ItemGear) { return Type.GEAR; }
            else if (stack.getItem() instanceof ItemPlate) { return Type.PLATE; }
            else if (stack.getItem() instanceof ItemComponent) { return Type.COMPONENT; }

            throw new IllegalArgumentException("The item '" + stack.getItem().getRegistryName() + "' should not be in this ItemGroup!");
        }

        private enum Type
        {
            ORE,
            STORAGE,
            POWDER,
            INGOT,
            NUGGET,
            GEAR,
            PLATE,
            COMPONENT
        }
    }

    private static final class ToolComparator implements Comparator<ItemStack>
    {
        @Override
        public int compare(ItemStack s1, ItemStack s2)
        {
            if (s1.getItem() instanceof ItemUpgrade && s2.getItem() instanceof ItemUpgrade)
            {
                return ((ItemUpgrade) s1.getItem()).getLevel().compareTo(((ItemUpgrade) s2.getItem()).getLevel());
            }
            else if (s1.getItem() instanceof ItemEnhancement && s2.getItem() instanceof ItemEnhancement)
            {
                ItemEnhancement i1 = (ItemEnhancement)s1.getItem();
                ItemEnhancement i2 = (ItemEnhancement)s2.getItem();

                if (i1.getType() == i2.getType())
                {
                    return Integer.compare(i1.getLevel(), i2.getLevel());
                }
                return i1.getType().compareTo(i2.getType());
            }
            return 0;
        }
    }

    private static final class MachineComparator implements Comparator<ItemStack>
    {
        @Override
        public int compare(ItemStack s1, ItemStack s2)
        {
            Block b1 = ((BlockItem)s1.getItem()).getBlock();
            Block b2 = ((BlockItem)s2.getItem()).getBlock();

            if (b1 instanceof BlockMachine && b2 instanceof BlockMachine)
            {
                return ((BlockMachine) b1).getType().compareTo(((BlockMachine) b2).getType());
            }
            return category(s1).compareTo(category(s2));
        }

        private MachineCategory category(ItemStack stack)
        {
            if (!(stack.getItem() instanceof BlockItem)) { throw new IllegalArgumentException("Machine item group can only contain blocks!"); }

            Block b = ((BlockItem)stack.getItem()).getBlock();
            if (b instanceof BlockMachine) { return ((BlockMachine)b).getType().getCategory(); }
            else if (b instanceof BlockEnergyCube || b instanceof BlockFluidTank) { return MachineCategory.STORAGE; }

            throw new IllegalArgumentException("The item '" + stack.getItem().getRegistryName() + "' should not be in this ItemGroup!");
        }
    }
}