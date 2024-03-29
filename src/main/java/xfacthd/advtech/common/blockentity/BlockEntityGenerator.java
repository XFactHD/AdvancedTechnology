package xfacthd.advtech.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import xfacthd.advtech.common.capability.energy.EnergySource;
import xfacthd.advtech.common.capability.item.MachineItemStackHandler;

import java.util.EnumMap;
import java.util.Map;

public abstract class BlockEntityGenerator extends BlockEntityInventoryMachine
{
    private final Map<Direction, LazyOptional<IEnergyStorage>> neighborCache = new EnumMap<>(Direction.class);
    protected int productionMult = 1;
    protected int maxExtract = 0;
    protected int progress = -1;

    public BlockEntityGenerator(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

    @Override
    public void tickInternal()
    {
        if (!level().isClientSide() && energyHandler.getEnergyStored() > 0)
        {
            for (Direction side : Direction.values())
            {
                if (supportsEnergyOnSide(side))
                {
                    getNeighboringEnergyHandler(side).ifPresent(handler ->
                    {
                        int rec = handler.receiveEnergy(energyHandler.extractEnergy(maxExtract, true), false);
                        if (rec > 0) { energyHandler.extractEnergy(rec, false); }
                    });

                    if (energyHandler.getEnergyStored() <= 0) { break; }
                }
            }
        }

        super.tick();
    }

    @Override
    public void onLevelChanged()
    {
        int mult = (int)Math.pow(2, level.ordinal());
        productionMult = mult;
        int capacity = getBaseEnergyCapacity() * mult;
        maxExtract = getBaseExtract() * mult;
        energyHandler.reconfigure(capacity, 0, maxExtract);
    }

    @Override
    protected void initCapabilities()
    {
        internalItemHandler = new MachineItemStackHandler(this, 1);

        super.initCapabilities();

        energyHandler = new EnergySource(getBaseEnergyCapacity(), getBaseExtract());
    }

    protected LazyOptional<IEnergyStorage> getNeighboringEnergyHandler(Direction side)
    {
        if (!supportsEnergyOnSide(side)) { return LazyOptional.empty(); }

        LazyOptional<IEnergyStorage> handler = neighborCache.getOrDefault(side, LazyOptional.empty());
        if (!handler.isPresent())
        {
            BlockEntity te = level().getBlockEntity(worldPosition.relative(side));
            if (te != null)
            {
                handler = te.getCapability(CapabilityEnergy.ENERGY, side.getOpposite());
                if (handler.isPresent())
                {
                    handler.addListener(itemHandler -> onEnergyNeighborInvalidate(side));
                    neighborCache.put(side, handler);
                }
            }
        }
        return handler;
    }

    private void onEnergyNeighborInvalidate(Direction side) { neighborCache.put(side, LazyOptional.empty()); }

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
        return Mth.clamp((float)progress / (float) getEnergyPerUnitFuel(), 0, 1);
    }

    @Override
    public boolean canForcePush() { return false; }

    @Override
    protected void pushOutputs() { /*NOOP*/ }

    @Override //TODO: implement enhancement support on generators and make this true
    public boolean supportsEnhancements() { return false; }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);
        progress = nbt.getInt("progress");
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        nbt.putInt("progress", progress);
    }
}