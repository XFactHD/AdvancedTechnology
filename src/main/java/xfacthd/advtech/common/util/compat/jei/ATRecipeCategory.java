package xfacthd.advtech.common.util.compat.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.util.TextureDrawer;

public abstract class ATRecipeCategory<R> implements IRecipeCategory<R>
{
    protected static final ResourceLocation ENERGY_BAR = widget("energy_bar");

    private final ResourceLocation uid;
    private final Class<R> recipeClass;
    protected final IGuiHelper guiHelper;
    private final Component title;
    private final IDrawable background;
    private final IDrawable icon;

    public ATRecipeCategory(Class<R> recipeClass, ResourceLocation uid, IGuiHelper guiHelper)
    {
        this.recipeClass = recipeClass;
        this.uid = uid;
        this.guiHelper = guiHelper;
        this.title = new TranslatableComponent("jei_cat." + uid.toString().replace(':', '.'));

        background = createBackground();
        icon = createIcon();
    }

    @Override
    public final ResourceLocation getUid() { return uid; }

    @Override
    public final Class<? extends R> getRecipeClass() { return recipeClass; }

    @Override
    public final Component getTitle() { return title; }

    @Override
    public final IDrawable getBackground() { return background; }

    @Override
    public final IDrawable getIcon() { return icon; }

    protected abstract IDrawable createBackground();

    protected abstract IDrawable createIcon();

    protected final void drawEnergyBar(int x, int y, int energy, int maxEnergy)
    {
        RenderSystem.setShaderTexture(0, ENERGY_BAR);

        float mult = (float) energy / (float) maxEnergy;
        float height = Math.round(mult * 60F);
        float minV = 1F - (height / 60F);
        TextureDrawer.drawTexture(x, y + (60F - height), 12, height, 0, 1, minV, 1);
    }

    protected static ResourceLocation background(String name)
    {
        return new ResourceLocation(AdvancedTechnology.MODID, "textures/gui/" + name + ".png");
    }

    protected static ResourceLocation widget(String name)
    {
        return new ResourceLocation(AdvancedTechnology.MODID, "textures/gui/widgets/" + name + ".png");
    }
}