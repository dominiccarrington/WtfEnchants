package com.ragegamingpe.wtfenchants.common.enchantment.base;

import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import java.util.List;

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

    public void onToolUse(EntityPlayer harvester, IBlockState state, ItemStack stack, int fortuneLevel, List<ItemStack> drops)
    {

    }

    public void onEntityDeath(EntityLivingBase player, DamageSource source, ItemStack enchantItem, List<EntityItem> drops)
    {

    }

    @Override
    public String getName()
    {
        return "enchantment." + LibMisc.MOD_ID + ":" + name + ".name";
    }
}
