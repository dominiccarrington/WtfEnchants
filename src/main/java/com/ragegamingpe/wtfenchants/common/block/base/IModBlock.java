package com.ragegamingpe.wtfenchants.common.block.base;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Functions used to register inventory rendering only. Has no effect on block rendering.
 */
public interface IModBlock
{
    @SideOnly(Side.CLIENT)
    void registerRender();

    @SideOnly(Side.CLIENT)
    default void registerModels()
    {

    }

    default String getUnwrappedUnlocalizedName(String unlocalizedName)
    {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }
}
