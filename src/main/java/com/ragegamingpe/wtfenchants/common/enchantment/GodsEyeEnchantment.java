package com.ragegamingpe.wtfenchants.common.enchantment;

import com.ragegamingpe.wtfenchants.common.enchantment.base.ModBaseEnchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class GodsEyeEnchantment extends ModBaseEnchantment
{
    public GodsEyeEnchantment()
    {
        super("gods_eye", Rarity.RARE, EnumEnchantmentType.ARMOR_HEAD, new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD});
    }

    @Override
    public void onArmorTick(EntityPlayer player, ItemStack armorPiece, int lvl)
    {
        player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 220, 0, true, false));
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel)
    {
        return 30;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel)
    {
        return this.getMinEnchantability(enchantmentLevel) + 30;
    }

    @Override
    public boolean isTreasureEnchantment()
    {
        return true;
    }
}
