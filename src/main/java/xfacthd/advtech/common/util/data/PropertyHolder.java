package xfacthd.advtech.common.util.data;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class PropertyHolder
{
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public static final DirectionProperty FACING_HOR = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
}