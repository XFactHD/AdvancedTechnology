package xfacthd.advtech.common.item.tool;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.subtypes.Enhancements;
import xfacthd.advtech.common.item.ItemBase;

import java.util.*;

public class ItemEnhancement extends ItemBase
{
    private final Enhancements type;
    private final int level;

    public ItemEnhancement(Enhancements type, int level)
    {
        super(type.getName() + "_" + level, ItemGroups.TOOL_GROUP);
        this.type = type;
        this.level = level;
    }

    public Enhancements getType() { return type; }

    public int getLevel() { return level; }

    public static class Holder
    {
        private final Map<Enhancements, Int2ObjectMap<ItemEnhancement>> items = new EnumMap<>(Enhancements.class);

        private void addItem(ItemEnhancement item)
        {
            if (!items.containsKey(item.type))
            {
                items.put(item.type, new Int2ObjectArrayMap<>(item.type.getLevels()));
            }

            items.get(item.type).put(item.level, item);
        }

        public Int2ObjectMap<ItemEnhancement> get(Enhancements upgrade) { return items.get(upgrade); }

        public ItemEnhancement[] registerItems()
        {
            ATContent.itemEnhancement = new Holder();
            List<ItemEnhancement> items = new ArrayList<>();

            for (Enhancements type : Enhancements.values())
            {
                for (int level = 0; level < type.getLevels(); level++)
                {
                    ItemEnhancement item = new ItemEnhancement(type, level);

                    ATContent.itemEnhancement.addItem(item);
                    items.add(item);
                }
            }

            return items.toArray(new ItemEnhancement[0]);
        }
    }
}