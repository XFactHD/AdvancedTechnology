package xfacthd.advtech.common.data.states;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.function.Function;

public enum Side implements StringRepresentable
{
    BOTTOM((facing) -> Direction.DOWN),
    TOP((facing) -> Direction.UP),
    FRONT((facing) -> facing),
    BACK(Direction::getOpposite),
    RIGHT(Direction::getCounterClockWise),
    LEFT(Direction::getClockWise);

    private final String name = toString().toLowerCase(Locale.ENGLISH);
    private final Function<Direction, Direction> dirMapper;

    Side(Function<Direction, Direction> dirMapper) { this.dirMapper = dirMapper; }

    public Direction mapFacing(Direction facing) { return dirMapper.apply(facing); }

    @Override
    public String getSerializedName() { return name; }
}