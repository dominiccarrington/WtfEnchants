package com.ragegamingpe.wtfenchants.common.enchantment;

import com.ragegamingpe.wtfenchants.common.enchantment.base.ModBaseEnchantment;
import com.ragegamingpe.wtfenchants.common.enchantment.handler.HandlerHeightWidth;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

import java.util.List;

public class WidthEnchantment extends ModBaseEnchantment
{
    public WidthEnchantment()
    {
        super("width", Rarity.RARE, EnumEnchantmentType.DIGGER);
    }

    @Override
    public void onPostInit()
    {
        if (!HandlerHeightWidth.registered) {
            HandlerHeightWidth.registered = true;
            MinecraftForge.EVENT_BUS.register(new HandlerHeightWidth());
        }
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
    public void onBlockBrokenDrops(BlockEvent.HarvestDropsEvent event, EntityPlayer harvester, IBlockState state, BlockPos pos, ItemStack stack, int fortuneLevel, List<ItemStack> drops)
    {
        drops.addAll(HandlerHeightWidth.getDrops());
    }
}
