package com.ragegamingpe.wtfenchants.common.container;

import com.ragegamingpe.wtfenchants.common.container.base.BaseContainer;
import com.ragegamingpe.wtfenchants.common.lib.ModBlocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ContainerDisenchantment extends Container
{
    private final Random rand = new Random();

    private final EntityPlayer player;
    private final IInventory playerInv;
    private final World world;
    private final BlockPos pos;

    public final IInventory inputSlots;
    public final IInventory outputSlots;
    public int[] disenchantLevels;
    private ItemStack takenBook = ItemStack.EMPTY;
    private int xpSpeed;

    public ContainerDisenchantment(EntityPlayer player, World world, BlockPos pos)
    {
        this.player = player;
        this.playerInv = player.inventory;
        this.world = world;
        this.pos = pos;
        this.xpSpeed = player.getXPSeed();

        this.inputSlots = new InventoryBasic("Disenchant", true, 2)
        {
            @Override
            public void markDirty()
            {
                ContainerDisenchantment.this.onCraftMatrixChanged(this);
                super.markDirty();
            }
        };

        this.outputSlots = new InventoryBasic("Books", true, 8);

        this.addSlotToContainer(new BaseContainer.BaseSlot(this.inputSlots, 0, 34, 47));
        this.addSlotToContainer(new BaseContainer.BaseSlot(this.inputSlots, 1, 54, 47));
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                this.addSlotToContainer(new ResultSlot(this.outputSlots, j + i * 2, 106 + j * 20, 6 + i * 18));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new BaseContainer.BaseSlot(this.playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new BaseContainer.BaseSlot(this.playerInv, i, 8 + i * 18, 142));
        }

        this.disenchantLevels = new int[this.outputSlots.getSizeInventory()];
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        if (!this.world.isRemote) {
            this.outputSlots.clear();
            ItemStack inputStack1 = this.inputSlots.getStackInSlot(0);
            ItemStack inputStack2 = this.inputSlots.getStackInSlot(1);

            if (inventoryIn.equals(this.outputSlots)) {
                if (canDisenchant(this.player, this.takenBook)) {
                    Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(inputStack1);
                    Enchantment takenEnchant = EnchantmentHelper.getEnchantments(this.takenBook).keySet().toArray(new Enchantment[0])[0];
                    int enchantLevel = enchantments.get(takenEnchant);
                    int requiredLevel = this.getDisenchantLevel(takenEnchant, enchantLevel);

                    enchantments.remove(takenEnchant);

                    this.takenBook = ItemStack.EMPTY;
                    EnchantmentHelper.setEnchantments(enchantments, inputStack1);
                    this.player.onEnchant(inputStack1, requiredLevel);
                    this.xpSpeed = this.player.getXPSeed();

                    if (inputStack1.isItemStackDamageable()) {
                        inputStack1.setItemDamage(
                                inputStack1.getItemDamage() + calcDamage(inputStack1, takenEnchant, enchantLevel)
                        );

                        if (inputStack1.getItemDamage() >= inputStack1.getMaxDamage()) {
                            this.inputSlots.setInventorySlotContents(0, ItemStack.EMPTY);
                        }
                    }

                    inputStack2.shrink(1);
                    if (inputStack2.getCount() <= 0) {
                        this.inputSlots.setInventorySlotContents(1, ItemStack.EMPTY);
                    }
                }
            }

            // Get the information about the stacks again (Could have changed from above)
            inputStack1 = this.inputSlots.getStackInSlot(0);
            inputStack2 = this.inputSlots.getStackInSlot(1);
            Map<Enchantment, Integer> enchants;

            if (!inputStack1.isEmpty() &&
                    !(inputStack1.getItem() instanceof ItemEnchantedBook) &&
                    !(enchants = EnchantmentHelper.getEnchantments(inputStack1)).isEmpty() &&
                    !inputStack2.isEmpty() &&
                    inputStack2.getItem() == Items.BOOK
                    ) {
                Enchantment[] e = enchants.keySet().toArray(new Enchantment[0]);
                for (int i = 0; i < Math.min(e.length, this.outputSlots.getSizeInventory()); i++) {
                    ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK, 1);

                    Map<Enchantment, Integer> bookEnchants = new HashMap<>();
                    bookEnchants.put(e[i], enchants.get(e[i]));
                    EnchantmentHelper.setEnchantments(bookEnchants, stack);

                    int levelToDisenchant = this.getDisenchantLevel(e[i], enchants.get(e[i]));
                    this.disenchantLevels[i] = levelToDisenchant;
                    this.outputSlots.setInventorySlotContents(i, stack);
                }
            }
        }

        this.detectAndSendChanges();
    }

    @Override
    public void addListener(IContainerListener listener)
    {
        super.addListener(listener);
        this.broadcastInformation(listener);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (IContainerListener icontainerlistener : this.listeners) {
            this.broadcastInformation(icontainerlistener);
        }
    }

    private void broadcastInformation(IContainerListener listener)
    {
        for (int i = 0; i < this.disenchantLevels.length; i++) {
            listener.sendWindowProperty(this, i, this.disenchantLevels[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        super.updateProgressBar(id, data);
        this.disenchantLevels[id] = data;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            int inventorySize = this.inputSlots.getSizeInventory();
            if (index < inventorySize) {
                if (!this.mergeItemStack(itemstack1, inventorySize + this.outputSlots.getSizeInventory(), inventorySize + this.outputSlots.getSizeInventory() + 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, this.inputSlots.getSizeInventory(), false)) {
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

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.world.getBlockState(this.pos).getBlock() == ModBlocks.DISENCHANTING_TABLE && playerIn.getDistanceSq(this.pos.add(0.5D, 0.5D, 0.5D)) <= 64.0D;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn)
    {
        if (!this.world.isRemote)
            InventoryHelper.dropInventoryItems(this.world, playerIn, this.inputSlots);
    }

    public boolean canDisenchant(EntityPlayer player, ItemStack stack)
    {
        return player.isCreative() || player.experienceLevel >= getDisenchantLevel(stack);
    }

    public int calcDamage(ItemStack stack, Enchantment enchant, int level)
    {
        return (stack.getItemDamage() / stack.getMaxDamage()) * 100 + (int) Math.floor(this.player.world.rand.nextFloat() * stack.getMaxDamage());
    }

    public int getDisenchantLevel(Enchantment enchant, int level)
    {
        // TODO BALANCE, BALANCE, BALANCE!!!
        int mod = 0;
        switch (enchant.getRarity()) {
            case COMMON:
                mod = 1;
                break;
            case UNCOMMON:
                mod = 2;
                break;
            case RARE:
                mod = 4;
                break;
            case VERY_RARE:
                mod = 8;
        }

        this.rand.setSeed(this.xpSpeed);
        return mod * level + (int) (mod + this.rand.nextInt(enchant.getMinEnchantability(level)) * 0.75);
    }

    public int getDisenchantLevel(ItemStack stack)
    {
        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
        if (enchants.size() <= 0) return Integer.MAX_VALUE;

        return getDisenchantLevel(enchants.keySet().toArray(new Enchantment[0])[0], enchants.values().toArray(new Integer[0])[0]);
    }

    private class ResultSlot extends BaseContainer.BaseSlot
    {
        public ResultSlot(IInventory inventory, int i, int x, int y)
        {
            super(inventory, i, x, y);
        }

        @Override
        public boolean isEnabled()
        {
            return this.getHasStack();
        }

        @Override
        public boolean canTakeStack(EntityPlayer playerIn)
        {
            if (!this.getHasStack()) return false;

            ItemStack stack = this.getStack();
            return canDisenchant(playerIn, stack);
        }

        @Override
        public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack)
        {
            ContainerDisenchantment.this.takenBook = stack;
            ContainerDisenchantment.this.onCraftMatrixChanged(this.inventory);
            return super.onTake(thePlayer, stack);
        }
    }
}
