package xfacthd.advtech.client.util;

public interface IRangedMachine
{
    int getRadius();

    default boolean isAreaCentered() { return false; }

    boolean showArea();
}