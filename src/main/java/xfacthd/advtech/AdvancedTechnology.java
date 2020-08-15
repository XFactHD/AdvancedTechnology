package xfacthd.advtech;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import xfacthd.advtech.client.util.ClientHelper;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.util.*;
import xfacthd.advtech.common.util.data.RecipeReloadListener;
import xfacthd.advtech.common.world.OreGenerator;

@Mod(AdvancedTechnology.MODID)
public class AdvancedTechnology
{
    public static final String MODID = "advtech";
    public static final ISidedHelper SIDED_HELPER = DistExecutor.safeRunForDist(() -> ClientHelper::new, () -> ServerHelper::new);

    public AdvancedTechnology()
    {
        LogHelper.setLogger(LogManager.getLogger());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModConfig);

        MinecraftForge.EVENT_BUS.addListener(this::onServerAboutToStart);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);

        //ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);
        //ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        NetworkHandler.initPackets();

        //noinspection deprecation
        DeferredWorkQueue.runLater(OreGenerator::addOreFeatures);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {

    }

    private void processIMC(final InterModProcessEvent event)
    {

    }

    private void onModConfig(final ModConfig.ModConfigEvent event)
    {

    }

    private void onServerAboutToStart(final FMLServerAboutToStartEvent event)
    {
        event.getServer().getResourceManager().addReloadListener(new RecipeReloadListener());
    }

    private void onServerStarting(final FMLServerStartingEvent event)
    {
        //Register commands
    }
}
