package com.ragegamingpe.wtfenchants.common.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class NBTHelper
{
    public static NBTTagCompound getTagSafe(ItemStack stack)
    {
        // yes, the null checks aren't needed anymore, but they don't hurt either.
        // After all the whole purpose of this function is safety/processing possibly invalid input ;)
        if (stack == null || stack.getItem() == null || stack.isEmpty() || !stack.hasTagCompound()) {
            return new NBTTagCompound();
        }

        return stack.getTagCompound();
    }
}
