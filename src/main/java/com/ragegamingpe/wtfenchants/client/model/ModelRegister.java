package com.ragegamingpe.wtfenchants.client.model;

import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelRegister
{
    public static void registerItemModel(Block block, int meta, String file)
    {
        registerItemModel(Item.getItemFromBlock(block), meta, file);
    }

    public static void registerItemModel(Item item, int meta, String file)
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                .register(item, meta, new ModelResourceLocation(LibMisc.MOD_ID + ":" + file, "inventory"));
    }
}

