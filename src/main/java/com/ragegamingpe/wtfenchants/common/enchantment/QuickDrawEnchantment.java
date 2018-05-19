package com.ragegamingpe.wtfenchants.common.enchantment;

import com.ragegamingpe.wtfenchants.common.enchantment.base.ModBaseEnchantment;
import com.ragegamingpe.wtfenchants.common.enchantment.handler.HandlerQuickDraw;
import com.ragegamingpe.wtfenchants.common.lib.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class QuickDrawEnchantment extends ModBaseEnchantment
{
    public QuickDrawEnchantment()
    {
        super("quick_draw", Rarity.RARE, EnumEnchantmentType.BOW);
    }

    @Override
    public void onPostInit()
    {
        MinecraftForge.EVENT_BUS.register(new HandlerQuickDraw());
        this.overridePullProperty();
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel)
    {
        return 13 + 6 * enchantmentLevel;
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

    public void overridePullProperty()
    {
        Items.BOW.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter()
        {
            @Override
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
            {
                int quickDraw = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.QUICK_DRAW, stack);
                return (entityIn == null || entityIn.getActiveItemStack().getItem() != Items.BOW) ? 0.0F : (float) (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / (20.0F / quickDraw);
            }
        });
    }
}
