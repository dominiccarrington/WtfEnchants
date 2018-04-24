package com.ragegamingpe.wtfenchants.common.enchantment;

import com.ragegamingpe.wtfenchants.common.enchantment.base.ModBaseEnchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

public class SoulboundEnchantment extends ModBaseEnchantment
{
    public SoulboundEnchantment()
    {
        super("soulbound", Rarity.RARE, EnumEnchantmentType.ALL, EntityEquipmentSlot.values());
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
