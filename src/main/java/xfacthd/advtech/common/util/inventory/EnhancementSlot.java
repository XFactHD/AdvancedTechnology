package xfacthd.advtech.common.util.inventory;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class EnhancementSlot extends SlotItemHandler
{
    private boolean enabled = false;

    public EnhancementSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition)
    {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isActive() { return enabled; }

    public void setActive(boolean enabled) { this.enabled = enabled; }
}