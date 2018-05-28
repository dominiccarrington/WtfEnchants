package com.ragegamingpe.wtfenchants.common.enchantment;

import com.ragegamingpe.wtfenchants.common.enchantment.base.ModBaseEnchantment;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.BlockEvent;

import java.util.List;
import java.util.Random;

public class AutoSmeltEnchantment extends ModBaseEnchantment
{
    public AutoSmeltEnchantment()
    {
        super("auto_smelt", Rarity.VERY_RARE, EnumEnchantmentType.DIGGER);
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel)
    {
        return 15;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel)
    {
        return super.getMinEnchantability(enchantmentLevel) + 50;
    }

    @Override
    public void onBlockBrokenDrops(BlockEvent.HarvestDropsEvent event, EntityPlayer harvester, IBlockState brokenBlock, BlockPos pos, ItemStack stack, int fortuneLevel, List<ItemStack> drops)
    {
        Random rand = new Random();
        Block currentBlock = brokenBlock.getBlock();
        int blockMeta = currentBlock.getMetaFromState(brokenBlock);

        ItemStack result = FurnaceRecipes.instance().getSmeltingResult(new ItemStack(currentBlock, 1, blockMeta));
        if (result != ItemStack.EMPTY) {
            drops.clear();
            int count = 1;
            if (fortuneLevel > 0) count += (int) Math.floor(rand.nextFloat() * fortuneLevel);
            result.setCount(count);

            drops.add(result);
        }
    }

    @Override
    protected boolean canApplyTogether(Enchantment ench)
    {
        return super.canApplyTogether(ench) && ench != Enchantments.SILK_TOUCH;
    }
}
