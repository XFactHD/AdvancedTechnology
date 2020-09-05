package xfacthd.advtech.common.data.types;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.container.energy.*;
import xfacthd.advtech.common.container.generator.*;
import xfacthd.advtech.common.container.machine.*;
import xfacthd.advtech.common.container.utility.*;

public class ContainerTypes
{
    private static IForgeRegistry<ContainerType<?>> typeRegistry;

    public static ContainerType<ContainerElectricFurnace>   containerTypeElectricFurnace;
    public static ContainerType<ContainerCrusher>           containerTypeCrusher;
    public static ContainerType<ContainerAlloySmelter>      containerTypeAlloySmelter;
    public static ContainerType<ContainerMetalPress>        containerTypeMetalPress;

    public static ContainerType<ContainerBurnerGenerator>   containerTypeBurnerGenerator;

    public static ContainerType<ContainerEnergyCube>        containerTypeEnergyCube;

    public static ContainerType<ContainerChunkLoader>       containerTypeChunkLoader;

    public static void setRegistry(IForgeRegistry<ContainerType<?>> registry) { typeRegistry = registry; }

    public static<T extends Container> ContainerType<T> create(String name, IContainerFactory<T> factory)
    {
        ContainerType<T> type = IForgeContainerType.create(factory);
        type.setRegistryName(AdvancedTechnology.MODID, name);
        typeRegistry.register(type);
        return type;
    }
}