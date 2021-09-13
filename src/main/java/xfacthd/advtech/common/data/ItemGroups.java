package xfacthd.advtech.common.data;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.*;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.block.storage.BlockEnergyCube;
import xfacthd.advtech.common.block.storage.BlockFluidTank;
import xfacthd.advtech.common.data.sorting.MachineCategory;
import xfacthd.advtech.common.data.sorting.MaterialCategory;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.data.subtypes.MaterialType;
import xfacthd.advtech.common.item.material.ItemComponent;
import xfacthd.advtech.common.item.tool.ItemEnhancement;
import xfacthd.advtech.common.item.tool.ItemUpgrade;
import xfacthd.advtech.common.util.interfaces.IMaterialObject;

import java.util.Comparator;

public class ItemGroups
{
    public static final ATItemGroup MATERIAL_GROUP = new ATItemGroup("materials", new MaterialComparator());
    public static final ATItemGroup TOOL_GROUP = new ATItemGroup("tools", new ToolComparator());
    public static final ATItemGroup MACHINE_GROUP = new ATItemGroup("machines", new MachineComparator());

    public static void finalizeItemGroups()
    {
        MATERIAL_GROUP.setIconStack(new ItemStack(ATContent.STORAGE_BLOCKS.get(MaterialType.SILVER).get()));
        TOOL_GROUP.setIconStack(new ItemStack(ATContent.ITEM_WRENCH.get()));
        MACHINE_GROUP.setIconStack(new ItemStack(ATContent.MACHINE_BLOCKS.get(MachineType.ELECTRIC_FURNACE).get()));
    }

    public static final class ATItemGroup extends CreativeModeTab
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
        public void fillItemList(NonNullList<ItemStack> items)
        {
            super.fillItemList(items);
            if (comp != null) { items.sort(comp); }
        }

        private void setIconStack(ItemStack stack) { this.iconStack = stack; }

        @Override
        public ItemStack makeIcon() { return iconStack; }
    }

    private static final class MaterialComparator implements Comparator<ItemStack>
    {
        @Override
        public int compare(ItemStack s1, ItemStack s2)
        {
            if (s1.getItem() instanceof IMaterialObject obj1 && s2.getItem() instanceof IMaterialObject obj2)
            {
                if (obj1.getCategory() != obj2.getCategory())
                {
                    return obj1.getCategory().compareTo(obj2.getCategory());
                }
                return obj1.getMaterial().compareTo(obj2.getMaterial());
            }
            else if (s1.getItem() instanceof ItemComponent c1 && s2.getItem() instanceof ItemComponent c2)
            {
                return c1.getComponent().compareTo(c2.getComponent());
            }
            return category(s1).compareTo(category(s2));
        }

        private MaterialCategory category(ItemStack stack)
        {
            if (stack.getItem() instanceof ItemComponent)
            {
                return MaterialCategory.COMPONENT;
            }
            else if (stack.getItem() instanceof IMaterialObject obj)
            {
                return obj.getCategory();
            }

            throw new IllegalArgumentException("The item '" + stack.getItem().getRegistryName() + "' should not be in this ItemGroup!");
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
            else if (s1.getItem() instanceof ItemEnhancement i1 && s2.getItem() instanceof ItemEnhancement i2)
            {
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
            if (!(s1.getItem() instanceof BlockItem item1))
            {
                throw new IllegalArgumentException("Machine item group can only contain blocks, '" + s1.getItem().getRegistryName() + "' is not a block!");
            }
            if (!(s2.getItem() instanceof BlockItem item2))
            {
                throw new IllegalArgumentException("Machine item group can only contain blocks, '" + s2.getItem().getRegistryName() + "' is not a block!");
            }

            if (item1.getBlock() instanceof BlockMachine m1 && item2.getBlock() instanceof BlockMachine m2)
            {
                return m1.getType().compareTo(m2.getType());
            }
            return category(s1).compareTo(category(s2));
        }

        private MachineCategory category(ItemStack stack)
        {
            if (!(stack.getItem() instanceof BlockItem item)) { throw new IllegalArgumentException("Machine item group can only contain blocks!"); }

            if (item.getBlock() instanceof BlockMachine machine)
            {
                return machine.getType().getCategory();
            }
            else if (item.getBlock() instanceof BlockEnergyCube || item.getBlock() instanceof BlockFluidTank)
            {
                return MachineCategory.STORAGE;
            }

            throw new IllegalArgumentException("The item '" + stack.getItem().getRegistryName() + "' should not be in this ItemGroup!");
        }
    }
}