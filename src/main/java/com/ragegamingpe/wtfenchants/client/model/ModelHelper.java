package com.ragegamingpe.wtfenchants.client.model;

import com.google.common.collect.ImmutableMap;
import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelHelper
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

    public static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> getTransforms(IBakedModel model)
    {
        ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> builder = ImmutableMap.builder();
        for (ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values()) {
            TRSRTransformation transformation = new TRSRTransformation(model.handlePerspective(type).getRight());
            if (!transformation.equals(TRSRTransformation.identity())) {
                builder.put(type, TRSRTransformation.blockCenterToCorner(transformation));
            }
        }
        return builder.build();
    }

    public static TextureAtlasSprite getTextureFromBlock(Block block, int meta)
    {
        IBlockState state = block.getStateFromMeta(meta);
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
    }
}

