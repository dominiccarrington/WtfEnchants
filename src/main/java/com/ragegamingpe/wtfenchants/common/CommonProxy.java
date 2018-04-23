package com.ragegamingpe.wtfenchants.common;

import com.ragegamingpe.wtfenchants.common.block.base.ModBlock;
import com.ragegamingpe.wtfenchants.common.enchantment.AutoFeedEnchantment;
import com.ragegamingpe.wtfenchants.common.enchantment.base.ModBaseEnchantment;
import com.ragegamingpe.wtfenchants.common.item.base.ModItem;
import com.ragegamingpe.wtfenchants.common.lib.ModBlocks;
import com.ragegamingpe.wtfenchants.common.lib.ModItems;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Map;

public class CommonProxy
{
    protected static boolean registeredEvents = false;

    public void preInit(FMLPreInitializationEvent event)
    {
        WtfEnchants.logger = event.getModLog();
        if (!registeredEvents) {
            MinecraftForge.EVENT_BUS.register(this);
            registeredEvents = true;
        }

//        Items.BOW.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter()
//        {
//            @Override
//            @SideOnly(Side.CLIENT)
//            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
//            {
//                int quickDraw = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.QUICK_DRAW, stack);
//                return (entityIn == null || entityIn.getActiveItemStack().getItem() != Items.BOW) ? 0.0F : (float) (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F;
//            }
//        });
    }

    public void init(FMLInitializationEvent event)
    {
    }

    public void postInit(FMLPostInitializationEvent event)
    {
    }

    public void serverStarting(FMLServerStartingEvent event)
    {
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(ModBlocks.ALL_BLOCKS.toArray(new ModBlock[ModBlocks.ALL_BLOCKS.size()]));
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event)
    {
        ItemBlock[] itemBlocks = new ItemBlock[ModBlocks.ALL_BLOCKS.size()];
        for (int i = 0; i < ModBlocks.ALL_BLOCKS.size(); i++) {
            Block block = ModBlocks.ALL_BLOCKS.get(i);
            itemBlocks[i] = (ItemBlock) new ItemBlock(block).setRegistryName(block.getRegistryName());
        }
        event.getRegistry().registerAll(itemBlocks);

        event.getRegistry().registerAll(ModItems.ALL_ITEMS.toArray(new ModItem[ModItems.ALL_ITEMS.size()]));
    }

    @SubscribeEvent
    public void registerEnchantments(RegistryEvent.Register<Enchantment> event)
    {
        event.getRegistry().registerAll(
                new AutoFeedEnchantment()
        );
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END) {
            EntityPlayer player = event.player;

            Iterable<ItemStack> armorInv = player.getArmorInventoryList();

            for (ItemStack armorPiece : armorInv) {
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(armorPiece);

                for (Map.Entry<Enchantment, Integer> enchant : enchantments.entrySet()) {
                    if (enchant.getKey() instanceof ModBaseEnchantment) {
                        ((ModBaseEnchantment) enchant.getKey()).onArmorTick(armorPiece, enchant.getValue());
                    }
                }
            }
        }
    }
}
