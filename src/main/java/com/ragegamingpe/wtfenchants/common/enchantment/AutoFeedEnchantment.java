package com.ragegamingpe.wtfenchants.common.enchantment;

import com.ragegamingpe.wtfenchants.common.enchantment.base.ModBaseEnchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;

public class AutoFeedEnchantment extends ModBaseEnchantment
{
    private static int delay = 0;

    public AutoFeedEnchantment()
    {
        super("auto_feed", Rarity.UNCOMMON, EnumEnchantmentType.ARMOR_HEAD);
    }

    @Override
    public void onArmorTick(EntityPlayer player, ItemStack armorPiece, int lvl)
    {
        if (++delay >= 100) {
            delay = 0;
            if (lvl >= 2) {
                player.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 5, 0, true, false));
            } else {
                if (player.getFoodStats().needFood()) {
                    ItemStack food = ItemStack.EMPTY;
                    if (player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemFood) {
                        food = player.getHeldItem(EnumHand.OFF_HAND);
                    } else if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemFood) {
                        food = player.getHeldItem(EnumHand.MAIN_HAND);
                    } else {
                        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                            ItemStack itemstack = player.inventory.getStackInSlot(i);

                            if (itemstack.getItem() instanceof ItemFood) {
                                food = itemstack;
                                break;
                            }
                        }
                    }

                    if (food != ItemStack.EMPTY) {
                        player.getFoodStats().addStats((ItemFood) food.getItem(), food);
                        food.shrink(1);
                    }
                }
            }
        }
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel)
    {
        return enchantmentLevel == 1 ? 25 : 99;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel)
    {
        return this.getMinEnchantability(enchantmentLevel) + 20;
    }

    @Override
    public int getMaxLevel()
    {
        return 2;
    }
}
