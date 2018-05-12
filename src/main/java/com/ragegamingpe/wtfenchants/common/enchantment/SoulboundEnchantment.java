package com.ragegamingpe.wtfenchants.common.enchantment;

import com.ragegamingpe.wtfenchants.common.enchantment.base.ModBaseEnchantment;
import com.ragegamingpe.wtfenchants.common.enchantment.handler.HandlerSoulbound;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraftforge.common.MinecraftForge;

public class SoulboundEnchantment extends ModBaseEnchantment
{
    public SoulboundEnchantment()
    {
        super("soulbound", Rarity.RARE, EnumEnchantmentType.ALL);
        MinecraftForge.EVENT_BUS.register(new HandlerSoulbound());
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel)
    {
        return 25;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel)
    {
        return this.getMinEnchantability(enchantmentLevel) + 15;
    }
}
