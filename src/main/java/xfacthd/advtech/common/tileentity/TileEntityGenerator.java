package xfacthd.advtech.common.tileentity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.MathHelper;
import xfacthd.advtech.common.capability.energy.EnergySource;
import xfacthd.advtech.common.capability.item.MachineItemStackHandler;

public abstract class TileEntityGenerator extends TileEntityInventoryMachine
{
    protected int productionMult = 1;
    protected int progress = -1;

    public TileEntityGenerator(TileEntityType<?> type) { super(type); }

    @Override
    public void onLevelChanged()
    {
        int mult = (int)Math.pow(2, level.ordinal());
        productionMult = mult;
        int capacity = getBaseEnergyCapacity() * mult;
        int maxExtract = getBaseExtract() * mult;
        energyHandler.reconfigure(capacity, 0, maxExtract);
    }

    @Override
    protected void initCapabilities()
    {
        internalItemHandler = new MachineItemStackHandler(this, 1);

        super.initCapabilities();

        energyHandler = new EnergySource(getBaseEnergyCapacity(), getBaseExtract());
    }

    protected abstract int getBaseExtract();

    protected boolean generateEnergy() { return true; }

    protected final boolean energyNotFull()
    {
        return energyHandler.getEnergyStored() < (energyHandler.getMaxEnergyStored() - getEnergyProduction());
    }

    protected abstract int getEnergyProduction();

    protected abstract int getEnergyPerUnitFuel();

    @Override
    public final float getProgress()
    {
        if (progress == -1) { return 0; }
        return MathHelper.clamp((float)progress / (float) getEnergyPerUnitFuel(), 0, 1);
    }

    @Override
    public boolean canForcePush() { return false; }

    @Override
    protected void pushOutputs() { /*NOOP*/ }

    @Override //TODO: implement enhancement support on generators and make this true
    public boolean supportsEnhancements() { return false; }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        progress = nbt.getInt("progress");
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putInt("progress", progress);
        return super.write(nbt);
    }
}