package xfacthd.advtech.common.util;

import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;
import xfacthd.advtech.common.blockentity.BlockEntityBase;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.util.data.PropertyHolder;
import xfacthd.advtech.common.util.interfaces.ISidedMachine;

import java.util.Map;

public class MachineUtils
{
    public static <T extends BlockEntityBase & ISidedMachine, C> void remapMachinePorts(
            T be,
            Map<Side, SideAccess> ports,
            Map<Direction, SideAccess> cardinalPorts,
            Map<Direction, C> capabilities,
            Map<Direction, LazyOptional<C>> lazyCapabilities
    )
    {
        Direction facing = be.getBlockState().getValue(PropertyHolder.FACING_HOR);

        boolean changed = false;
        for (Side side : Side.values())
        {
            SideAccess setting = ports.get(side);
            Direction dir = side.mapFacing(facing);

            if (cardinalPorts.get(dir) != setting)
            {
                cardinalPorts.put(dir, setting);
                changed = true;
            }

            if (lazyCapabilities.get(dir).isPresent() && setting.isDisabled())
            {
                lazyCapabilities.get(dir).invalidate();
            }
            else if (!lazyCapabilities.get(dir).isPresent() && !setting.isDisabled())
            {
                lazyCapabilities.put(dir, LazyOptional.of(() -> capabilities.get(dir)));
            }
        }

        be.onPortMappingChanged(facing);

        if (be.level().isClientSide()) { be.markRenderUpdate(); }
        else if (changed) { be.markForSync(); }
    }
}