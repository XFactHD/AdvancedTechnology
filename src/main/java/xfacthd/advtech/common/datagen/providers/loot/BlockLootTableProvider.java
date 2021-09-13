package xfacthd.advtech.common.datagen.providers.loot;

import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.fmllegacy.RegistryObject;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.subtypes.MachineType;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class BlockLootTableProvider extends LootTableProvider
{
    public BlockLootTableProvider(DataGenerator gen) { super(gen); }

    @Override
    public String getName() { return AdvancedTechnology.MODID + ".loot_tables"; }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext context) { /*NOOP*/ }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables()
    {
        return Collections.singletonList(Pair.of(ATBlockLootTable::new, LootContextParamSets.BLOCK));
    }

    private static class ATBlockLootTable extends BlockLoot
    {
        @Override
        protected Iterable<Block> getKnownBlocks()
        {
            List<Block> blocks = new ArrayList<>();

            blocks.addAll(ATContent.ORE_BLOCKS.values().stream().map(RegistryObject::get).collect(Collectors.toList()));
            blocks.addAll(ATContent.STORAGE_BLOCKS.values().stream().map(RegistryObject::get).collect(Collectors.toList()));

            blocks.add(ATContent.machineBlock(MachineType.CASING));
            blocks.add(ATContent.machineBlock(MachineType.ELECTRIC_FURNACE));
            blocks.add(ATContent.machineBlock(MachineType.CRUSHER));
            blocks.add(ATContent.machineBlock(MachineType.ALLOY_SMELTER));
            blocks.add(ATContent.machineBlock(MachineType.METAL_PRESS));
            blocks.add(ATContent.machineBlock(MachineType.LIQUIFIER));
            blocks.add(ATContent.machineBlock(MachineType.CHARGER));
            blocks.add(ATContent.machineBlock(MachineType.PLANTER));
            blocks.add(ATContent.machineBlock(MachineType.HARVESTER));
            blocks.add(ATContent.machineBlock(MachineType.FERTILIZER));

            blocks.add(ATContent.machineBlock(MachineType.BURNER_GENERATOR));
            blocks.add(ATContent.machineBlock(MachineType.SOLAR_PANEL));

            blocks.add(ATContent.BLOCK_ENERGY_CUBE.get());
            blocks.add(ATContent.BLOCK_FLUID_TANK.get());

            blocks.add(ATContent.machineBlock(MachineType.CHUNK_LOADER));

            return blocks;
        }

        @Override
        protected void addTables()
        {
            ATContent.ORE_BLOCKS.forEach((mat, block) ->
            {
                switch (mat)
                {
                    case SULFUR:
                    {
                        add(block.get(), b ->
                                createSilkTouchDispatchTable(b,
                                        applyExplosionDecay(b,
                                                LootItem
                                                        .lootTableItem(ATContent.POWDER_ITEMS.get(mat).get())
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 9.0F)))
                                                        .apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE))
                                        )
                                )
                        );
                        break;
                    }
                    default:
                    {
                        dropSelf(block.get());
                    }
                }
            });

            ATContent.STORAGE_BLOCKS.forEach((mat, block) -> dropSelf(block.get()));

            dropSelf(ATContent.machineBlock(MachineType.CASING));
            dropSelf(ATContent.machineBlock(MachineType.ELECTRIC_FURNACE));
            dropSelf(ATContent.machineBlock(MachineType.CRUSHER));
            dropSelf(ATContent.machineBlock(MachineType.ALLOY_SMELTER));
            dropSelf(ATContent.machineBlock(MachineType.METAL_PRESS));
            dropSelf(ATContent.machineBlock(MachineType.LIQUIFIER));
            dropSelf(ATContent.machineBlock(MachineType.CHARGER));
            dropSelf(ATContent.machineBlock(MachineType.PLANTER));
            dropSelf(ATContent.machineBlock(MachineType.HARVESTER));
            dropSelf(ATContent.machineBlock(MachineType.FERTILIZER));

            dropSelf(ATContent.machineBlock(MachineType.BURNER_GENERATOR));
            dropSelf(ATContent.machineBlock(MachineType.SOLAR_PANEL));

            dropSelf(ATContent.BLOCK_ENERGY_CUBE.get());
            dropSelf(ATContent.BLOCK_FLUID_TANK.get());

            dropSelf(ATContent.machineBlock(MachineType.CHUNK_LOADER));
        }
    }
}