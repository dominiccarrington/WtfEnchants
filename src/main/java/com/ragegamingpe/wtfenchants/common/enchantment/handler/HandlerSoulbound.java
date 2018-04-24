package com.ragegamingpe.wtfenchants.common.enchantment.handler;

import com.ragegamingpe.wtfenchants.common.WtfEnchants;
import com.ragegamingpe.wtfenchants.common.lib.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ListIterator;

public class HandlerSoulbound
{
    @SubscribeEvent
    public void onPlayerDrops(PlayerDropsEvent event)
    {
        if (event.getEntityPlayer() == null || event.getEntityPlayer() instanceof FakePlayer || event.isCanceled())
            return;

        if (event.getEntityPlayer().world.getGameRules().getBoolean("keepInventory")) return;

        ListIterator<EntityItem> iter = event.getDrops().listIterator();
        while (iter.hasNext()) {
            EntityItem ei = iter.next();
            ItemStack item = ei.getItem();
            if (isSoulBound(item)) if (addToPlayerInventory(event.getEntityPlayer(), item)) iter.remove();
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event)
    {
        if (!event.isWasDeath() || event.isCanceled()) return;

        EntityPlayer original = event.getOriginal();
        EntityPlayer newPlayer = event.getEntityPlayer();

        if (original == null || newPlayer == null || newPlayer instanceof FakePlayer)
            return;

        if (newPlayer.world.getGameRules().getBoolean("keepInventory")) return;

        if (original == newPlayer || original.inventory == newPlayer.inventory
                || (original.inventory.armorInventory == newPlayer.inventory.armorInventory
                && original.inventory.mainInventory == newPlayer.inventory.mainInventory)) {
            WtfEnchants.logger.warn("Soulbounding the same player... That is weird. Aborting");
            return;
        }

        for (int i = 0; i < original.inventory.armorInventory.size(); i++) {
            ItemStack item = original.inventory.armorInventory.get(i);
            if (isSoulBound(item))
                if (addToPlayerInventory(newPlayer, item)) original.inventory.armorInventory.set(i, ItemStack.EMPTY);
        }

        for (int i = 0; i < original.inventory.mainInventory.size(); i++) {
            ItemStack item = original.inventory.mainInventory.get(i);
            if (isSoulBound(item))
                if (addToPlayerInventory(newPlayer, item)) original.inventory.mainInventory.set(i, ItemStack.EMPTY);
        }
    }

    private static boolean isSoulBound(ItemStack stack)
    {
        return EnchantmentHelper.getEnchantmentLevel(ModEnchantments.SOULBOUND, stack) > 0;
    }

    private static boolean addToPlayerInventory(EntityPlayer entityPlayer, ItemStack item)
    {
        if (item == null || entityPlayer == null) {
            return false;
        }

        InventoryPlayer inv = entityPlayer.inventory;

        if (item.getItem() instanceof ItemArmor) {
            ItemArmor arm = (ItemArmor) item.getItem();
            int index = arm.armorType.getIndex();
            if (entityPlayer.inventory.armorInventory.get(index).isEmpty()) {
                inv.armorInventory.set(index, item);
                return true;
            }
        }

        for (int i = 0; i < inv.mainInventory.size(); i++) {
            if (inv.mainInventory.get(i).isEmpty()) {
                inv.mainInventory.set(i, item.copy());
                return true;
            }
        }

        return false;
    }
}
