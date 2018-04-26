package com.ragegamingpe.wtfenchants.common.enchantment;

import com.ragegamingpe.wtfenchants.common.enchantment.base.ModBaseEnchantment;
import com.ragegamingpe.wtfenchants.common.lib.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class ExplosionEnchantment extends ModBaseEnchantment
{
    public ExplosionEnchantment()
    {
        super("explosion", Rarity.VERY_RARE, EnumEnchantmentType.ARMOR, new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET});
    }

    @Override
    public void onEntityDeath(EntityLivingBase entity, DamageSource source, ItemStack enchantItem, Integer lvl)
    {
        World world = entity.getEntityWorld();
        world.createExplosion(entity, entity.posX, entity.posY, entity.posZ, 1.5F * lvl, false);
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel)
    {
        return 20 + (enchantmentLevel - 1) * 4;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel)
    {
        return this.getMinEnchantability(enchantmentLevel) + 30;
    }

    @Override
    public int getMaxLevel()
    {
        return 5;
    }

    @Override
    protected boolean canApplyTogether(Enchantment ench)
    {
        return super.canApplyTogether(ench) && ench != ModEnchantments.SOULBOUND;
    }
}
