package xfacthd.advtech.common.util.data;

import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.util.Direction;

public class PropertyHolder
{
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public static final DirectionProperty FACING_HOR = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
}