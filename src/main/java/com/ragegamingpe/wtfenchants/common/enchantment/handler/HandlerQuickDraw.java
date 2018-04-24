package com.ragegamingpe.wtfenchants.common.enchantment.handler;

import com.ragegamingpe.wtfenchants.common.lib.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HandlerQuickDraw
{
    @SubscribeEvent
    public void arrowFiredEvent(ArrowLooseEvent event)
    {
        int level = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.QUICK_DRAW, event.getBow());
        if (level > 0) event.setCharge(event.getCharge() * level);
    }
}
