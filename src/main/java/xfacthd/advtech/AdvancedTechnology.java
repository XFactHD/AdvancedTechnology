package xfacthd.advtech;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.util.LogHelper;
import xfacthd.advtech.common.util.data.RecipeReloadListener;

@Mod(AdvancedTechnology.MODID)
public class AdvancedTechnology
{
    public static final String MODID = "advtech";

    public AdvancedTechnology()
    {
        LogHelper.setLogger(LogManager.getLogger());

        ATContent.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onSetup);

        MinecraftForge.EVENT_BUS.addListener(this::onRegisterReloadListeners);

        //ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);
        //ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    private void onSetup(final FMLCommonSetupEvent event)
    {
        NetworkHandler.initPackets();
    }

    private void onRegisterReloadListeners(final AddReloadListenerEvent event)
    {
        //event.addListener(new RecipeReloadListener()); //FIXME: deadlocks the server on startup
    }
}