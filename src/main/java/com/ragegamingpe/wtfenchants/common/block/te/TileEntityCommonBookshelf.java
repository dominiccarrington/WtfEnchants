package com.ragegamingpe.wtfenchants.common.block.te;

import com.ragegamingpe.wtfenchants.common.block.BlockCommonBookshelf;
import com.ragegamingpe.wtfenchants.common.block.te.base.ModBaseTEInventory;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.ArrayList;
import java.util.List;

import static com.ragegamingpe.wtfenchants.client.model.CommonBookshelfModel.getTextureFromBlock;

public class TileEntityCommonBookshelf extends ModBaseTEInventory
{
    public static final String PLANKS_TAG = "textureBlock";
    public static final String FACE_TAG = "facing";
    public static final String BOOKS_TAG = "books";

    protected final List<BlockPos> accessedBySorters = new ArrayList<>();

    public TileEntityCommonBookshelf()
    {
        super("bookshelf", 14);
    }

    public IExtendedBlockState writeExtendedBlockState(IExtendedBlockState state)
    {
        String texture = getTileData().getString("texture");

        // texture not loaded
        if (texture.isEmpty()) {
            // load it from saved block
            ItemStack stack = new ItemStack(getTileData().getCompoundTag(PLANKS_TAG));
            if (!stack.isEmpty()) {
                Block block = Block.getBlockFromItem(stack.getItem());
                texture = getTextureFromBlock(block, stack.getItemDamage()).getIconName();
                getTileData().setString("texture", texture);
            }
        }

        if (!texture.isEmpty()) {
            state = state.withProperty(BlockCommonBookshelf.TEXTURE, texture);
        }

        EnumFacing facing = getFacing();
        state = state.withProperty((IUnlistedProperty<EnumFacing>) BlockCommonBookshelf.FACING, facing);

        int books = getBooks();
        state = state.withProperty((IUnlistedProperty<Integer>) BlockCommonBookshelf.BOOKS, books);

        return state;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return stack.getItem() == Items.ENCHANTED_BOOK && EnchantmentHelper.getEnchantments(stack).size() > 0;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        super.setInventorySlotContents(index, stack);
        if (!world.isRemote) {
            updateBooks();
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, getBooks());
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int type)
    {
        if (id == 1) {
            getTileData().setInteger(BOOKS_TAG, type);
            BlockCommonBookshelf.setState(type, this.world, this.pos);
            return true;
        }

        return super.receiveClientEvent(id, type);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        // note that this sends all of the tile data. you should change this if you use additional tile data
        NBTTagCompound tag = getTileData().copy();
        writeToNBT(tag);
        return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        NBTTagCompound tag = pkt.getNbtCompound();
        NBTBase feet = tag.getTag(PLANKS_TAG);
        if (feet != null) {
            getTileData().setTag(PLANKS_TAG, feet);
        }
        NBTBase facing = tag.getTag(FACE_TAG);
        if (facing != null) {
            getTileData().setTag(FACE_TAG, facing);
        }
        NBTBase books = tag.getTag(BOOKS_TAG);
        if (books != null) {
            getTileData().setTag(BOOKS_TAG, books);
        }
        readFromNBT(tag);
    }

    public void addSorter(BlockPos pos)
    {
        if (!accessedBySorters.contains(pos))
            accessedBySorters.add(pos);
    }

    public void updateSorters()
    {
        for (BlockPos sorterPos : accessedBySorters) {
            TileEntity te = this.world.getTileEntity(sorterPos);
            if (te instanceof TileEntitySorter)
                ((TileEntitySorter) te).deleteCachedInventories();
        }
    }

    public void setFacing(EnumFacing face)
    {
        getTileData().setInteger(FACE_TAG, face.getIndex());
    }

    public EnumFacing getFacing()
    {
        return EnumFacing.getFront(getTileData().getInteger(FACE_TAG));
    }

    public void updateBooks()
    {
        int books = 0;
        for (ItemStack stack : this.inventory) {
            if (stack.getItem() == Items.ENCHANTED_BOOK) books++;
        }

        getTileData().setInteger(BOOKS_TAG, books);
    }

    public int getBooks()
    {
        return getTileData().getInteger(BOOKS_TAG);
    }

    public void updateTextureBlock(NBTTagCompound tag)
    {
        getTileData().setTag(PLANKS_TAG, tag);
    }

    public NBTTagCompound getTextureBlock()
    {
        return getTileData().getCompoundTag(PLANKS_TAG);
    }
}
