package com.ragegamingpe.wtfenchants.common.enchantment;

import com.ragegamingpe.wtfenchants.common.enchantment.base.ModBaseEnchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import java.util.Random;

public class EnvenomationEnchantment extends ModBaseEnchantment
{
    private static Random rand;
    public EnvenomationEnchantment()
    {
        super("envenomation", Rarity.RARE, EnumEnchantmentType.WEAPON);
    }

    @Override
    public void onEntityDamaged(EntityLivingBase user, Entity target, int level)
    {
        if (rand == null) rand = new Random();

        if (rand.nextFloat() < 0.5 && target instanceof EntityLivingBase) {
            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.POISON, 100, level - 1));
        }
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel)
    {
        return 15 + 5 * enchantmentLevel;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel)
    {
        return this.getMinEnchantability(enchantmentLevel) + 15;
    }

    @Override
    public int getMaxLevel()
    {
        return 3;
    }
}
