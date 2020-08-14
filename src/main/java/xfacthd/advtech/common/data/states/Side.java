package xfacthd.advtech.common.data.states;

import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;
import java.util.function.Function;

public enum Side implements IStringSerializable
{
    BOTTOM((facing) -> Direction.DOWN),
    TOP((facing) -> Direction.UP),
    FRONT((facing) -> facing),
    BACK(Direction::getOpposite),
    RIGHT(Direction::rotateYCCW),
    LEFT(Direction::rotateY);

    private final String name = toString().toLowerCase(Locale.ENGLISH);
    private final Function<Direction, Direction> dirMapper;

    Side(Function<Direction, Direction> dirMapper) { this.dirMapper = dirMapper; }

    public Direction mapFacing(Direction facing) { return dirMapper.apply(facing); }

    @Override
    public String getName() { return name; }
}