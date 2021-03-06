package xfacthd.advtech.common.data.types;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.IForgeRegistry;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.tileentity.debug.*;
import xfacthd.advtech.common.tileentity.storage.*;
import xfacthd.advtech.common.tileentity.generator.*;
import xfacthd.advtech.common.tileentity.machine.*;
import xfacthd.advtech.common.tileentity.utility.*;

import java.util.function.Supplier;

public class TileEntityTypes
{
    private static IForgeRegistry<TileEntityType<?>> typeRegistry;

    //Debug
    public static TileEntityType<TileEntityCreativeEnergySource>    tileTypeCreativeEnergySource;
    public static TileEntityType<TileEntityCreativeItemSource>      tileTypeCreativeItemSource;
    public static TileEntityType<TileEntityCreativeFluidSource>     tileTypeCreativeFluidSource;

    //Machine
    public static TileEntityType<TileEntityElectricFurnace> tileTypeElectricFurnace;
    public static TileEntityType<TileEntityCrusher>         tileTypeCrusher;
    public static TileEntityType<TileEntityAlloySmelter>    tileTypeAlloySmelter;
    public static TileEntityType<TileEntityMetalPress>      tileTypeMetalPress;
    public static TileEntityType<TileEntityLiquifier>       tileTypeLiquifier;
    public static TileEntityType<TileEntityCharger>         tileTypeCharger;
    public static TileEntityType<TileEntityPlanter>         tileTypePlanter;
    public static TileEntityType<TileEntityHarvester>       tileTypeHarvester;
    public static TileEntityType<TileEntityFertilizer>      tileTypeFertilizer;

    //Generator
    public static TileEntityType<TileEntityBurnerGenerator> tileTypeBurnerGenerator;

    //Storage
    public static TileEntityType<TileEntityEnergyCube>      tileTypeEnergyCube;
    public static TileEntityType<TileEntityFluidTank>       tileTypeFluidTank;

    //Utility
    public static TileEntityType<TileEntityChunkLoader>     tileTypeChunkLoader;

    public static void setRegistry(IForgeRegistry<TileEntityType<?>> registry) { typeRegistry = registry; }

    public static<T extends TileEntity> TileEntityType<T> create(Supplier<T> factory, String name, Block... blocks)
    {
        //noinspection ConstantConditions
        TileEntityType<T> tileType = TileEntityType.Builder.create(factory, blocks).build(null);
        tileType.setRegistryName(AdvancedTechnology.MODID, name);
        typeRegistry.register(tileType);
        return tileType;
    }
}