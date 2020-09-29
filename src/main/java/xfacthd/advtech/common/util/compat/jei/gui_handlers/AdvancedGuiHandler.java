package xfacthd.advtech.common.util.compat.jei.gui_handlers;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rectangle2d;
import xfacthd.advtech.client.gui.AdvancedScreen;
import xfacthd.advtech.client.gui.tabs.AbstractTab;

import java.util.ArrayList;
import java.util.List;

public class AdvancedGuiHandler<T extends AdvancedScreen<?>> implements IGuiContainerHandler<T>
{
    @Override
    public List<Rectangle2d> getGuiExtraAreas(T screen)
    {
        List<Rectangle2d> areas = new ArrayList<>();
        List<AbstractTab<?>> tabs = screen.getTabs();
        if (!tabs.isEmpty())
        {
            tabs.forEach(tab -> areas.add(tab.getKeepOut()));
        }
        return areas;
    }
}