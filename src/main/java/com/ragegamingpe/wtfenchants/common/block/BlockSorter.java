package com.ragegamingpe.wtfenchants.common.block;

import com.google.gson.JsonObject;
import com.ragegamingpe.wtfenchants.common.WtfEnchants;
import com.ragegamingpe.wtfenchants.common.block.base.ModBlockContainer;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntityBookshelf;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntitySorter;
import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import com.ragegamingpe.wtfenchants.common.network.GuiHandler;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

public class BlockSorter extends ModBlockContainer
{
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    private static final AxisAlignedBB[][] COLLISION_LIST = new AxisAlignedBB[][]{
            new AxisAlignedBB[]{ //NORTH
                    createAABB(0, 0, 0, 16, 16, 5),
                    createAABB(1, 0, 5, 15, 13, 9),
                    createAABB(2, 1, 9, 14, 11, 12)
            },
            new AxisAlignedBB[]{ // SOUTH
                    createAABB(0, 0, 16, 16, 16, 11),
                    createAABB(1, 0, 11, 15, 13, 7),
                    createAABB(2, 1, 7, 14, 11, 4)
            },
            new AxisAlignedBB[]{ // EAST
                    createAABB(0, 0, 0, 5, 16, 16),
                    createAABB(5, 0, 1, 9, 13, 15),
                    createAABB(9, 1, 2, 12, 11, 14)
            },
            new AxisAlignedBB[]{ // WEST
                    createAABB(16, 0, 0, 11, 16, 16),
                    createAABB(11, 0, 1, 7, 13, 15),
                    createAABB(7, 1, 2, 4, 11, 14)
            },
    };

