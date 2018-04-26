package com.ragegamingpe.wtfenchants.common.enchantment;

import com.ragegamingpe.wtfenchants.common.enchantment.base.ModBaseEnchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.Random;

public class DisarmEnchantment extends ModBaseEnchantment
{
    private static Random rand;

    public DisarmEnchantment()
    {
        super("disarm", Rarity.RARE, EnumEnchantmentType.WEAPON);
    }

    @Override
    public void onEntityDamaged(EntityLivingBase user, Entity target, int level)
    {
        if (rand == null) rand = new Random();

        if (target instanceof EntityLivingBase) {
            if (rand.nextFloat() < 0.03 * level) {
                EntityLivingBase livingBase = (EntityLivingBase) target;
                EntityEquipmentSlot[] slots = EntityEquipmentSlot.values();
                int index = (int) Math.ceil(rand.nextFloat() * slots.length) - 1;

                EntityEquipmentSlot chosen;
                int tries = 16;
                do {
                    chosen = slots[index];
                    if (livingBase.getItemStackFromSlot(chosen) == ItemStack.EMPTY) chosen = null;
                    tries--;
                } while (chosen == null && tries > 0);

                if (tries > 0) {
                    ItemStack stack = livingBase.getItemStackFromSlot(chosen);
                    livingBase.entityDropItem(stack, 0.1F);
                    livingBase.setItemStackToSlot(chosen, ItemStack.EMPTY);
                }
            }
        }
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel)
    {
        return 20 + 2 * enchantmentLevel;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel)
    {
        return this.getMinEnchantability(enchantmentLevel) + 15;
    }

    @Override
    public int getMaxLevel()
    {
        return 10;
    }
}
