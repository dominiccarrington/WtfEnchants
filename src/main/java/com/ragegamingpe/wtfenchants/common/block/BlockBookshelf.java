package com.ragegamingpe.wtfenchants.common.block;

import com.google.gson.JsonObject;
import com.ragegamingpe.wtfenchants.common.block.base.ModBlockInventory;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntityBookshelf;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntitySorter;
import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import com.ragegamingpe.wtfenchants.common.lib.ModBlocks;
import com.ragegamingpe.wtfenchants.common.network.GuiHandler;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockBookshelf extends ModBlockInventory
{
    public static final PropertyInteger BOOKS = PropertyInteger.create("books", 0, 14);
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final AxisAlignedBB[] BOUNDING_BOXES_AABB = new AxisAlignedBB[]{
            new AxisAlignedBB(0, 0, 8 / 16D, 1, 1, 1),
            new AxisAlignedBB(0, 0, 0, 1, 1, 8 / 16D),
            new AxisAlignedBB(8 / 16D, 0, 0, 1, 1, 1),
            new AxisAlignedBB(0, 0, 0, 8 / 16D, 1, 1)
    };

    public BlockBookshelf()
    {
        super(Material.WOOD, "bookshelf");
        this.setHardness(Blocks.PLANKS.getBlockHardness(null, null, null));
        this.setDefaultState(this.blockState.getBaseState().withProperty(BOOKS, 0).withProperty(FACING, EnumFacing.NORTH));
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        int index = state.getValue(FACING).ordinal() - 2;
        return BOUNDING_BOXES_AABB[index];
    }

    @Override
    public boolean isFullBlock(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        // This could be bad...
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileEntityBookshelf) {
            return state.withProperty(BOOKS, ((TileEntityBookshelf) te).getNumberOfBooks());
        }
        return super.getActualState(state, worldIn, pos);
    }

    public static void setState(int books, World worldIn, BlockPos pos)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        TileEntity tileentity = worldIn.getTileEntity(pos);

        worldIn.setBlockState(pos, ModBlocks.BOOKSHELF.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)).withProperty(BOOKS, books), 3);

        if (tileentity != null) {
            tileentity.validate();
            worldIn.setTileEntity(pos, tileentity);
        }
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing facing = EnumFacing.getFront(meta);
        if (facing.getAxis() == EnumFacing.Axis.Y)
            facing = EnumFacing.NORTH;

        return this.getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        // Force inventory check on sorters
        for (EnumFacing direction : EnumFacing.values()) {
            BlockPos check = pos.offset(direction, 1);
            TileEntity te = worldIn.getTileEntity(check);

            if (te instanceof TileEntitySorter) {
                ((TileEntitySorter) te).deleteCachedInventories();
                break;
            } else if (te instanceof TileEntityBookshelf) {
                ((TileEntityBookshelf) te).updateSorters();
                break;
            }
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileEntityBookshelf) ((TileEntityBookshelf) te).updateSorters();

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityBookshelf();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, getAllProperties());
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return face.getOpposite() == state.getValue(FACING) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    protected int getGuiID()
    {
        return GuiHandler.BOOKSHELF;
    }

    public static IProperty[] getAllProperties()
    {
        return new IProperty[]{BOOKS, FACING};
    }

    public static JsonObject generateModel(Integer books, EnumFacing facing)
    {
        JsonObject object = new JsonObject();

        object.addProperty("model", LibMisc.MOD_ID + ":bookshelf/bookshelf_" + books);
        object.addProperty("y", (int) facing.getOpposite().getHorizontalAngle());

        return object;
    }
}
