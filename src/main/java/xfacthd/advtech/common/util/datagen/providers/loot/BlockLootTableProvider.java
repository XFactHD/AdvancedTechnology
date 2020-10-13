package xfacthd.advtech.common.util.datagen.providers.loot;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.functions.ApplyBonus;
import net.minecraft.world.storage.loot.functions.SetCount;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;

import java.util.*;
import java.util.function.*;

public class BlockLootTableProvider extends LootTableProvider
{
    public BlockLootTableProvider(DataGenerator gen) { super(gen); }

    @Override
    public String getName() { return AdvancedTechnology.MODID + ".loot_tables"; }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker tracker) { /*NOOP*/ }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables()
    {
        return Collections.singletonList(Pair.of(ATBlockLootTable::new, LootParameterSets.BLOCK));
    }

    private static class ATBlockLootTable extends BlockLootTables
    {
        @Override
        protected Iterable<Block> getKnownBlocks()
        {
            List<Block> blocks = new ArrayList<>();

            blocks.addAll(ATContent.blockOre.values());
            blocks.addAll(ATContent.blockStorage.values());

            blocks.add(ATContent.blockMachineCasing);
            blocks.add(ATContent.blockElectricFurnace);
            blocks.add(ATContent.blockCrusher);
            blocks.add(ATContent.blockAlloySmelter);
            blocks.add(ATContent.blockMetalPress);
            blocks.add(ATContent.blockLiquifier);
            blocks.add(ATContent.blockCharger);
            blocks.add(ATContent.blockPlanter);
            blocks.add(ATContent.blockHarvester);
            blocks.add(ATContent.blockFertilizer);

            blocks.add(ATContent.blockBurnerGenerator);

            blocks.add(ATContent.blockEnergyCube);
            blocks.add(ATContent.blockFluidTank);

            blocks.add(ATContent.blockChunkLoader);

            return blocks;
        }

        @Override
        protected void addTables()
        {
            ATContent.blockOre.forEach((mat, block) ->
            {
                switch (mat)
                {
                    case SULFUR:
                    {
                        registerLootTable(block, b ->
                                droppingWithSilkTouch(b,
                                        withExplosionDecay(b,
                                                ItemLootEntry
                                                        .builder(ATContent.itemPowder.get(mat))
                                                        .acceptFunction(SetCount.builder(RandomValueRange.of(3.0F, 9.0F)))
                                                        .acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE))
                                        )
                                )
                        );
                        break;
                    }
                    default:
                    {
                        registerDropSelfLootTable(block);
                    }
                }
            });

            ATContent.blockStorage.forEach((mat, block) -> registerDropSelfLootTable(block));

            registerDropSelfLootTable(ATContent.blockMachineCasing);
            registerDropSelfLootTable(ATContent.blockElectricFurnace);
            registerDropSelfLootTable(ATContent.blockCrusher);
            registerDropSelfLootTable(ATContent.blockAlloySmelter);
            registerDropSelfLootTable(ATContent.blockMetalPress);
            registerDropSelfLootTable(ATContent.blockLiquifier);
            registerDropSelfLootTable(ATContent.blockCharger);
            registerDropSelfLootTable(ATContent.blockPlanter);
            registerDropSelfLootTable(ATContent.blockHarvester);
            registerDropSelfLootTable(ATContent.blockFertilizer);

            registerDropSelfLootTable(ATContent.blockBurnerGenerator);

            registerDropSelfLootTable(ATContent.blockEnergyCube);
            registerDropSelfLootTable(ATContent.blockFluidTank);

            registerDropSelfLootTable(ATContent.blockChunkLoader);
        }
    }
}