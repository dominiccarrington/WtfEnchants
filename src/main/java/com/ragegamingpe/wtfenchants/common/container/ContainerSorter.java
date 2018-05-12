package com.ragegamingpe.wtfenchants.common.container;


import com.ragegamingpe.wtfenchants.client.gui.container.GuiContainerSorter;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntitySorter;
import com.ragegamingpe.wtfenchants.common.container.base.BaseContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContainerSorter extends Container
{
    private final IInventory playerInv;
    public TileEntitySorter sorter;
    private final BlockPos pos;

    public ContainerSorter(IInventory player, TileEntitySorter sorter, BlockPos pos)
    {
        this.playerInv = player;
        this.sorter = sorter;
        this.pos = pos;

        for (int j = 0; j < sorter.getSizeInventory(); j++) {
            this.addSlotToContainer(new BaseContainer.BaseSlot(sorter, j, Integer.MIN_VALUE, Integer.MIN_VALUE));
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new BaseContainer.BaseSlot(player, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new BaseContainer.BaseSlot(player, i, 8 + i * 18, 198));
        }
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

            if (index < this.sorter.getSizeInventory()) {
                if (!this.mergeItemStack(itemstack1, this.sorter.getSizeInventory(), this.sorter.getSizeInventory() + 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, this.sorter.getSizeInventory(), false)) {
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

        List<Slot> validSlots = new ArrayList<>(inventorySlots);

        validSlots.removeIf((slot) -> {
            if (slot.inventory instanceof InventoryPlayer) {
                return true;
            }

            if (GuiContainerSorter.selectedEnchant == null) return false;
            ItemStack stack = slot.getStack();

            if (stack.isEmpty()) {
                return true;
            }

            Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
            for (Enchantment enchant : enchants.keySet()) {
                if (GuiContainerSorter.matchesSelectedEnchant(enchant)) {
                    return false;
                }
            }

            return true;
        });

        int slotID = (int) (sliderPos * (validSlots.size() - 1));
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 9; j++) {
                if (slotID >= validSlots.size())
                    break;

                Slot slot = validSlots.get(slotID);

                slot.xPos = 8 + j * 18;
                slot.yPos = 18 + i * 18;
                slotID++;
            }
        }
    }
}
