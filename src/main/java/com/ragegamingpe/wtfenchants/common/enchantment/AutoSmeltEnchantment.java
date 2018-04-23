package com.ragegamingpe.wtfenchants.common.enchantment;

import com.ragegamingpe.wtfenchants.common.enchantment.base.ModBaseEnchantment;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import java.util.List;
import java.util.Random;

public class AutoSmeltEnchantment extends ModBaseEnchantment
{
    public AutoSmeltEnchantment()
    {
        super("auto_smelt", Rarity.VERY_RARE, EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
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
    public void onToolUse(EntityPlayer harvester, IBlockState brokenBlock, ItemStack stack, int fortuneLevel, List<ItemStack> drops)
    {
        Random rand = new Random();
        Block currentBlock = brokenBlock.getBlock();
        int blockMeta = currentBlock.getMetaFromState(brokenBlock);

        ItemStack result = FurnaceRecipes.instance().getSmeltingResult(new ItemStack(currentBlock, 1, blockMeta));
        if (result != ItemStack.EMPTY) {
            drops.clear();
            int count = 1;
            if (fortuneLevel > 0) count += rand.nextInt(fortuneLevel);
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
