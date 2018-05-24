package com.ragegamingpe.wtfenchants.common.helper;

import com.google.common.base.CaseFormat;
import com.ragegamingpe.wtfenchants.common.block.BlockXpFarmer;
import com.ragegamingpe.wtfenchants.common.block.base.ModBlock;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntityXpFunnel;
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
    public static final String CATEGORY_BLOCK = "blocks";

    public static Map<Class<? extends ModBaseEnchantment>, Boolean> enchantsEnabled = new HashMap<>();
    public static Map<String, Boolean> wtfEventEnabled = new HashMap<>();    // Coming Soon (TM)
    public static Map<Class<? extends ModBlock>, Boolean> blocksEnabled = new HashMap<>(); // Coming Soon (TM)

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
            enchantsEnabled.put(clazz, getInstance().get(CATEGORY_ENCHANTS, key, true).setRequiresMcRestart(true).getBoolean());
        }

        BlockXpFarmer.configRadius = getInstance().get(CATEGORY_BLOCK, "XP Farmer Radius", 5).getInt();
        BlockXpFarmer.configXpPenalty = getInstance().get(CATEGORY_BLOCK, "XP Farmer Penalty", 0.75, "The percentage of the XP that drops. 0 means no XP drops, 1 means no penality", 0, 1).getDouble();

        TileEntityXpFunnel.configTransferPerOperation = getInstance().get(CATEGORY_BLOCK, "XP Funnel Transfer Per Operation", 10, "The maxmium of expirence transferred every 4 redstone ticks").getInt();

        //TODO add WTF enchant effects to config

        if (instance.hasChanged())
            instance.save();
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(LibMisc.MOD_ID)) sync();
    }
}
