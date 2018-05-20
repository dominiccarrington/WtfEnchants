package com.ragegamingpe.wtfenchants.common.item.base;

import com.ragegamingpe.wtfenchants.client.model.ModelRegister;
import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import com.ragegamingpe.wtfenchants.common.lib.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ModItem extends Item implements IModItem
{
    public ModItem(String regName)
    {
        super();

        this.setUnlocalizedName(regName);
        this.setRegistryName(LibMisc.MOD_ID, regName);
        ModItems.ALL_ITEMS.add(this);
    }

    @Override
    public String getUnlocalizedName()
    {
        return String.format("item.%s:%s", LibMisc.MOD_ID, getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    @Override
    public ModItem setCreativeTab(CreativeTabs tab)
    {
        super.setCreativeTab(tab);
        return this;
    }

    @Override
    public void registerRender()
    {
        ModelRegister.registerItemModel(this, 0, getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }
}
