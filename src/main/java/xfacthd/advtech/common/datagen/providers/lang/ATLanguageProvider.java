package xfacthd.advtech.common.datagen.providers.lang;

import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.data.LanguageProvider;
import xfacthd.advtech.AdvancedTechnology;

public abstract class ATLanguageProvider extends LanguageProvider
{
    private final String locale; //Need to store it here aswell because it's private in the super class

    public ATLanguageProvider(DataGenerator gen, String locale)
    {
        super(gen, AdvancedTechnology.MODID, locale);
        this.locale = locale;
    }

    @Override
    public String getName() { return AdvancedTechnology.MODID + ".lang." + locale; }

    protected void add(Component text, String name) { add(text.getString(), name); }
}