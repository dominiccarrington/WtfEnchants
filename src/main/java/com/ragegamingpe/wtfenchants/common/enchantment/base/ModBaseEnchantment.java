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
import net.minecraft.util.math.BlockPos;

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

    public float onToolUse(EntityPlayer harvester, IBlockState state, BlockPos pos, ItemStack stack, int fortuneLevel, List<ItemStack> drops)
    {
        return 1.0F;
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
                break;

            case WEARABLE:
            case ARMOR:
                slots = new EntityEquipmentSlot[]{
                        EntityEquipmentSlot.HEAD,
                        EntityEquipmentSlot.CHEST,
                        EntityEquipmentSlot.LEGS,
                        EntityEquipmentSlot.FEET
                };
                break;

            case ARMOR_HEAD:
                slots = new EntityEquipmentSlot[]{
                        EntityEquipmentSlot.HEAD
                };
                break;

            case ARMOR_CHEST:
                slots = new EntityEquipmentSlot[]{
                        EntityEquipmentSlot.CHEST
                };
                break;

            case ARMOR_LEGS:
                slots = new EntityEquipmentSlot[]{
                        EntityEquipmentSlot.LEGS
                };
                break;

            case ARMOR_FEET:
                slots = new EntityEquipmentSlot[]{
                        EntityEquipmentSlot.FEET
                };
                break;

            case ALL:
            default:
                slots = EntityEquipmentSlot.values();
        }

        return slots;
    }
}
