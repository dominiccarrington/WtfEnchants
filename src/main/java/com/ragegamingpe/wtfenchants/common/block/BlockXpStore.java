package com.ragegamingpe.wtfenchants.common.block;

import com.ragegamingpe.wtfenchants.common.WtfEnchants;
import com.ragegamingpe.wtfenchants.common.block.base.ModBlockContainer;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntityXpStore;
import com.ragegamingpe.wtfenchants.common.network.GuiHandler;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockXpStore extends ModBlockContainer
{
    public BlockXpStore()
    {
        super(Material.ROCK, MapColor.RED, "xp_store");
        this.setHardness(5.0F);
        this.setResistance(2000.0F);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote) {
            playerIn.openGui(WtfEnchants.instance, GuiHandler.XP_STORE, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityXpStore();
    }
}
