package com.ragegamingpe.wtfenchants.common.enchantment.base;

import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class ModBaseEnchantment extends Enchantment
{
    protected ModBaseEnchantment(String name, Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots)
    {
        super(rarityIn, typeIn, slots);
        this.setName(name);
        this.setRegistryName(LibMisc.MOD_ID, name);
    }

    public void onArmorTick(EntityPlayer player, ItemStack armorPiece, int lvl)
    {

    }

    @Override
    public String getName()
    {
        return "enchantment." + LibMisc.MOD_ID + ":" + name + ".name";
    }
}
