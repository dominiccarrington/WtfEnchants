package com.ragegamingpe.wtfenchants.common.container.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class BaseContainer extends Container
{
    protected IInventory player;
    protected IInventory te;

    public BaseContainer(IInventory player, IInventory te)
    {
        this.player = player;
        this.te = te;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.te.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            int inventorySize = this.getTileEntityInventory().getSizeInventory();
            if (index < inventorySize) {
                if (!this.mergeItemStack(itemstack1, inventorySize, inventorySize + 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, inventorySize, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    public IInventory getPlayerInventory()
    {
        return this.player;
    }

    public IInventory getTileEntityInventory()
    {
        return this.te;
    }

    public static class BaseSlot extends Slot
    {
        public BaseSlot(IInventory inventoryIn, int index, int xPosition, int yPosition)
        {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack)
        {
            return this.inventory.isItemValidForSlot(this.getSlotIndex(), stack);
        }
    }
}
