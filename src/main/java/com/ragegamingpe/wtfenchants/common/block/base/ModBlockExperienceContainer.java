package com.ragegamingpe.wtfenchants.common.block.base;

import com.ragegamingpe.wtfenchants.common.WtfEnchants;
import com.ragegamingpe.wtfenchants.common.block.te.base.TEBasicExperience;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ModBlockExperienceContainer extends ModBlockContainer
{
    public ModBlockExperienceContainer(Material materialIn, String regName)
    {
        super(materialIn, regName);
    }

    public ModBlockExperienceContainer(Material material, MapColor color, String regName)
    {
        super(material, color, regName);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote) {
            playerIn.openGui(WtfEnchants.instance, this.getGuiID(), worldIn, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TEBasicExperience && dumpXpOnBreak()) {
            int i = ((TEBasicExperience) tileentity).getTotalExperience();
            while (i > 0) {
                int j = EntityXPOrb.getXPSplit(i);
                i -= j;
                worldIn.spawnEntity(new EntityXPOrb(worldIn, pos.getX() + RANDOM.nextFloat(), pos.getY(), pos.getZ() + RANDOM.nextFloat(), j));
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    protected boolean dumpXpOnBreak()
    {
        return true;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
    {
        if (worldIn.isRemote) return 0;

        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TEBasicExperience) {
            return ((TEBasicExperience) te).getComparatorOutput();
        }
        return super.getComparatorInputOverride(blockState, worldIn, pos);
    }

    protected abstract int getGuiID();
}
