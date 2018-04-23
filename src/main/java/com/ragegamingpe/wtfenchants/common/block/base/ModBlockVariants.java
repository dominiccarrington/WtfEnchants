package com.ragegamingpe.wtfenchants.common.block.base;

import com.ragegamingpe.wtfenchants.common.WtfEnchants;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ModBlockVariants extends ModBlock
{
    protected IBaseVariant variant;

    public ModBlockVariants(Material material, MapColor color, String regName, IBaseVariant variant)
    {
        super(material, color, variant.getName() + "_" + regName);
        this.variant = variant;
    }

    public ModBlockVariants(Material materialIn, String regName, IBaseVariant variant)
    {
        this(materialIn, materialIn.getMaterialMapColor(), regName, variant);
    }

    public static <T extends Enum & IBaseVariant, B extends ModBlockVariants> Map<T, B> constructVariants(Class<B> clazz, Class<T> variants)
    {
        return constructVariants(clazz, null, null, variants);
    }

    public static <T extends Enum & IBaseVariant, B extends ModBlockVariants> Map<T, B> constructVariants(Class<B> clazz, Material material, String baseName, Class<T> variants)
    {
        T[] types = variants.getEnumConstants();

        Constructor<? extends ModBlockVariants> constructor = null;
        int parameters = 0;
        try {
            constructor = clazz.getConstructor(IBaseVariant.class);
            parameters = 1;
        } catch (NoSuchMethodException e) {
            try {
                constructor = clazz.getConstructor(Material.class, String.class, IBaseVariant.class);
                parameters = 3;
            } catch (NoSuchMethodException e1) {
                WtfEnchants.logger.error("No constructor found for " + clazz.getSimpleName() + "... Block not loaded.");
                return new HashMap<>();
            }
        }

        if (parameters == 3 && material == null)
            throw new RuntimeException("Material and base registry name must be passed.");

        Map<T, B> allBlocks = new HashMap<>();
        for (T type : types) {
            try {
                B block;
                if (parameters == 1) block = (B) constructor.newInstance(type);
                else block = (B) constructor.newInstance(material, baseName, type);

                allBlocks.put(type, block);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return allBlocks;
    }

    public interface IBaseVariant
    {
        public default String getName()
        {
            return ((Enum) this).name().toLowerCase();
        }
    }
}
