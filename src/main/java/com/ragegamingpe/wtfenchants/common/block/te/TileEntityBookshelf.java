package com.ragegamingpe.wtfenchants.common.block.te;

import com.ragegamingpe.wtfenchants.common.block.BlockBookshelf;
import com.ragegamingpe.wtfenchants.common.block.te.base.ModBaseTEInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityBookshelf extends ModBaseTEInventory
{
    private int books;

    public TileEntityBookshelf()
    {
        super("bookshelf", 14);
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
        if (!world.isRemote)
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, getNumberOfBooks());
    }

    @Override
    public boolean receiveClientEvent(int id, int type)
    {
        if (id == 1) {
            this.books = type;
            BlockBookshelf.setState(type, this.world, this.pos);
            return true;
        }

        return super.receiveClientEvent(id, type);
    }

    public int getNumberOfBooks()
    {
        if (!world.isRemote) {
            this.books = 0;
            for (ItemStack stack : this.inventory) {
                if (stack.getItem() == Items.ENCHANTED_BOOK) this.books++;
            }
        }
        return this.books;
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        NBTTagCompound comp = super.getUpdateTag();
        comp.setInteger("Books", getNumberOfBooks());
        return comp;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        super.handleUpdateTag(tag);
        this.books = tag.getInteger("Books");
    }
}