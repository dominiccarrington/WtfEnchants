package com.ragegamingpe.wtfenchants.common.block;

import com.google.gson.JsonObject;
import com.ragegamingpe.wtfenchants.client.model.ModelCreator;
import com.ragegamingpe.wtfenchants.common.block.base.IModBlockVariants;
import com.ragegamingpe.wtfenchants.common.block.base.ModBlockInventory;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntityBookshelf;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntitySorter;
import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import com.ragegamingpe.wtfenchants.common.network.GuiHandler;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

public class BlockBookshelf extends ModBlockInventory implements IModBlockVariants
{
    public static final PropertyInteger BOOKS = PropertyInteger.create("books", 0, 14);
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final AxisAlignedBB[] BOUNDING_BOXES_AABB = new AxisAlignedBB[]{
            createAABB(0, 0, 8, 16, 16, 16),
            createAABB(0, 0, 0, 16, 16, 8),
            createAABB(8, 0, 0, 16, 16, 16),
            createAABB(0, 0, 0, 8, 16, 16)
    };

    protected IBaseVariant variant;

    public BlockBookshelf(IBaseVariant variant)
    {
        super(Material.WOOD, variant.getName() + "_bookshelf");

        this.variant = variant;

        this.setHardness(2.0F);
        this.setResistance(5.0F);
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

        worldIn.setBlockState(pos, iblockstate.getBlock().getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)).withProperty(BOOKS, books), 3);

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
        return new BlockStateContainer(this, BOOKS, FACING);
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

    private static final PropertyEnum TYPE = PropertyEnum.create("type", Variant.class);

    @SuppressWarnings("unused")
    public static IProperty[] getAllProperties()
    {
        return new IProperty[]{TYPE, BOOKS, FACING};
    }

    @SuppressWarnings("unused")
    public static IProperty[] getIgnoredProperties()
    {
        return new IProperty[]{TYPE};
    }

    @SuppressWarnings("unused")
    public static Pair<String, JsonObject> generateBlockState(Variant type, Integer books, EnumFacing facing)
    {
        JsonObject object = new JsonObject();

        object.addProperty("model", LibMisc.MOD_ID + ":bookshelf/" + type.getName() + "/bookshelf_" + books);
        object.addProperty("y", (int) facing.getOpposite().getHorizontalAngle());

        return new ImmutablePair<>(type.getName() + "_bookshelf", object);
    }

    @SuppressWarnings("unused")
    public static Pair<String, String> generateBlockModel(Variant type, Integer books, EnumFacing facing)
    {
        String woodType = type.getFileName();
        String fileContents = "{\n" +
                "  \"credit\": \"Made with Blockbench\",\n" +
                "  \"textures\": {\n" +
                "    \"books\": \"wtfenchants:blocks/books\",\n" +
                "    \"planks\": \"minecraft:blocks/planks_" + woodType + "\",\n" +
                "    \"particle\": \"minecraft:blocks/planks_" + woodType + "\",\n" +
                "    \"missing\": \"minecraft:blocks/bedrock\"\n" +
                "  },\n" +
                "  \"elements\": [\n" +
                "    {\n" +
                "      \"name\": \"east\",\n" +
                "      \"from\": [15, 2, 8],\n" +
                "      \"to\": [16, 14, 15],\n" +
                "      \"faces\": {\n" +
                "        \"north\": {\"uv\": [0, 0, 1, 12], \"texture\": \"#planks\"},\n" +
                "        \"east\": {\"uv\": [0, 0, 7, 12],\"texture\": \"#planks\"},\n" +
                "        \"south\": {\"uv\": [0, 0, 1, 12],\"texture\": \"#planks\"},\n" +
                "        \"west\": {\"uv\": [0, 0, 7, 12],\"texture\": \"#planks\"},\n" +
                "        \"up\": {\"uv\": [0, 6, 1, 13],\"texture\": \"#planks\"},\n" +
                "        \"down\": {\"uv\": [1, 1, 2, 8],\"texture\": \"#planks\"}\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"west\",\n" +
                "      \"from\": [0, 2, 8],\n" +
                "      \"to\": [1, 14, 15],\n" +
                "      \"faces\": {\n" +
                "        \"north\": {\"uv\": [0, 0, 1, 12],\"texture\": \"#planks\"},\n" +
                "        \"east\": {\"uv\": [0, 0, 7, 12],\"texture\": \"#planks\"},\n" +
                "        \"south\": {\"uv\": [0, 0, 1, 12],\"texture\": \"#planks\"},\n" +
                "        \"west\": {\"uv\": [0, 0, 7, 12],\"texture\": \"#planks\"},\n" +
                "        \"up\": {\"uv\": [0, 6, 1, 13],\"texture\": \"#planks\"},\n" +
                "        \"down\": {\"uv\": [1, 1, 2, 8],\"texture\": \"#planks\"}\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"cube\",\n" +
                "      \"from\": [0, 0, 15],\n" +
                "      \"to\": [16, 16, 16],\n" +
                "      \"faces\": {\n" +
                "        \"north\": {\"uv\": [0, 0, 16, 16],\"texture\": \"#planks\"},\n" +
                "        \"east\": {\"uv\": [0, 0, 1, 16],\"texture\": \"#planks\"},\n" +
                "        \"south\": {\"uv\": [0, 0, 16, 16],\"texture\": \"#planks\"},\n" +
                "        \"west\": {\"uv\": [0, 0, 1, 16], \"texture\": \"#planks\"},\n" +
                "        \"up\": {\"uv\": [0, 0, 16, 1], \"texture\": \"#planks\"},\n" +
                "        \"down\": {\"uv\": [0, 0, 16, 1], \"texture\": \"#planks\"}\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"top\",\n" +
                "      \"from\": [0, 14, 8],\n" +
                "      \"to\": [16, 16, 15],\n" +
                "      \"faces\": {\n" +
                "        \"north\": {\"uv\": [0, 0, 16, 4], \"texture\": \"#planks\"},\n" +
                "        \"east\": {\"uv\": [2, 1, 10, 3], \"texture\": \"#planks\"},\n" +
                "        \"south\": {\"uv\": [0, 0, 16, 2], \"texture\": \"#planks\"},\n" +
                "        \"west\": {\"uv\": [1, 1, 9, 3], \"texture\": \"#planks\"},\n" +
                "        \"up\": {\"uv\": [0, 1, 16, 9], \"texture\": \"#planks\"},\n" +
                "        \"down\": {\"uv\": [0, 1, 16, 9], \"texture\": \"#planks\"}\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"bottom\",\n" +
                "      \"from\": [0, 0, 8],\n" +
                "      \"to\": [16, 2, 15],\n" +
                "      \"faces\": {\n" +
                "        \"north\": {\"uv\": [0, 0, 16, 2], \"texture\": \"#planks\"},\n" +
                "        \"east\": {\"uv\": [0, 0, 7, 2], \"texture\": \"#planks\"},\n" +
                "        \"south\": {\"uv\": [0, 0, 16, 2], \"texture\": \"#planks\"},\n" +
                "        \"west\": {\"uv\": [0, 0, 7, 2], \"texture\": \"#planks\"},\n" +
                "        \"up\": {\"uv\": [0, 0, 16, 7], \"texture\": \"#planks\"},\n" +
                "        \"down\": {\"uv\": [0, 0, 16, 7], \"texture\": \"#planks\"}\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"seperator\",\n" +
                "      \"from\": [1, 7, 8],\n" +
                "      \"to\": [15, 9, 15],\n" +
                "      \"faces\": {\n" +
                "        \"north\": {\"uv\": [0, 0, 14, 2], \"texture\": \"#planks\"},\n" +
                "        \"east\": {\"uv\": [0, 0, 7, 2], \"texture\": \"#planks\"},\n" +
                "        \"south\": {\"uv\": [0, 0, 14, 2], \"texture\": \"#planks\"},\n" +
                "        \"west\": {\"uv\": [0, 0, 7, 2], \"texture\": \"#planks\"},\n" +
                "        \"up\": {\"uv\": [1, 2, 15, 9], \"texture\": \"#planks\"},\n" +
                "        \"down\": {\"uv\": [1, 2, 15, 9], \"texture\": \"#planks\"}\n" +
                "      }\n" +
                "    }\n";

        switch (books) {
            case 14:
                fileContents += "    ,{\n" +
                        "      \"name\": \"book b7\",\n" +
                        "      \"from\": [1, 2, 9],\n" +
                        "      \"to\": [3, 7, 14],\n" +
                        "      \"faces\": {\n" +
                        "        \"north\": {\"uv\": [0, 0, 1, 2.5], \"texture\": \"#books\"},\n" +
                        "        \"east\": {\"uv\": [0, 0, 0.5, 2.5], \"texture\": \"#books\"},\n" +
                        "        \"south\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"},\n" +
                        "        \"west\": {\"uv\": [0.5, 0, 1, 2.5], \"texture\": \"#books\"},\n" +
                        "        \"up\": {\"uv\": [0, 2.5, 3.5, 3], \"texture\": \"#books\"},\n" +
                        "        \"down\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"}\n" +
                        "      }\n" +
                        "    }\n";

            case 13:
                fileContents += "    ,{\n" +
                        "      \"name\": \"book b6\",\n" +
                        "      \"from\": [3, 2, 8],\n" +
                        "      \"to\": [5, 7, 14],\n" +
                        "      \"faces\": {\n" +
                        "        \"north\": {\"uv\": [3.5, 3, 4.5, 5.5], \"texture\": \"#books\"},\n" +
                        "        \"east\": {\"uv\": [3.5, 3, 4, 5.5], \"texture\": \"#books\"},\n" +
                        "        \"south\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"},\n" +
                        "        \"west\": {\"uv\": [4, 3, 4.5, 5.5], \"texture\": \"#books\"},\n" +
                        "        \"up\": {\"uv\": [3.5, 5.5, 7, 6], \"texture\": \"#books\"},\n" +
                        "        \"down\": {\"uv\": [0, 0, 2, 6], \"texture\": \"#missing\"}\n" +
                        "      }\n" +
                        "    }\n";

            case 12:
                fileContents += "    ,{\n" +
                        "      \"name\": \"book b5\",\n" +
                        "      \"from\": [5, 2, 11],\n" +
                        "      \"to\": [7, 7, 14],\n" +
                        "      \"faces\": {\n" +
                        "        \"north\": {\"uv\": [7, 6, 8, 8.5], \"texture\": \"#books\"},\n" +
                        "        \"east\": {\"uv\": [7, 6, 7.5, 8.5], \"texture\": \"#books\"},\n" +
                        "        \"south\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"},\n" +
                        "        \"west\": {\"uv\": [7.5, 6, 8, 8.5], \"texture\": \"#books\"},\n" +
                        "        \"up\": {\"uv\": [7, 8.5, 10, 9], \"texture\": \"#books\"},\n" +
                        "        \"down\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"}\n" +
                        "      }\n" +
                        "    }\n";

            case 11:
                fileContents += "    ,{\n" +
                        "      \"name\": \"book b4\",\n" +
                        "      \"from\": [7, 2, 9],\n" +
                        "      \"to\": [9, 6, 14],\n" +
                        "      \"faces\": {\n" +
                        "        \"north\": {\"uv\": [0, 6, 1, 8.5], \"texture\": \"#books\"},\n" +
                        "        \"east\": {\"uv\": [0, 6, 0.5, 8.5], \"texture\": \"#books\"},\n" +
                        "        \"south\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"},\n" +
                        "        \"west\": {\"uv\": [0.5, 6, 1, 8.5], \"texture\": \"#books\"},\n" +
                        "        \"up\": {\"uv\": [0, 8.5, 3.5, 9], \"texture\": \"#books\"},\n" +
                        "        \"down\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"}\n" +
                        "      }\n" +
                        "    }\n";

            case 10:
                fileContents += "    ,{\n" +
                        "      \"name\": \"book b3\",\n" +
                        "      \"from\": [9, 2, 10],\n" +
                        "      \"to\": [11, 7, 14],\n" +
                        "      \"faces\": {\n" +
                        "        \"north\": {\"uv\": [3.5, 6, 4.5, 8.5], \"texture\": \"#books\"},\n" +
                        "        \"east\": {\"uv\": [3.5, 6, 4, 8.5], \"texture\": \"#books\"},\n" +
                        "        \"south\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"},\n" +
                        "        \"west\": {\"uv\": [4, 6, 4.5, 8.5], \"texture\": \"#books\"},\n" +
                        "        \"up\": {\"uv\": [3.5, 8.5, 7, 9], \"texture\": \"#books\"},\n" +
                        "        \"down\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"}\n" +
                        "      }\n" +
                        "    }\n";

            case 9:
                fileContents += "    ,{\n" +
                        "      \"name\": \"book b2\",\n" +
                        "      \"from\": [11, 2, 9],\n" +
                        "      \"to\": [13, 7, 15],\n" +
                        "      \"faces\": {\n" +
                        "        \"north\": {\"uv\": [0, 3, 1, 5.5], \"texture\": \"#books\"},\n" +
                        "        \"east\": {\"uv\": [0, 3, 0.5, 5.5], \"texture\": \"#books\"},\n" +
                        "        \"south\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"},\n" +
                        "        \"west\": {\"uv\": [0.5, 3, 1, 5.5], \"texture\": \"#books\"},\n" +
                        "        \"up\": {\"uv\": [0, 5.5, 3.5, 6], \"texture\": \"#books\"},\n" +
                        "        \"down\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"}\n" +
                        "      }\n" +
                        "    }\n";

            case 8:
                fileContents += "    ,{\n" +
                        "      \"name\": \"book b1\",\n" +
                        "      \"from\": [13, 2, 8],\n" +
                        "      \"to\": [15, 6, 14],\n" +
                        "      \"faces\": {\n" +
                        "        \"north\": {\"uv\": [7, 0, 8, 2], \"texture\": \"#books\"},\n" +
                        "        \"east\": {\"uv\": [7, 0, 7.5, 2.5], \"texture\": \"#books\"},\n" +
                        "        \"south\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"},\n" +
                        "        \"west\": {\"uv\": [7.5, 0, 8, 2.5], \"texture\": \"#books\"},\n" +
                        "        \"up\": {\"uv\": [7, 2.5, 10.5, 3], \"texture\": \"#books\"},\n" +
                        "        \"down\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"}\n" +
                        "      }\n" +
                        "    }\n";

            case 7:
                fileContents += "    ,{\n" +
                        "      \"name\": \"book t7\",\n" +
                        "      \"from\": [1, 9, 10],\n" +
                        "      \"to\": [3, 14, 14],\n" +
                        "      \"faces\": {\n" +
                        "        \"north\": {\"uv\": [0, 6, 1, 8.5], \"texture\": \"#books\"},\n" +
                        "        \"east\": {\"uv\": [0, 6, 0.5, 8.5], \"texture\": \"#books\"},\n" +
                        "        \"south\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"},\n" +
                        "        \"west\": {\"uv\": [0.5, 6, 1, 8.5], \"texture\": \"#books\"},\n" +
                        "        \"up\": {\"uv\": [0, 8.5, 3.5, 9], \"texture\": \"#books\"},\n" +
                        "        \"down\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"}\n" +
                        "      }\n" +
                        "    }\n";

            case 6:
                fileContents += "    ,{\n" +
                        "      \"name\": \"book t6\",\n" +
                        "      \"from\": [3, 9, 8],\n" +
                        "      \"to\": [5, 14, 14],\n" +
                        "      \"faces\": {\n" +
                        "        \"north\": {\"uv\": [3.5, 0, 4.5, 2.5], \"texture\": \"#books\"},\n" +
                        "        \"east\": {\"uv\": [3.5, 0, 4, 2.5], \"texture\": \"#books\"},\n" +
                        "        \"south\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"},\n" +
                        "        \"west\": {\"uv\": [4, 0, 4.5, 2.5], \"texture\": \"#books\"},\n" +
                        "        \"up\": {\"uv\": [3.5, 2.5, 7, 3], \"texture\": \"#books\"},\n" +
                        "        \"down\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"}\n" +
                        "      }\n" +
                        "    }\n";

            case 5:
                fileContents += "    ,{\n" +
                        "      \"name\": \"book t5\",\n" +
                        "      \"from\": [5, 9, 9],\n" +
                        "      \"to\": [7, 13, 14],\n" +
                        "      \"faces\": {\n" +
                        "        \"north\": {\"uv\": [0, 6, 1, 8.5], \"texture\": \"#books\"},\n" +
                        "        \"east\": {\"uv\": [0, 6, 0.5, 8.5], \"texture\": \"#books\"},\n" +
                        "        \"south\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"},\n" +
                        "        \"west\": {\"uv\": [0.5, 6, 1, 8.5], \"texture\": \"#books\"},\n" +
                        "        \"up\": {\"uv\": [0, 8.5, 3.5, 9], \"texture\": \"#books\"},\n" +
                        "        \"down\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"}\n" +
                        "      }\n" +
                        "    }\n";

            case 4:
                fileContents += "    ,{\n" +
                        "      \"name\": \"book t4\",\n" +
                        "      \"from\": [7, 9, 9],\n" +
                        "      \"to\": [9, 14, 14],\n" +
                        "      \"faces\": {\n" +
                        "        \"north\": {\"uv\": [7, 3, 8, 5.5], \"texture\": \"#books\"},\n" +
                        "        \"east\": {\"uv\": [7, 3, 8, 5.5], \"texture\": \"#books\"},\n" +
                        "        \"south\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"},\n" +
                        "        \"west\": {\"uv\": [7.5, 3, 8, 5.5], \"texture\": \"#books\"},\n" +
                        "        \"up\": {\"uv\": [7, 5.5, 10.5, 6], \"texture\": \"#books\"},\n" +
                        "        \"down\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"}\n" +
                        "      }\n" +
                        "    }\n";

            case 3:
                fileContents += "    ,{\n" +
                        "      \"name\": \"book t3\",\n" +
                        "      \"from\": [9, 9, 10],\n" +
                        "      \"to\": [11, 14, 15],\n" +
                        "      \"faces\": {\n" +
                        "        \"north\": {\"uv\": [7, 6, 8, 8.5], \"texture\": \"#books\"},\n" +
                        "        \"east\": {\"uv\": [7, 6, 7.5, 8.5], \"texture\": \"#books\"},\n" +
                        "        \"south\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"},\n" +
                        "        \"west\": {\"uv\": [7.5, 6, 8, 8], \"texture\": \"#books\"},\n" +
                        "        \"up\": {\"uv\": [7, 8.5, 10.5, 9], \"texture\": \"#books\"},\n" +
                        "        \"down\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"}\n" +
                        "      }\n" +
                        "    }\n";

            case 2:
                fileContents += "    ,{\n" +
                        "      \"name\": \"book t2\",\n" +
                        "      \"from\": [11, 9, 8],\n" +
                        "      \"to\": [13, 13, 14],\n" +
                        "      \"faces\": {\n" +
                        "        \"north\": {\"uv\": [3.5, 3, 4.5, 5], \"texture\": \"#books\"},\n" +
                        "        \"east\": {\"uv\": [3.5, 3, 4, 5.5], \"texture\": \"#books\"},\n" +
                        "        \"south\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"},\n" +
                        "        \"west\": {\"uv\": [4, 3, 4.5, 5.5], \"texture\": \"#books\"},\n" +
                        "        \"up\": {\"uv\": [3.5, 5.5, 7, 6], \"texture\": \"#books\"},\n" +
                        "        \"down\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"}\n" +
                        "      }\n" +
                        "    }\n";

            case 1:
                fileContents += "    ,{\n" +
                        "      \"name\": \"book t1\",\n" +
                        "      \"from\": [13, 9, 10],\n" +
                        "      \"to\": [15, 14, 14],\n" +
                        "      \"faces\": {\n" +
                        "        \"north\": {\"uv\": [0, 0, 1, 2.5], \"texture\": \"#books\"},\n" +
                        "        \"east\": {\"uv\": [0, 0, 0.5, 2.5], \"texture\": \"#books\"},\n" +
                        "        \"south\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"},\n" +
                        "        \"west\": {\"uv\": [0.5, 0, 1, 2.5], \"texture\": \"#books\"},\n" +
                        "        \"up\": {\"uv\": [0, 2.5, 3.5, 3], \"texture\": \"#books\"},\n" +
                        "        \"down\": {\"uv\": [0, 0, 2, 5], \"texture\": \"#missing\"}\n" +
                        "      }\n" +
                        "    }\n";
        }

        fileContents += "]}";
        return new ImmutablePair<>("bookshelf/" + type.getName() + "/bookshelf_" + books, fileContents);
    }

    @SuppressWarnings("unused")
    public static Pair<String, JsonObject> generateItemModel(Variant type, Integer books, EnumFacing facing)
    {
        return new ImmutablePair<>(type.getName() + "_bookshelf", ModelCreator.generateDefaultItemModel("bookshelf/" + type.getName() + "/bookshelf_14"));
    }

    public enum Variant implements IBaseVariant
    {
        OAK,
        SPRUCE,
        BIRCH,
        JUNGLE,
        ACACIA,
        DARK_OAK("big_oak");

        private String fileName;

        Variant()
        {
            this.fileName = this.getName();
        }

        Variant(String fileName)
        {
            this();
            this.fileName = fileName;
        }

        public String getFileName()
        {
            return this.fileName;
        }
    }
}
