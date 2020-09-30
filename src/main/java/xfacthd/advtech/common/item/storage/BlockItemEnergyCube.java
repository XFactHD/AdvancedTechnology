package xfacthd.advtech.common.item.storage;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.render.ister.RenderItemEnergyCube;
import xfacthd.advtech.common.block.storage.BlockEnergyCube;
import xfacthd.advtech.common.capability.energy.ItemEnergyStorage;

import java.util.List;

public class BlockItemEnergyCube extends BlockItem
{
    public static final ITextComponent STORED = new TranslationTextComponent("info." + AdvancedTechnology.MODID + ".stored");

    public BlockItemEnergyCube(BlockEnergyCube block, Properties props)
    {
        super(block, props.setISTER(() -> RenderItemEnergyCube::new));
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt)
    {
        return new ICapabilitySerializable<CompoundNBT>()
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
            public CompoundNBT serializeNBT() { return storage.serializeNBT(); }

            @Override
            public void deserializeNBT(CompoundNBT nbt) { storage.deserializeNBT(nbt); }
        };
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        super.addInformation(stack, world, tooltip, flag);

        stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler ->
        {
            String text = STORED.getFormattedText() + " " + handler.getEnergyStored() + " RF / " + handler.getMaxEnergyStored() + " RF";
            tooltip.add(new StringTextComponent(text));
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
}