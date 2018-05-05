package com.ragegamingpe.wtfenchants.common.container;


import com.ragegamingpe.wtfenchants.common.container.base.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ContainerSorter extends Container
{
    public final int numSlots;

    private final IInventory playerInv;
    private IInventory[] bookshelves;
    private final BlockPos pos;

    public ContainerSorter(IInventory player, IInventory[] bookshelves, BlockPos pos)
    {
        this.playerInv = player;
        this.bookshelves = bookshelves;
        this.pos = pos;

        for (IInventory bookshelf : bookshelves) {
            for (int j = 0; j < bookshelf.getSizeInventory(); j++) {
                this.addSlotToContainer(new BaseContainer.BaseSlot(bookshelf, j, Integer.MIN_VALUE, Integer.MIN_VALUE));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new BaseContainer.BaseSlot(player, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new BaseContainer.BaseSlot(player, i, 8 + i * 18, 198));
        }

        numSlots = this.bookshelves.length * this.bookshelves[0].getSizeInventory() - 1;
        this.changeShownSlots(0.0F);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return playerIn.getDistanceSq(pos) <= 64;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < this.numSlots) {
                if (!this.mergeItemStack(itemstack1, this.numSlots, this.numSlots + 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, this.numSlots, false)) {
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

    /**
     * @param sliderPos 0 <= x <= 1
     */
    public void changeShownSlots(float sliderPos)
    {
        for (Slot slot : inventorySlots) {
            if (!(slot.inventory instanceof InventoryPlayer)) {
                slot.xPos = Integer.MIN_VALUE;
                slot.yPos = Integer.MIN_VALUE;
            }
        }

        int slotID = (int) (sliderPos * this.numSlots);
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 9; j++) {
                if (slotID > this.numSlots)
                    break;

                Slot slot = inventorySlots.get(slotID);

                slot.xPos = 8 + j * 18;
                slot.yPos = 18 + i * 18;
                slotID++;
            }
        }
    }
}
