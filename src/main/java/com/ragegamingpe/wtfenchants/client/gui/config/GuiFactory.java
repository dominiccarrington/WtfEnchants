package com.ragegamingpe.wtfenchants.client.gui.config;

import com.ragegamingpe.wtfenchants.common.helper.Config;
import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GuiFactory implements IModGuiFactory
{
    @Override
    public void initialize(Minecraft minecraftInstance)
    {

    }

    @Override
    public boolean hasConfigGui()
    {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parent)
    {
        return new ConfigScreen(parent);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return null;
    }

    public static class ConfigScreen extends GuiConfig
    {
        public ConfigScreen(GuiScreen parent)
        {
            super(
                    parent,
                    getConfigElements(),
                    LibMisc.MOD_ID,
                    false,
                    false,
                    LibMisc.MOD_NAME,
                    GuiConfig.getAbridgedConfigPath(Config.getInstance().toString())
            );
        }

        private static List<IConfigElement> getConfigElements()
        {
            List<IConfigElement> configElements = new ArrayList<>();
            for (String category : Config.getInstance().getCategoryNames()) {
                configElements.add(
                        new DummyConfigElement.DummyCategoryElement(
                                category,
                                "en_us",
                                new ConfigElement(Config.getInstance().getCategory(category)).getChildElements()
                        )
                );
            }

            return configElements;
        }
    }
}
