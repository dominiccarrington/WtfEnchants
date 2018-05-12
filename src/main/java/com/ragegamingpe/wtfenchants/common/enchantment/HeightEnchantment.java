package com.ragegamingpe.wtfenchants.common.enchantment;

import com.ragegamingpe.wtfenchants.common.enchantment.base.ModBaseEnchantment;
import com.ragegamingpe.wtfenchants.common.enchantment.handler.HandlerHeightWidth;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class HeightEnchantment extends ModBaseEnchantment
{
    public HeightEnchantment()
    {
        super("height", Rarity.RARE, EnumEnchantmentType.DIGGER);
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel)
    {
        return 25 + 5 * enchantmentLevel;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel)
    {
        return super.getMaxEnchantability(enchantmentLevel) + 30;
    }

    @Override
    public int getMaxLevel()
    {
        return 3;
    }

    @Override
    public float onToolUse(EntityPlayer harvester, IBlockState state, BlockPos pos, ItemStack stack, int fortuneLevel, List<ItemStack> drops)
    {
        drops.addAll(HandlerHeightWidth.getDrops());
        return super.onToolUse(harvester, state, pos, stack, fortuneLevel, drops);
    }
}
