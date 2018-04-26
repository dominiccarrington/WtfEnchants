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
    protected ModBaseEnchantment(String name, Rarity rarityIn, EnumEnchantmentType typeIn)
    {
        this(name, rarityIn, typeIn, calculateSlots(typeIn));
    }

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

    public void onEntityDeath(EntityLivingBase entity, DamageSource source, ItemStack enchantItem, Integer lvl)
    {

    }

    public void onEntityDeathDrops(EntityLivingBase entity, DamageSource source, ItemStack stack, List<EntityItem> drops, Integer lvl)
    {

    }

    @Override
    public String getName()
    {
        return "enchantment." + LibMisc.MOD_ID + ":" + name + ".name";
    }

    private static EntityEquipmentSlot[] calculateSlots(EnumEnchantmentType typeIn)
    {
        EntityEquipmentSlot[] slots;
        switch (typeIn) {
            case BOW:
            case WEAPON:
            case BREAKABLE:
            case DIGGER:
            case FISHING_ROD:
                slots = new EntityEquipmentSlot[]{
                        EntityEquipmentSlot.MAINHAND,
                        EntityEquipmentSlot.OFFHAND
                };

            case WEARABLE:
            case ARMOR:
                slots = new EntityEquipmentSlot[]{
                        EntityEquipmentSlot.HEAD,
                        EntityEquipmentSlot.CHEST,
                        EntityEquipmentSlot.LEGS,
                        EntityEquipmentSlot.FEET
                };

            case ARMOR_HEAD:
                slots = new EntityEquipmentSlot[]{
                        EntityEquipmentSlot.HEAD
                };

            case ARMOR_CHEST:
                slots = new EntityEquipmentSlot[]{
                        EntityEquipmentSlot.CHEST
                };

            case ARMOR_LEGS:
                slots = new EntityEquipmentSlot[]{
                        EntityEquipmentSlot.LEGS
                };

            case ARMOR_FEET:
                slots = new EntityEquipmentSlot[]{
                        EntityEquipmentSlot.FEET
                };

            case ALL:
            default:
                slots = EntityEquipmentSlot.values();
        }

        return slots;
    }
}
