package com.ragegamingpe.wtfenchants.common.enchantment;

import com.ragegamingpe.wtfenchants.common.enchantment.base.ModBaseEnchantment;
import net.minecraft.enchantment.EnumEnchantmentType;

public class HeightEnchantment extends ModBaseEnchantment
{
    public HeightEnchantment()
    {
        super("height", Rarity.RARE, EnumEnchantmentType.DIGGER);
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel)
    {
        return 25 + 5 * enchantmentLevel;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel)
    {
        return super.getMaxEnchantability(enchantmentLevel) + 30;
    }

    @Override
    public int getMaxLevel()
    {
        return 3;
    }
}
