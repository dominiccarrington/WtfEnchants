package com.ragegamingpe.wtfenchants.common.helper;

import com.google.common.base.CaseFormat;
import com.ragegamingpe.wtfenchants.common.enchantment.base.ModBaseEnchantment;
import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import com.ragegamingpe.wtfenchants.common.lib.ModEnchantments;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;

public class Config
{
    public static final String CATEGORY_ENCHANTS = "enchantments";

    public static Map<Class<? extends ModBaseEnchantment>, Boolean> enchantsEnabled = new HashMap<>();
    private static Configuration instance;
    private static Side side;

    public static void setConfigInstance(Configuration config, Side side)
    {
        if (instance != null) throw new RuntimeException("Cannot override config instance");

        instance = config;
        Config.side = side;
    }

    public static Configuration getInstance()
    {
        return instance;
    }

    public static void sync()
    {
        enchantsEnabled.clear();
        for (ModBaseEnchantment enchantment : ModEnchantments.ALL_ENCHANTMENTS) {
            Class<? extends ModBaseEnchantment> clazz = enchantment.getClass();

            String key = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getSimpleName().replace("Enchantment", ""));
            enchantsEnabled.put(clazz, Config.getBoolean(CATEGORY_ENCHANTS, key, true, true, true));
        }

        //TODO add WTF enchant effects to config

        if (instance.hasChanged())
            instance.save();
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(LibMisc.MOD_ID)) sync();
    }


    public static boolean getBoolean(String category, String key, boolean def, boolean worldRestart, boolean clientRestart)
    {
        return getInstance().get(category, key, def)
                .setRequiresWorldRestart(worldRestart)
                .setRequiresMcRestart(clientRestart)
                .getBoolean();
    }

    public static boolean getBoolean(String category, String key, boolean def, boolean worldRestart)
    {
        return getBoolean(category, key, def, worldRestart, false);
    }

    public static boolean getBoolean(String category, String key, boolean def)
    {
        return getBoolean(category, key, def, false, false);
    }
}
