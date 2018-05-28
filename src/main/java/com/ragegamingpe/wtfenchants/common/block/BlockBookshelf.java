package com.ragegamingpe.wtfenchants.common.block;

import com.ragegamingpe.wtfenchants.common.block.base.ModBlockInventory;
import com.ragegamingpe.wtfenchants.common.block.property.PropertyString;
import com.ragegamingpe.wtfenchants.common.block.property.PropertyUnlistedDirection;
import com.ragegamingpe.wtfenchants.common.block.property.PropertyUnlistedInteger;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntityBookshelf;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntitySorter;
import com.ragegamingpe.wtfenchants.common.helper.NBTHelper;
import com.ragegamingpe.wtfenchants.common.network.GuiHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * FIX: PICK BLOCK
 */
public class BlockBookshelf extends ModBlockInventory
{
    public static final PropertyUnlistedInteger BOOKS = new PropertyUnlistedInteger("books", 0, 14);
    public static final PropertyUnlistedDirection FACING = new PropertyUnlistedDirection("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyString TEXTURE = new PropertyString("texture");
    public static final AxisAlignedBB[] BOUNDING_BOXES_AABB = new AxisAlignedBB[]{
            createAABB(0, 0, 8, 16, 16, 16),
            createAABB(0, 0, 0, 16, 16, 8),
            createAABB(8, 0, 0, 16, 16, 16),
            createAABB(0, 0, 0, 8, 16, 16)
    };

    private static TileEntity teStore = null;

    public BlockBookshelf()
    {
        super(Material.WOOD, "bookshelf");

        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }

    public static void setState(int books, World worldIn, BlockPos pos)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (iblockstate instanceof IExtendedBlockState) {
            iblockstate = ((IExtendedBlockState) iblockstate).withProperty((IUnlistedProperty<Integer>) BOOKS, books);
        }

        worldIn.setBlockState(pos, iblockstate, 11);

        if (tileentity != null) {
            tileentity.validate();
            worldIn.setTileEntity(pos, tileentity);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        TileEntity te = source.getTileEntity(pos);
        if (te instanceof TileEntityBookshelf) {
            if (((TileEntityBookshelf) te).getFacing().ordinal() >= 2)
                return BOUNDING_BOXES_AABB[((TileEntityBookshelf) te).getFacing().ordinal() - 2];
        }

        return FULL_BLOCK_AABB;
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

    @Nonnull
    @Override
    public IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos)
    {
        IExtendedBlockState extendedState = (IExtendedBlockState) state;

        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityBookshelf) {
            TileEntityBookshelf table = (TileEntityBookshelf) te;
            return table.writeExtendedBlockState(extendedState);
        }

        return super.getExtendedState(state, world, pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list)
    {
        for (ItemStack stack : OreDictionary.getOres("slabWood")) {
            Block block = getBlockFromItem(stack.getItem());
            int blockMeta = stack.getItemDamage();

            if (blockMeta == OreDictionary.WILDCARD_VALUE) {
                NonNullList<ItemStack> subBlocks = NonNullList.create();
                block.getSubBlocks(null, subBlocks);

                for (ItemStack subBlock : subBlocks) {
                    list.add(createItemstack(this, 0, getBlockFromItem(subBlock.getItem()), subBlock.getItemDamage()));
                }
            } else {
                list.add(createItemstack(this, 0, block, blockMeta));
            }
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        ItemStack item = new ItemStack(this, 1);
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof TileEntityBookshelf) {
            TileEntityBookshelf bookshelf = (TileEntityBookshelf) te;

            NBTTagCompound tag = NBTHelper.getTagSafe(item);

            // texture
            NBTTagCompound data = bookshelf.getTextureBlock();

            if (!data.hasNoTags()) {
                tag.setTag(TileEntityBookshelf.PLANKS_TAG, data);
                item.setTagCompound(tag);
            }

            teStore = null;
        }

        return item;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        NBTTagCompound tag = NBTHelper.getTagSafe(stack);
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityBookshelf) {
            TileEntityBookshelf table = (TileEntityBookshelf) te;
            NBTTagCompound feetTag = tag.getCompoundTag(TileEntityBookshelf.PLANKS_TAG);
            if (feetTag == null) {
                feetTag = new NBTTagCompound();
            }

            table.updateTextureBlock(feetTag);
            table.setFacing(placer.getHorizontalFacing().getOpposite());
        }

        // Force inventory check on sorters
        for (EnumFacing direction : EnumFacing.values()) {
            BlockPos check = pos.offset(direction, 1);
            TileEntity teCheck = world.getTileEntity(check);

            if (teCheck instanceof TileEntitySorter) {
                ((TileEntitySorter) teCheck).deleteCachedInventories();
                break;
            } else if (teCheck instanceof TileEntityBookshelf) {
                ((TileEntityBookshelf) teCheck).updateSorters();
                break;
            }
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity te = worldIn.getTileEntity(pos);
        teStore = te;
        if (te instanceof TileEntityBookshelf) ((TileEntityBookshelf) te).updateSorters();

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        super.getDrops(drops, world, pos, state, fortune);

        TileEntity te;
        if (teStore != null) {
            te = teStore;
        } else {
            // Hope
            te = world.getTileEntity(pos);
        }

        if (te instanceof TileEntityBookshelf) {
            TileEntityBookshelf bookshelf = (TileEntityBookshelf) te;

            for (ItemStack item : drops) {
                if (item.getItem() == Item.getItemFromBlock(this)) {
                    NBTTagCompound tag = NBTHelper.getTagSafe(item);

                    // texture
                    NBTTagCompound data = bookshelf.getTextureBlock();

                    if (!data.hasNoTags()) {
                        tag.setTag(TileEntityBookshelf.PLANKS_TAG, data);
                        item.setTagCompound(tag);
                    }
                }
            }
            teStore = null;
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (!stack.hasTagCompound()) return;

        ItemStack legs = getPlanksTag(stack);
        if (!legs.isEmpty()) tooltip.add(legs.getDisplayName());
    }

    public static ItemStack getPlanksTag(ItemStack table)
    {
        NBTTagCompound tag = NBTHelper.getTagSafe(table).getCompoundTag(TileEntityBookshelf.PLANKS_TAG);
        return new ItemStack(tag);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this, new IProperty[]{}, new IUnlistedProperty[]{BOOKS, FACING, TEXTURE});
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
    protected int getGuiID()
    {
        return GuiHandler.BOOKSHELF;
    }

    @Override
    public void registerRender()
    {
    }

    public static ItemStack createItemstack(BlockBookshelf block, int itemDamage, Block blockFromItem, int meta)
    {
        ItemStack stack = new ItemStack(block, 1, itemDamage);

        if (block != null) {
            ItemStack blockStack = new ItemStack(blockFromItem, 1, meta);
            NBTTagCompound tag = new NBTTagCompound();
            NBTTagCompound subTag = new NBTTagCompound();
            blockStack.writeToNBT(subTag);
            tag.setTag(TileEntityBookshelf.PLANKS_TAG, subTag);
            stack.setTagCompound(tag);
        }

        return stack;
    }
}
