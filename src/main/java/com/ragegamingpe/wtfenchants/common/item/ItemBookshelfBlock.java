package com.ragegamingpe.wtfenchants.common.item;

import com.ragegamingpe.wtfenchants.common.block.te.TileEntityCommonBookshelf;
import com.ragegamingpe.wtfenchants.common.lib.ModBlocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemColored;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.ragegamingpe.wtfenchants.client.model.CommonBookshelfModel.getTagSafe;

public class ItemBookshelfBlock extends ItemColored
{
    public ItemBookshelfBlock()
    {
        super(ModBlocks.COMMON_BOOKSHELF, true);
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (!stack.hasTagCompound()) return;

        ItemStack legs = getPlanksTag(stack);
        if (!legs.isEmpty()) tooltip.add(legs.getDisplayName());
    }

    public static ItemStack getPlanksTag(ItemStack table)
    {
        NBTTagCompound tag = getTagSafe(table).getCompoundTag(TileEntityCommonBookshelf.PLANKS_TAG);
        return new ItemStack(tag);
    }
}