    public BlockSorter()
    {
        super(Material.ROCK, "sorter");

        this.setHardness(0.8F);
        this.setDefaultState(this.getDefaultState().withProperty(FACING, EnumFacing.NORTH));
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        int facing = state.getValue(FACING).ordinal() - 2;
        for (AxisAlignedBB axisalignedbb : COLLISION_LIST[facing]) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, axisalignedbb);
        }
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
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote) {
            playerIn.openGui(WtfEnchants.instance, GuiHandler.SORTER, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
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
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, getAllProperties());
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntitySorter();
    }

    public static IInventory[] getAllBookshelvesConnected(World world, BlockPos pos)
    {
        if (world == null || pos == null) return null;

        if (!(world.getBlockState(pos).getBlock() instanceof BlockSorter)) return null;

        List<IInventory> bookshelves = new ArrayList<>(); // What I am finding

        List<BlockPos> checkedLocations = new ArrayList<>(); // List of checked BlockPoses
        Queue<BlockPos> currentChecks = new PriorityQueue<>(); // Queue of checks

        currentChecks.add(pos);

        while (currentChecks.size() > 0) {
            BlockPos current = currentChecks.peek();
            if (current == null) break; // WTF? Really, should not happen

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        BlockPos check = current.add(i, j, k);

                        if (checkedLocations.contains(check)) continue;

                        if (world.getBlockState(check).getBlock() instanceof BlockBookshelf && world.getTileEntity(check) instanceof TileEntityBookshelf) {
                            bookshelves.add((IInventory) world.getTileEntity(check));
                            ((TileEntityBookshelf) Objects.requireNonNull(world.getTileEntity(check))).addSorter(pos);

                            checkedLocations.add(check);
                            currentChecks.add(check);
                        }
                    }
                }
            }

            currentChecks.remove(current);
        }

        if (bookshelves.size() == 0) {
            return new IInventory[]{new InventoryBasic("", false, 0)};
        }

        return bookshelves.toArray(new IInventory[0]);
    }

    public static IProperty[] getAllProperties()
    {
        return new IProperty[]{FACING};
    }

    public static JsonObject generateBlockState(EnumFacing facing)
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("model", LibMisc.MOD_ID + ":sorter");
        obj.addProperty("y", facing.getOpposite().getHorizontalAngle());

        return obj;
    }

    public static String generateBlockModel(EnumFacing facing)
    {
        return "{\n" +
                "  \"textures\": {\n" +
                "    \"0\": \"minecraft:blocks/quartz_block_bottom\",\n" +
                "    \"1\": \"minecraft:blocks/concrete_black\",\n" +
                "    \"particle\": \"minecraft:blocks/quartz_block_bottom\"\n" +
                "  },\n" +
                "  \"elements\": [\n" +
                "    {\n" +
                "      \"name\": \"monitor_back_1\",\n" +
                "      \"from\": [0, 0, 1],\n" +
                "      \"to\": [16, 16, 5],\n" +
                "      \"faces\": {\n" +
                "        \"north\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#0\"},\n" +
                "        \"east\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#0\"},\n" +
                "        \"south\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#0\"},\n" +
                "        \"west\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#0\"},\n" +
                "        \"up\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#0\", \"rotation\": 270},\n" +
                "        \"down\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#0\", \"rotation\": 90}\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"monitor_back_2\",\n" +
                "      \"from\": [1, 0, 5],\n" +
                "      \"to\": [15, 13, 9],\n" +
                "      \"faces\": {\n" +
                "        \"north\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#0\"},\n" +
                "        \"east\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#0\"},\n" +
                "        \"south\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#0\"},\n" +
                "        \"west\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#0\"},\n" +
                "        \"up\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#0\", \"rotation\": 270},\n" +
                "        \"down\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#0\", \"rotation\": 90}\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"monitor_back_3\",\n" +
                "      \"from\": [2, 1, 9],\n" +
                "      \"to\": [14, 11, 12],\n" +
                "      \"faces\": {\n" +
                "        \"north\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#0\"},\n" +
                "        \"east\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#0\"},\n" +
                "        \"south\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#0\"},\n" +
                "        \"west\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#0\"},\n" +
                "        \"up\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#0\", \"rotation\": 270},\n" +
                "        \"down\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#0\", \"rotation\": 90}\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"monitor_outline_bottom\",\n" +
                "      \"from\": [0, 0, 0],\n" +
                "      \"to\": [16, 1, 1],\n" +
                "      \"faces\": {\n" +
                "        \"north\": {\"uv\": [0, 0, 16, 1], \"texture\": \"#0\"},\n" +
                "        \"east\": {\"uv\": [0, 0, 1, 1], \"texture\": \"#0\"},\n" +
                "        \"south\": {\"uv\": [0, 0, 16, 1], \"texture\": \"#0\"},\n" +
                "        \"west\": {\"uv\": [0, 0, 1, 1], \"texture\": \"#0\"},\n" +
                "        \"up\": {\"uv\": [0, 0, 16, 1], \"texture\": \"#0\"},\n" +
                "        \"down\": {\"uv\": [0, 0, 16, 1], \"texture\": \"#0\"}\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"monitor_outline_top\",\n" +
                "      \"from\": [0, 15, 0],\n" +
                "      \"to\": [16, 16, 1],\n" +
                "      \"faces\": {\n" +
                "        \"north\": {\"uv\": [0, 0, 16, 1], \"texture\": \"#0\"},\n" +
                "        \"east\": {\"uv\": [0, 0, 1, 1], \"texture\": \"#0\"},\n" +
                "        \"south\": {\"uv\": [0, 0, 16, 1], \"texture\": \"#0\"},\n" +
                "        \"west\": {\"uv\": [0, 0, 1, 1], \"texture\": \"#0\"},\n" +
                "        \"up\": {\"uv\": [0, 0, 16, 1], \"texture\": \"#0\"},\n" +
                "        \"down\": {\"uv\": [0, 0, 16, 1], \"texture\": \"#0\"}\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"monitor_outline_left\",\n" +
                "      \"from\": [15, 1, 0],\n" +
                "      \"to\": [16, 15, 1],\n" +
                "      \"faces\": {\n" +
                "        \"north\": {\"uv\": [0, 0, 1, 14], \"texture\": \"#0\"},\n" +
                "        \"east\": {\"uv\": [0, 0, 1, 14], \"texture\": \"#0\"},\n" +
                "        \"south\": {\"uv\": [0, 0, 1, 14], \"texture\": \"#0\"},\n" +
                "        \"west\": {\"uv\": [0, 0, 1, 14], \"texture\": \"#0\"},\n" +
                "        \"up\": {\"uv\": [0, 0, 1, 1], \"texture\": \"#0\"},\n" +
                "        \"down\": {\"uv\": [0, 0, 1, 1], \"texture\": \"#0\"}\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"monitor_outline_right\",\n" +
                "      \"from\": [0, 1, 0],\n" +
                "      \"to\": [1, 15, 1],\n" +
                "      \"faces\": {\n" +
                "        \"north\": {\"uv\": [0, 0, 1, 14], \"texture\": \"#0\"},\n" +
                "        \"east\": {\"uv\": [0, 0, 1, 14], \"texture\": \"#0\"},\n" +
                "        \"south\": {\"uv\": [0, 0, 1, 14], \"texture\": \"#0\"},\n" +
                "        \"west\": {\"uv\": [0, 0, 1, 14], \"texture\": \"#0\"},\n" +
                "        \"up\": {\"uv\": [0, 0, 1, 1], \"texture\": \"#0\"},\n" +
                "        \"down\": {\"uv\": [0, 0, 1, 1], \"texture\": \"#0\"}\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"screen\",\n" +
                "      \"from\": [1, 1, 0],\n" +
                "      \"to\": [15, 15, 1],\n" +
                "      \"faces\": {\n" +
                "        \"north\": {\"uv\": [0, 0, 14, 14], \"texture\": \"#1\"},\n" +
                "        \"east\": {\"uv\": [0, 0, 1, 14], \"texture\": \"#1\"},\n" +
                "        \"south\": {\"uv\": [0, 0, 14, 14], \"texture\": \"#1\"},\n" +
                "        \"west\": {\"uv\": [0, 0, 1, 14], \"texture\": \"#1\"},\n" +
                "        \"up\": {\"uv\": [0, 0, 14, 1], \"texture\": \"#1\"},\n" +
                "        \"down\": {\"uv\": [0, 0, 14, 1], \"texture\": \"#1\"}\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }
}
