package com.ragegamingpe.wtfenchants.common.enchantment;

import com.ragegamingpe.wtfenchants.common.enchantment.base.ModBaseEnchantment;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class SuperSoftEnchantment extends ModBaseEnchantment
{
    public SuperSoftEnchantment()
    {
        super("super_soft", Rarity.VERY_RARE, EnumEnchantmentType.DIGGER);
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel)
    {
        return 29;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel)
    {
        return this.getMinEnchantability(enchantmentLevel) + 50;
    }

    @Override
    public int getMaxLevel()
    {
        return 1;
    }

    @Override
    public boolean isTreasureEnchantment()
    {
        return true;
    }

    @Override
    public void onPostInit()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    protected boolean canApplyTogether(Enchantment ench)
    {
        return super.canApplyTogether(ench) && ench != Enchantments.SILK_TOUCH && ench != Enchantments.FORTUNE;
    }

    private TileEntityMobSpawner teTempStore;

    @Override
    public void onBlockBrokenDrops(BlockEvent.HarvestDropsEvent event, EntityPlayer harvester, IBlockState state, BlockPos pos, ItemStack stack, int fortuneLevel, List<ItemStack> drops)
    {
        if (state.getBlock() == Blocks.MOB_SPAWNER) {
            drops.clear();

            ItemStack block = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));

            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound = teTempStore.writeToNBT(nbttagcompound);
            block.setTagInfo("BlockEntityTag", nbttagcompound);
            teTempStore = null;

            drops.add(block);
        }
    }

    @Override
    public void onBlockBroken(BlockEvent.BreakEvent event, EntityPlayer harvester, IBlockState state, BlockPos pos, ItemStack stack)
    {
        World world = harvester.getEntityWorld();
        if (state.getBlock() == Blocks.MOB_SPAWNER && world.getTileEntity(pos) instanceof TileEntityMobSpawner) {
            teTempStore = (TileEntityMobSpawner) world.getTileEntity(pos);
            event.setExpToDrop(0);
        }
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.PlaceEvent event)
    {
        IBlockState placedBlock = event.getPlacedBlock();

        if (placedBlock.getBlock() == Blocks.MOB_SPAWNER) {
            EntityPlayer player = event.getPlayer();
            EnumHand hand = event.getHand();
            ItemStack stack = player.getHeldItem(hand);
            World world = player.getEntityWorld();
            TileEntity te = world.getTileEntity(event.getPos());

            if (stack.hasTagCompound() && te instanceof TileEntityMobSpawner && !world.isRemote && te.onlyOpsCanSetNbt() && (player == null || !player.canUseCommandBlock())) {
                NBTTagCompound compound = stack.getTagCompound();
                assert compound != null;
                if (compound.hasKey("BlockEntityTag")) {
                    compound = compound.getCompoundTag("BlockEntityTag");
                    TileEntityMobSpawner spawner = (TileEntityMobSpawner) te;
                    spawner.getSpawnerBaseLogic().readFromNBT(compound);
                }
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onItemHover(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        if (Block.getBlockFromItem(stack.getItem()) == Blocks.MOB_SPAWNER) {
            // {MaxNearbyEntities:6s,RequiredPlayerRange:16s,SpawnCount:4s,SpawnData:{id:"minecraft:pig"},MaxSpawnDelay:800s,Delay:595s,x:44,y:67,z:237,id:"wtfenchants:mob_spawner",SpawnRange:4s,MinSpawnDelay:200s,SpawnPotentials:[{Entity:{id:"minecraft:pig"},Weight:1}]}
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("BlockEntityTag")) {
                NBTTagCompound blockEntityTag = stack.getTagCompound().getCompoundTag("BlockEntityTag");

                String id = blockEntityTag.getCompoundTag("SpawnData").getString("id");

                String name = EntityList.getTranslationName(new ResourceLocation(id));
                event.getToolTip().add(1, name);
            }
        }
    }
}
