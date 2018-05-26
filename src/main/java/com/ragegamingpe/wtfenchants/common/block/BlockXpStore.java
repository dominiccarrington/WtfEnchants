package com.ragegamingpe.wtfenchants.common.block;

import com.ragegamingpe.wtfenchants.common.block.base.ModBlockExperienceContainer;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntityXpStore;
import com.ragegamingpe.wtfenchants.common.network.GuiHandler;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockXpStore extends ModBlockExperienceContainer
{
    public BlockXpStore()
    {
        super(Material.ROCK, MapColor.RED, "xp_store");
        this.setHardness(5.0F);
        this.setResistance(2000.0F);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityXpStore();
    }

    @Override
    protected int getGuiID()
    {
        return GuiHandler.XP_STORE;
    }
}
