package xfacthd.advtech.common.capability.energy;

public class EnergySource extends EnergyMachine
{
    public EnergySource(int capacity, int maxExtract) { super(capacity, 0, maxExtract); }

    @Override
    public boolean canReceive() { return false; }
}