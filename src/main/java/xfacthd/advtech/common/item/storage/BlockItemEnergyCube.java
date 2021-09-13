package xfacthd.advtech.common.item.storage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.render.item.RenderItemEnergyCube;
import xfacthd.advtech.common.block.storage.BlockEnergyCube;
import xfacthd.advtech.common.capability.energy.ItemEnergyStorage;

import java.util.List;
import java.util.function.Consumer;

public class BlockItemEnergyCube extends BlockItem
{
    public static final Component STORED = new TranslatableComponent("info." + AdvancedTechnology.MODID + ".stored");

    public BlockItemEnergyCube(BlockEnergyCube block, Properties props) { super(block, props); }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt)
    {
        return new ICapabilitySerializable<CompoundTag>()
        {
            private final ItemEnergyStorage storage = new ItemEnergyStorage();
            private final LazyOptional<ItemEnergyStorage> lazyStorage = LazyOptional.of(() -> storage);

            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
            {
                if (cap == CapabilityEnergy.ENERGY)
                {
                    return lazyStorage.cast();
                }
                return LazyOptional.empty();
            }

            @Override
            public CompoundTag serializeNBT() { return storage.serializeNBT(); }

            @Override
            public void deserializeNBT(CompoundTag nbt) { storage.deserializeNBT(nbt); }
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag)
    {
        super.appendHoverText(stack, level, tooltip, flag);

        stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler ->
        {
            String text = " " + handler.getEnergyStored() + " RF / " + handler.getMaxEnergyStored() + " RF";
            tooltip.add(STORED.copy().append(new TextComponent(text)));
        });
    }

    public static int getEnergyLevel(ItemStack stack)
    {
        return stack.getCapability(CapabilityEnergy.ENERGY).map(handler ->
        {
            int stored = handler.getEnergyStored();
            int capacity = handler.getMaxEnergyStored();
            return Math.round(((float)stored / (float)capacity) * 10F);
        }).orElse(0);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer)
    {
        consumer.accept(new IItemRenderProperties()
        {
            private final RenderItemEnergyCube stackRenderer = Minecraft.getInstance() != null ? new RenderItemEnergyCube(
                    Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                    Minecraft.getInstance().getEntityModels()
            ) : null;

            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() { return stackRenderer; }
        });
    }
}