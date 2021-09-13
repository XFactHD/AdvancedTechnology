package xfacthd.advtech.common.capability.energy;

public class EnergySink extends EnergyMachine
{
    public EnergySink(int capacity, int maxReceive) { super(capacity, maxReceive, 0); }

    @Override
    public boolean canExtract() { return false; }
}