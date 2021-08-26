package xfacthd.advtech.common.tileentity.generator;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.capability.energy.EnergySource;
import xfacthd.advtech.common.data.types.TileEntityTypes;
import xfacthd.advtech.common.tileentity.TileEntityMachine;

import java.lang.ref.WeakReference;
import java.util.*;

public class TileEntitySolarPanel extends TileEntityMachine //FIXME: level color not visible
{
    public static final ITextComponent TITLE = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".solar_panel");
    private static final int BASE_CAPACITY = 20000;
    private static final int BASE_GENERATION = 200;
    private static final int BASE_EXTRACT = 800;

    private LazyOptional<IEnergyStorage> energyBelow = LazyOptional.empty();
    private int generation = 0;
    private int maxExtract = 0;
    private boolean seesSky = false;

    public TileEntitySolarPanel() { super(TileEntityTypes.tileTypeSolarPanel, true); }

    @Override
    public void tick()
    {
        //noinspection ConstantConditions
        if (!world.isRemote())
        {
            if (seesSky && world.isDaytime())
            {
                energyHandler.receiveEnergyInternal(generation, false);
            }

            if (energyHandler.getEnergyStored() > 0)
            {
                getEnergyBelow().ifPresent(handler ->
                {
                    int rec = handler.receiveEnergy(energyHandler.extractEnergy(maxExtract, true), false);
                    if (rec > 0) { energyHandler.extractEnergy(rec, false); }
                });
            }
        }
        super.tick();
    }

    private LazyOptional<IEnergyStorage> getEnergyBelow()
    {
        if (!energyBelow.isPresent())
        {
            //noinspection ConstantConditions
            TileEntity te = world.getTileEntity(pos.down());
            if (te != null)
            {
                energyBelow = te.getCapability(CapabilityEnergy.ENERGY, Direction.UP);
                if (energyBelow.isPresent())
                {
                    energyBelow.addListener(handler -> energyBelow = LazyOptional.empty());
                }
            }
        }
        return energyBelow;
    }

    @Override
    public void onLevelChanged()
    {
        int mult = (int)Math.pow(2, level.ordinal());
        generation = BASE_GENERATION * mult;
        int capacity = getBaseEnergyCapacity() * mult;
        maxExtract = BASE_EXTRACT * mult;
        energyHandler.reconfigure(capacity, 0, maxExtract);
    }

    @Override
    protected void initCapabilities() { energyHandler = new EnergySource(getBaseEnergyCapacity(), BASE_EXTRACT); }

    @Override
    public void validate()
    {
        super.validate();
        SolarSkyCheckManager.get(world).addPanel(this);
    }

    @Override
    public void remove()
    {
        super.remove();
        SolarSkyCheckManager.get(world).removePanel(this);
    }

    @Override
    public float getProgress() { return 0; }

    @Override
    protected boolean supportsEnergyOnSide(Direction side) { return side == Direction.DOWN; }

    @Override //TODO: implement support
    public boolean supportsEnhancements() { return false; }

    @Override
    protected int getBaseEnergyCapacity() { return BASE_CAPACITY; }

    public int getGeneration() { return generation; }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putBoolean("seesSky", seesSky);
        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        seesSky = nbt.getBoolean("seesSky");
    }

    @Override
    public ITextComponent getDisplayName() { return TITLE; }

    @Override
    public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player)
    {
        return null;
    }



    @Mod.EventBusSubscriber(modid = AdvancedTechnology.MODID)
    public static class SolarSkyCheckManager
    {
        private static final WeakHashMap<World, SolarSkyCheckManager> managers = new WeakHashMap<>();

        private final List<BlockPos> positions = new ArrayList<>();
        private final List<BlockPos> invalid = new ArrayList<>();
        private final Map<BlockPos, WeakReference<TileEntitySolarPanel>> panels = new HashMap<>();
        private int panelIdx = 0;

        @SubscribeEvent
        public static void onWorldTick(final TickEvent.WorldTickEvent event)
        {
            if (!event.world.isRemote() && event.phase == TickEvent.Phase.START)
            {
                get(event.world).tick();
            }
        }

        private SolarSkyCheckManager() {}

        private void tick()
        {
            if (!panels.isEmpty())
            {
                TileEntitySolarPanel panel;
                do
                {
                    BlockPos pos = positions.get(panelIdx);
                    panel = panels.get(pos).get();
                    if (panel != null)
                    {
                        //noinspection ConstantConditions
                        panel.seesSky = panel.world.canBlockSeeSky(panel.pos);
                    }
                    else
                    {
                        invalid.add(pos);
                    }
                    panelIdx = (panelIdx + 1) % positions.size();
                } while (panel == null);

                if (!invalid.isEmpty())
                {
                    invalid.forEach(panels::remove);
                    positions.removeAll(invalid);
                    invalid.clear();

                    if (panelIdx >= positions.size()) { panelIdx = 0; }
                }
            }
        }

        private void addPanel(TileEntitySolarPanel panel)
        {
            positions.add(panel.pos);
            panels.put(panel.pos, new WeakReference<>(panel));
        }

        private void removePanel(TileEntitySolarPanel panel)
        {
            positions.remove(panel.pos);
            panels.remove(panel.pos);
        }

        private static SolarSkyCheckManager get(World world)
        {
            return managers.computeIfAbsent(world, w -> new SolarSkyCheckManager());
        }
    }
}