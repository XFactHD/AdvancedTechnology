package xfacthd.advtech.common.data.subtypes;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public enum Enhancement implements StringRepresentable
{
    RANGE(4, true);

    private final int levels;
    private final boolean single;

    Enhancement(int levels, boolean single)
    {
        this.levels = levels;
        this.single = single;
    }

    public int getLevels() { return levels; }

    public boolean singleInstance() { return single; }

    @Override
    public String getSerializedName() { return toString().toLowerCase(Locale.ENGLISH); }

    public Stream<Pair<Enhancement, Integer>> streamLevels()
    {
        return IntStream.range(0, levels).mapToObj(level -> Pair.of(this, level));
    }

    public String toItemName(int level) { return "enhancement_" + getSerializedName() + "_" + level; }
}