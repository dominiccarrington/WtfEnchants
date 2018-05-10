package com.ragegamingpe.wtfenchants.client;

import com.ragegamingpe.wtfenchants.common.CommonProxy;
import com.ragegamingpe.wtfenchants.common.block.base.IModBlock;
import com.ragegamingpe.wtfenchants.common.enchantment.handler.HandlerHeightWidth;
import com.ragegamingpe.wtfenchants.common.item.base.IModItem;
import com.ragegamingpe.wtfenchants.common.lib.ModBlocks;
import com.ragegamingpe.wtfenchants.common.lib.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.input.Mouse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
        CommonProxy.registeredEvents = true;
        super.preInit(event);
        ModBlocks.ALL_BLOCKS.forEach(IModBlock::registerModels);
        ModItems.ALL_ITEMS.forEach(IModItem::registerModels);

        MinecraftForge.EVENT_BUS.register(new HandlerHeightWidth());
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
        ModBlocks.ALL_BLOCKS.forEach(IModBlock::registerRender);
        ModItems.ALL_ITEMS.forEach((item) -> {
            ItemMeshDefinition def = item.registerCustomMeshDefinition();

            if (def == null) item.registerRender();
            else Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, def);
        });
    }

    @SubscribeEvent
    public void onGuiTick(GuiScreenEvent.DrawScreenEvent event)
    {
        // SLOT 149, -93
        // LEVEL 3 ENCHANT = 236, -101
        // SLOT PLAYER 1 = 142 -187
        GuiScreen gui = event.getGui();
        int i = Mouse.getEventX() * gui.width / gui.mc.displayWidth;
        int j = gui.height - Mouse.getEventY() * gui.height / gui.mc.displayHeight - 1;
//        System.out.println(i + " - " + j);

        if (false) {
            if (event.getGui() instanceof GuiEnchantment) {
                GuiEnchantment guiEnchantment = (GuiEnchantment) event.getGui();
                ContainerEnchantment container = (ContainerEnchantment) guiEnchantment.inventorySlots;
                Method mouseClicked = ReflectionHelper.findMethod(GuiEnchantment.class, "mouseClicked", "mouseClicked", int.class, int.class, int.class);
                Method handleMouseClick = ReflectionHelper.findMethod(GuiContainer.class, "handleMouseClick", "handleMouseClick", Slot.class, int.class, int.class, ClickType.class);
                try {
                    if (!container.getSlot(0).getHasStack()) {
                        // Put Item In
                        handleMouseClick.invoke(guiEnchantment, container.getSlot(0), 0, 0, ClickType.SWAP);
                    } else if (container.getSlot(0).getStack() != ItemStack.EMPTY) {
                        if (EnchantmentHelper.getEnchantments(container.getSlot(0).getStack()).size() > 0) {
                            handleMouseClick.invoke(guiEnchantment, container.getSlot(0), 0, 0, ClickType.THROW);
                        } else {
                            mouseClicked.invoke(guiEnchantment, 236, 101, 0);
                        }
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
