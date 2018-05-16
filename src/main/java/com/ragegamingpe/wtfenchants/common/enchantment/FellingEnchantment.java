package com.ragegamingpe.wtfenchants.common.enchantment;

import com.ragegamingpe.wtfenchants.common.enchantment.base.ModBaseEnchantment;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class FellingEnchantment extends ModBaseEnchantment
{
    public FellingEnchantment()
    {
        super("felling", Rarity.RARE, EnumEnchantmentType.DIGGER);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack)
    {
        return super.canApplyAtEnchantingTable(stack) && stack.getItem() instanceof ItemAxe;
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel)
    {
        return 25;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel)
    {
        return this.getMinEnchantability(enchantmentLevel) + 50;
    }

    @Override
    public float onToolUse(EntityPlayer harvester, IBlockState state, BlockPos pos, ItemStack stack, int fortuneLevel, List<ItemStack> drops)
    {
        onToolUse(harvester, state, pos, stack, fortuneLevel, drops, false);
        return 1.0F;
    }

    private void onToolUse(EntityPlayer harvester, IBlockState state, BlockPos pos, ItemStack stack, int fortuneLevel, List<ItemStack> drops, boolean fired)
    {
        World world = harvester.getEntityWorld();
        if (state.getBlock().isWood(world, pos)) {
            if (fired) {
                world.destroyBlock(pos, false);
                stack.attemptDamageItem(1, world.rand, (EntityPlayerMP) harvester);
                drops.add(new ItemStack(state.getBlock().getItemDropped(state, world.rand, fortuneLevel), state.getBlock().quantityDropped(state, fortuneLevel, world.rand), state.getBlock().damageDropped(state)));
            }

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        BlockPos checkLoc = new BlockPos(pos.getX() + i, pos.getY() + j, pos.getZ() + k);
                        IBlockState checkState = world.getBlockState(checkLoc);

                        if (checkState.getBlock().isWood(world, checkLoc)) {
                            onToolUse(harvester, checkState, checkLoc, stack, fortuneLevel, drops, true);
                        }
                    }
                }
            }
        }
    }
}
