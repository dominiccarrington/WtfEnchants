package com.ragegamingpe.wtfenchants.common;

import com.ragegamingpe.wtfenchants.common.block.BlockXpFarmer;
import com.ragegamingpe.wtfenchants.common.block.base.ModBlock;
import com.ragegamingpe.wtfenchants.common.block.te.*;
import com.ragegamingpe.wtfenchants.common.command.WtfCommand;
import com.ragegamingpe.wtfenchants.common.command.XpDebugCommand;
import com.ragegamingpe.wtfenchants.common.enchantment.base.ModBaseEnchantment;
import com.ragegamingpe.wtfenchants.common.helper.Config;
import com.ragegamingpe.wtfenchants.common.item.base.ModItem;
import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import com.ragegamingpe.wtfenchants.common.lib.ModBlocks;
import com.ragegamingpe.wtfenchants.common.lib.ModEnchantments;
import com.ragegamingpe.wtfenchants.common.lib.ModItems;
import com.ragegamingpe.wtfenchants.common.network.GuiHandler;
import com.ragegamingpe.wtfenchants.common.network.MessageHandler;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommonProxy
{
    protected static boolean registeredEvents = false;

    public void preInit(FMLPreInitializationEvent event)
    {
        WtfEnchants.logger = event.getModLog();
        Config.setConfigInstance(new Configuration(event.getSuggestedConfigurationFile()), event.getSide());
        Config.sync();

        if (!registeredEvents) {
            MinecraftForge.EVENT_BUS.register(this);
            registeredEvents = true;
        }

        NetworkRegistry.INSTANCE.registerGuiHandler(WtfEnchants.instance, new GuiHandler());
        MessageHandler.init();
    }

    public void init(FMLInitializationEvent event)
    {
    }

    public void postInit(FMLPostInitializationEvent event)
    {
        for (ModBaseEnchantment enchant : ModEnchantments.ALL_ENCHANTMENTS) {
            if (enchant.isEnabled())
                enchant.onPostInit();
        }

        MinecraftForge.EVENT_BUS.register(BlockXpFarmer.class);
    }

    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new WtfCommand());

        if (WtfEnchants.isDebugEnvironment()) {
            event.registerServerCommand(new XpDebugCommand());
        }
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(ModBlocks.ALL_BLOCKS.toArray(new ModBlock[0]));

        GameRegistry.registerTileEntity(TileEntityBookshelf.class, LibMisc.MOD_ID + ":bookshelf");
        GameRegistry.registerTileEntity(TileEntitySorter.class, LibMisc.MOD_ID + ":sorter");
        GameRegistry.registerTileEntity(TileEntityXpStore.class, LibMisc.MOD_ID + ":xp_store");
        GameRegistry.registerTileEntity(TileEntityXpFarmer.class, LibMisc.MOD_ID + ":xp_farmer");
        GameRegistry.registerTileEntity(TileEntityXpFunnel.class, LibMisc.MOD_ID + ":xp_funnel");

        ModBlocks.ALL_BLOCKS.forEach(block -> {
            if (WtfEnchants.isDebugEnvironment() && block.getCreativeTabToDisplayOn() == null) {
                WtfEnchants.logger.error(block.getUnlocalizedName() + " is not a member of a Creative Tab (This may not be a bug!)");
            }
        });
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event)
    {
        ItemBlock[] itemBlocks = new ItemBlock[ModBlocks.ALL_BLOCKS.size()];
        for (int i = 0; i < ModBlocks.ALL_BLOCKS.size(); i++) {
            ModBlock block = ModBlocks.ALL_BLOCKS.get(i);
            itemBlocks[i] = block.getItemBlock();

            if (WtfEnchants.isDebugEnvironment() && !I18n.hasKey(itemBlocks[i].getUnlocalizedName() + ".name")) {
                WtfEnchants.logger.error(itemBlocks[i].getUnlocalizedName() + " doesn't have a translation");
            }
        }
        event.getRegistry().registerAll(itemBlocks);

        event.getRegistry().registerAll(ModItems.ALL_ITEMS.toArray(new ModItem[0]));
        ModItems.ALL_ITEMS.forEach((item) -> {
            if (WtfEnchants.isDebugEnvironment() && !I18n.hasKey(item.getUnlocalizedName() + ".name")) {
                WtfEnchants.logger.error(item.getUnlocalizedName() + " doesn't have a translation");
            }

            if (WtfEnchants.isDebugEnvironment() && item.getCreativeTab() == null) {
                WtfEnchants.logger.error(item.getUnlocalizedName() + " is not a member of a Creative Tab (This may not be a bug!)");
            }
        });
    }

    @SubscribeEvent
    public void registerEnchantments(RegistryEvent.Register<Enchantment> event)
    {
        List<ModBaseEnchantment> tempStore = new ArrayList<>(ModEnchantments.ALL_ENCHANTMENTS);
        tempStore.removeIf(enchantment -> !enchantment.isEnabled());

        ModEnchantments.ALL_ENCHANTMENTS.forEach(enchantment -> {
            if (WtfEnchants.isDebugEnvironment() && !I18n.hasKey(enchantment.getName())) {
                WtfEnchants.logger.error(enchantment.getName() + " doesn't have a translation");
            }
        });

        event.getRegistry().registerAll(tempStore.toArray(new ModBaseEnchantment[0]));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END && event.side == Side.SERVER) {
            EntityPlayer player = event.player;

            Iterable<ItemStack> armorInv = player.getArmorInventoryList();

            for (ItemStack armorPiece : armorInv) {
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(armorPiece);

                for (Map.Entry<Enchantment, Integer> enchant : enchantments.entrySet()) {
                    if (enchant.getKey() instanceof ModBaseEnchantment) {
                        ((ModBaseEnchantment) enchant.getKey()).onArmorTick(event, player, armorPiece, enchant.getValue());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockBrokenDrops(BlockEvent.HarvestDropsEvent event)
    {
        if (event.getHarvester() != null) {
            ItemStack stack = event.getHarvester().getHeldItemMainhand();
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

            for (Map.Entry<Enchantment, Integer> enchant : enchantments.entrySet()) {
                if (enchant.getKey() instanceof ModBaseEnchantment) {
                    ((ModBaseEnchantment) enchant.getKey()).onBlockBrokenDrops(event, event.getHarvester(), event.getState(), event.getPos(), stack, event.getFortuneLevel(), event.getDrops());
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockBroken(BlockEvent.BreakEvent event)
    {
        if (event.getPlayer() != null) {
            ItemStack stack = event.getPlayer().getHeldItemMainhand();
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

            for (Map.Entry<Enchantment, Integer> enchant : enchantments.entrySet()) {
                if (enchant.getKey() instanceof ModBaseEnchantment) {
                    ((ModBaseEnchantment) enchant.getKey()).onBlockBroken(event, event.getPlayer(), event.getState(), event.getPos(), stack);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event)
    {
        EntityLivingBase entity = event.getEntityLiving();
        Iterable<ItemStack> stacks = entity.getEquipmentAndArmor();

        for (ItemStack stack : stacks) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

            for (Map.Entry<Enchantment, Integer> enchant : enchantments.entrySet()) {
                if (enchant.getKey() instanceof ModBaseEnchantment) {
                    ((ModBaseEnchantment) enchant.getKey()).onEntityDeath(event, event.getEntityLiving(), event.getSource(), stack, enchant.getValue());
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityDrops(LivingDropsEvent event)
    {
        EntityLivingBase entity = event.getEntityLiving();
        Iterable<ItemStack> stacks = entity.getEquipmentAndArmor();

        for (ItemStack stack : stacks) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

            for (Map.Entry<Enchantment, Integer> enchant : enchantments.entrySet()) {
                if (enchant.getKey() instanceof ModBaseEnchantment) {
                    ((ModBaseEnchantment) enchant.getKey()).onEntityDeathDrops(event, event.getEntityLiving(), event.getSource(), stack, event.getDrops(), enchant.getValue());
                }
            }
        }
    }
}
