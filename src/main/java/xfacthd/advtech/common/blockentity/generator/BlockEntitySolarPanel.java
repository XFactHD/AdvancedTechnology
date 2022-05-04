package xfacthd.advtech.common.blockentity.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.BlockEntityMachine;
import xfacthd.advtech.common.capability.energy.EnergySource;
import xfacthd.advtech.common.data.subtypes.MachineType;

import java.lang.ref.WeakReference;
import java.util.*;

public class BlockEntitySolarPanel extends BlockEntityMachine
{
    public static final Component TITLE = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".solar_panel");
    private static final int BASE_CAPACITY = 20000;
    private static final int BASE_GENERATION = 20;
    private static final int BASE_EXTRACT = 80;

    private LazyOptional<IEnergyStorage> energyBelow = LazyOptional.empty();
    private int generation = 0;
    private int maxExtract = 0;
    private boolean seesSky = false;

    public BlockEntitySolarPanel(BlockPos pos, BlockState state)
    {
        super(ATContent.machineEntity(MachineType.SOLAR_PANEL), pos, state, true);
    }

    @Override
    public void tickInternal()
    {
        if (!level().isClientSide())
        {
            if (seesSky && level().isDay())
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
    }

    private LazyOptional<IEnergyStorage> getEnergyBelow()
    {
        if (!energyBelow.isPresent())
        {
            BlockEntity te = level().getBlockEntity(worldPosition.below());
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
    public void onLoad()
    {
        super.onLoad();
        SolarSkyCheckManager.get(level()).addPanel(this);
    }

    @Override
    public void setRemoved()
    {
        super.setRemoved();
        SolarSkyCheckManager.get(level()).removePanel(this);
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
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        nbt.putBoolean("seesSky", seesSky);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);
        seesSky = nbt.getBoolean("seesSky");
    }

    @Override
    public Component getDisplayName() { return TITLE; }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player)
    {
        return null;
    }



    @Mod.EventBusSubscriber(modid = AdvancedTechnology.MODID)
    public static class SolarSkyCheckManager
    {
        private static final WeakHashMap<Level, SolarSkyCheckManager> managers = new WeakHashMap<>();

        private final List<BlockPos> positions = new ArrayList<>();
        private final List<BlockPos> invalid = new ArrayList<>();
        private final Map<BlockPos, WeakReference<BlockEntitySolarPanel>> panels = new HashMap<>();
        private int panelIdx = 0;

        @SubscribeEvent
        public static void onWorldTick(final TickEvent.WorldTickEvent event)
        {
            if (!event.world.isClientSide() && event.phase == TickEvent.Phase.START)
            {
                get(event.world).tick();
            }
        }

        private SolarSkyCheckManager() {}

        private void tick()
        {
            if (!panels.isEmpty())
            {
                BlockEntitySolarPanel panel;
                do
                {
                    BlockPos pos = positions.get(panelIdx);
                    panel = panels.get(pos).get();
                    if (panel != null)
                    {
                        panel.seesSky = panel.level().canSeeSky(panel.worldPosition);
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

        private void addPanel(BlockEntitySolarPanel panel)
        {
            positions.add(panel.worldPosition);
            panels.put(panel.worldPosition, new WeakReference<>(panel));
        }

        private void removePanel(BlockEntitySolarPanel panel)
        {
            positions.remove(panel.worldPosition);
            panels.remove(panel.worldPosition);
        }

        private static SolarSkyCheckManager get(Level level)
        {
            return managers.computeIfAbsent(level, l -> new SolarSkyCheckManager());
        }
    }
}