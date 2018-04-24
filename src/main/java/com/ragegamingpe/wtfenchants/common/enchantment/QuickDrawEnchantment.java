package com.ragegamingpe.wtfenchants.common.enchantment;

import com.ragegamingpe.wtfenchants.common.enchantment.base.ModBaseEnchantment;
import com.ragegamingpe.wtfenchants.common.lib.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class QuickDrawEnchantment extends ModBaseEnchantment
{
    public QuickDrawEnchantment()
    {
        super("quick_draw", Rarity.RARE, EnumEnchantmentType.BOW, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel)
    {
        return 10 + 6 * enchantmentLevel;
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

    @SubscribeEvent
    public void arrowFiredEvent(ArrowLooseEvent event)
    {
        int level = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.QUICK_DRAW, event.getBow());
        if (level > 0) event.setCharge(event.getCharge() * level);
    }
}
