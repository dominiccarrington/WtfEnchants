package com.ragegamingpe.wtfenchants.common.block.te.base;

import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

public class ModBaseTEInventory extends TileEntity implements IInventory
{
    private String name;
    private boolean hasCustomName = false;
    private int slots;
    protected NonNullList<ItemStack> inventory;

    public ModBaseTEInventory()
    {

    }

    public ModBaseTEInventory(String name, int slots)
    {
        this.name = name;
        this.slots = slots;
        this.inventory = NonNullList.withSize(slots, ItemStack.EMPTY);
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public boolean hasCustomName()
    {
        return hasCustomName;
    }

    public void setCustomName(String name)
    {
        this.name = name;
        this.hasCustomName = true;
    }

    @Nullable
    @Override
    public ITextComponent getDisplayName()
    {
        return this.hasCustomName()
                ? new TextComponentString(this.name)
                : new TextComponentTranslation(String.format("container.%s:%s.name", LibMisc.MOD_ID, this.name));
    }

    @Override
    public int getSizeInventory()
    {
        return this.slots;
    }

    @Override
    public boolean isEmpty()
    {
        for (ItemStack stack : this.inventory)
            if (stack != ItemStack.EMPTY)
                return false;
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        if (index < 0 || index > this.getSizeInventory())
            return ItemStack.EMPTY;
        return this.inventory.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        if (this.getStackInSlot(index) != ItemStack.EMPTY) {
            ItemStack itemstack;

            if (this.getStackInSlot(index).getCount() <= count) {
                itemstack = this.getStackInSlot(index);
                this.setInventorySlotContents(index, ItemStack.EMPTY);
            } else {
                itemstack = this.getStackInSlot(index).splitStack(count);

                if (this.getStackInSlot(index).getCount() <= 0) {
                    this.setInventorySlotContents(index, ItemStack.EMPTY);
                } else {
                    //Just to show that changes happened
                    this.setInventorySlotContents(index, this.getStackInSlot(index));
                }

            }
            this.markDirty();
            return itemstack;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        ItemStack stack = this.getStackInSlot(index);
        setInventorySlotContents(index, ItemStack.EMPTY);

        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        if (index < 0 || index >= this.getSizeInventory())
            return;

        if (stack != ItemStack.EMPTY && stack.getCount() > this.getInventoryStackLimit())
            stack.setCount(this.getInventoryStackLimit());

        if (stack != ItemStack.EMPTY && stack.getCount() == 0)
            stack = ItemStack.EMPTY;

        this.inventory.set(index, stack);
        this.markDirty();
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        ItemStackHelper.loadAllItems(compound, this.inventory);

        if (compound.hasKey("CustomName")) {
            this.name = compound.getString("CustomName");
            this.hasCustomName = true;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);

        ItemStackHelper.saveAllItems(compound, this.inventory, true);
        if (this.hasCustomName()) compound.setString("CustomName", this.name);

        return compound;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        return this.getWorld().getTileEntity(this.getPos()) == this && player.getDistanceSq(this.pos.add(0.5, 0.5, 0.5)) <= 64;

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
        return true;
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
        this.inventory.clear();
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
