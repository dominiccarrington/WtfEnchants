package com.ragegamingpe.wtfenchants.client;

import com.ragegamingpe.wtfenchants.client.model.CommonBookshelfModel;
import com.ragegamingpe.wtfenchants.common.CommonProxy;
import com.ragegamingpe.wtfenchants.common.WtfEnchants;
import com.ragegamingpe.wtfenchants.common.block.base.IModBlock;
import com.ragegamingpe.wtfenchants.common.item.base.IModItem;
import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import com.ragegamingpe.wtfenchants.common.lib.ModBlocks;
import com.ragegamingpe.wtfenchants.common.lib.ModItems;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.input.Mouse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
        ModBlocks.ALL_BLOCKS.forEach(IModBlock::registerRender);
        ModItems.ALL_ITEMS.forEach(IModItem::registerRender);
    }

    @SubscribeEvent
    public void onModelRegistry(ModelRegistryEvent event)
    {
        Item tableItem = Item.getItemFromBlock(ModBlocks.COMMON_BOOKSHELF);
        ModelLoader.setCustomModelResourceLocation(tableItem, 0, new ModelResourceLocation(LibMisc.MOD_ID + ":bookshelf", "normal"));
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event)
    {
        ResourceLocation[] bookshelves = new ResourceLocation[15];
        for (int i = 0; i < bookshelves.length; i++) {
            bookshelves[i] = new ResourceLocation(LibMisc.MOD_ID, "block/bookshelf/bookshelf_" + i);
        }

        replaceTableModel(
                new ModelResourceLocation(LibMisc.MOD_ID + ":bookshelf", "normal"),
                bookshelves,
                event
        );
    }

    public static void replaceTableModel(ModelResourceLocation modelVariantLocation, ResourceLocation[] modelLocations, ModelBakeEvent event)
    {
        try {
            boolean flag = false;
            List<ResourceLocation> missing = new ArrayList<>();

            IModel[] models = new IModel[modelLocations.length];
            for (int i = 0; i < modelLocations.length; i++) {
                IModel model = ModelLoaderRegistry.getModel(modelLocations[i]);
                if (model instanceof IModel) {
                    models[i] = model;
                } else {
                    flag = true;
                    missing.add(modelLocations[i]);
                }
            }

            if (!flag) {
                IBakedModel standard = event.getModelRegistry().getObject(modelVariantLocation);
                if (standard instanceof IBakedModel) {
                    IBakedModel finalModel = new CommonBookshelfModel(standard, models, DefaultVertexFormats.BLOCK);

                    event.getModelRegistry().putObject(modelVariantLocation, finalModel);
                } else {
                    WtfEnchants.logger.warn("Could not find " + modelVariantLocation);
                }
            } else {
                WtfEnchants.logger.warn("Could not find " + Arrays.toString(missing.toArray(new ResourceLocation[0])));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int slot = 0;

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

        if (true) {
            if (event.getGui() instanceof GuiEnchantment) {
                GuiEnchantment guiEnchantment = (GuiEnchantment) event.getGui();
                ContainerEnchantment container = (ContainerEnchantment) guiEnchantment.inventorySlots;
                Method mouseClicked = ReflectionHelper.findMethod(GuiEnchantment.class, "mouseClicked", "mouseClicked", int.class, int.class, int.class);
                Method handleMouseClick = ReflectionHelper.findMethod(GuiContainer.class, "handleMouseClick", "handleMouseClick", Slot.class, int.class, int.class, ClickType.class);
                try {
                    if (!container.getSlot(0).getHasStack()) {
                        // Put Item In
                        slot++;
                        if (slot >= 10) {
                            slot = 0;
                        }
                        handleMouseClick.invoke(guiEnchantment, container.getSlot(0), 0, slot, ClickType.SWAP);
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
