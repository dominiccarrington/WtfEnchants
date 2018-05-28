package com.ragegamingpe.wtfenchants.common.lib;

import com.ragegamingpe.wtfenchants.common.enchantment.*;
import com.ragegamingpe.wtfenchants.common.enchantment.base.ModBaseEnchantment;

import java.util.ArrayList;
import java.util.List;

public class ModEnchantments
{
    public static final List<ModBaseEnchantment> ALL_ENCHANTMENTS = new ArrayList<>();

    public static final ModBaseEnchantment AUTO_FEED;
    public static final ModBaseEnchantment AUTO_SMELT;
    public static final ModBaseEnchantment DISARM;
    public static final ModBaseEnchantment ENVENOMATION;
    public static final ModBaseEnchantment EXPLOSION;
    public static final ModBaseEnchantment FELLING;
    public static final ModBaseEnchantment GODSEYE;
    public static final ModBaseEnchantment HEIGHT;
    public static final ModBaseEnchantment QUICK_DRAW;
    public static final ModBaseEnchantment SOULBOUND;
    public static final SuperSoftEnchantment SUPER_SOFT;
    public static final ModBaseEnchantment WIDTH;
    public static final ModBaseEnchantment WTF;

    static {
        AUTO_FEED = new AutoFeedEnchantment();
        AUTO_SMELT = new AutoSmeltEnchantment();
        DISARM = new DisarmEnchantment();
        ENVENOMATION = new EnvenomationEnchantment();
        EXPLOSION = new ExplosionEnchantment();
        FELLING = new FellingEnchantment();
        GODSEYE = new GodsEyeEnchantment();
        HEIGHT = new HeightEnchantment();
        QUICK_DRAW = new QuickDrawEnchantment();
        SOULBOUND = new SoulboundEnchantment();
        SUPER_SOFT = new SuperSoftEnchantment();
        WIDTH = new WidthEnchantment();
        WTF = new WtfEnchantment();
    }
}
