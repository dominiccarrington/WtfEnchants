package com.ragegamingpe.wtfenchants.common.block.te;

import com.ragegamingpe.wtfenchants.common.block.BlockSorter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

public class TileEntitySorter extends TileEntity implements IInventory
{
    private IInventory[] inventories;

    public TileEntitySorter()
    {
    }

    @Override
    public String getName()
    {
        return this.getInventories()[0].getName();
    }

    @Override
    public boolean hasCustomName()
    {
        return this.getInventories()[0].hasCustomName();
    }

    @Override
    public int getSizeInventory()
    {
        int size = 0;
        for (IInventory inventory : this.getInventories()) {
            size += inventory.getSizeInventory();
        }

        return size;
    }

    @Override
    public boolean isEmpty()
    {
        for (IInventory inventory : this.getInventories()) {
            if (!inventory.isEmpty())
                return false;
        }

        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        int size = this.getInventories()[0].getSizeInventory();
        int bookshelf = index / size;
        if (bookshelf < 0 || bookshelf > this.getInventories().length) return ItemStack.EMPTY;

        return this.getInventories()[bookshelf].getStackInSlot(index % size);
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        int size = this.getInventories()[0].getSizeInventory();
        int bookshelf = index / size;
        if (bookshelf < 0 || bookshelf > this.getInventories().length) return ItemStack.EMPTY;

        return this.getInventories()[bookshelf].decrStackSize(index % size, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        int size = this.getInventories()[0].getSizeInventory();
        int bookshelf = index / size;
        if (bookshelf < 0 || bookshelf > this.getInventories().length) return ItemStack.EMPTY;

        return this.getInventories()[bookshelf].removeStackFromSlot(index % size);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        int size = this.getInventories()[0].getSizeInventory();
        int bookshelf = index / size;
        if (bookshelf < 0 || bookshelf > this.getInventories().length) return;

        this.getInventories()[bookshelf].setInventorySlotContents(index % size, stack);
    }

    @Override
    public int getInventoryStackLimit()
    {
        return this.getInventories()[0].getInventoryStackLimit();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        for (IInventory inventory : this.getInventories()) {
            if (!inventory.isUsableByPlayer(player))
                return false;
        }
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player)
    {

    }

    @Override
    public void closeInventory(EntityPlayer player)
    {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        int size = this.getInventories()[0].getSizeInventory();
        int bookshelf = index / size;
        if (bookshelf < 0 || bookshelf > this.getInventories().length) return false;

        return this.getInventories()[bookshelf].isItemValidForSlot(index % size, stack);
    }

    @Override
    public int getField(int id)
    {
        return 0;
    }

    @Override
    public void setField(int id, int value)
    {

    }

    @Override
    public int getFieldCount()
    {
        return 0;
    }

    @Override
    public void clear()
    {

    }

    public void deleteCachedInventories()
    {
        System.out.println("Delete");
        this.inventories = null;
    }

    public IInventory[] getInventories()
    {
        if (this.inventories == null)
            this.inventories = BlockSorter.getAllBookshelvesConnected(this.getWorld(), this.getPos());

        return this.inventories;
    }

    private IItemHandler itemHandler;

    protected IItemHandler createUnsidedHandler()
    {
        return new InvWrapper(this);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) (itemHandler != null ? itemHandler : (itemHandler = createUnsidedHandler()));

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}
