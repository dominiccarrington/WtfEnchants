package com.ragegamingpe.wtfenchants.common.block.base;

import com.ragegamingpe.wtfenchants.client.model.ModelRegister;
import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import com.ragegamingpe.wtfenchants.common.lib.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.math.AxisAlignedBB;

public class ModBlock extends Block implements IModBlock
{
    public ModBlock(Material material, MapColor color, String regName)
    {
        super(material, color);

        this.setUnlocalizedName(regName);
        this.setRegistryName(LibMisc.MOD_ID, regName);
        ModBlocks.ALL_BLOCKS.add(this);
    }

    public ModBlock(Material material, String regName)
    {
        this(material, material.getMaterialMapColor(), regName);
    }

    @Override
    public String getUnlocalizedName()
    {
        return String.format("tile.%s:%s", LibMisc.MOD_ID, getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    @Override
    public ModBlock setCreativeTab(CreativeTabs tab)
    {
        super.setCreativeTab(tab);
        return this;
    }

    @Override
    public void registerRender()
    {
        ModelRegister.registerItemModel(this, 0, getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    protected static AxisAlignedBB createAABB(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        return new AxisAlignedBB(x1 / 16F, y1 / 16F, z1 / 16F, x2 / 16F, y2 / 16F, z2 / 16F);
    }
}
