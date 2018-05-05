package com.ragegamingpe.wtfenchants.client.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ragegamingpe.wtfenchants.common.block.BlockBookshelf;
import com.ragegamingpe.wtfenchants.common.block.base.ModBlock;
import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import net.minecraft.block.properties.IProperty;

import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ModelCreator
{
    public static final Map<String, Class<? extends ModBlock>> blocks = new HashMap<>();

    public static void main(String[] args) throws Exception
    {
        registerBlock("bookshelf", BlockBookshelf.class);

        for (Map.Entry<String, Class<? extends ModBlock>> block : blocks.entrySet()) {
            String regName = block.getKey();
            Class<? extends ModBlock> clazzBlock = block.getValue();

            try {
                IProperty[] properties = (IProperty[]) clazzBlock.getMethod("getAllProperties").invoke(clazzBlock);

                Class[] parameters = new Class[properties.length];
                int[] size = new int[properties.length];
                Collection[] values = new Collection[properties.length];
                StringBuilder stateString = new StringBuilder();
                for (int i = 0; i < parameters.length; i++) {
                    parameters[i] = properties[i].getValueClass();
                    size[i] = properties[i].getAllowedValues().size() - 1;
                    values[i] = properties[i].getAllowedValues();
                    stateString.append(",").append(properties[i].getName()).append("=%s");
                }

                Method generateModelMethod = clazzBlock.getMethod("generateModel", parameters);
                if (generateModelMethod.getReturnType() != JsonObject.class)
                    throw new Exception("Return type MUST be JsonObject");
                JsonObject variants = new JsonObject();

                while (size[0] >= 0) {
                    Object[] objParameters = new Object[parameters.length];
                    for (int i = 0; i < objParameters.length; i++) {
                        objParameters[i] = values[i].toArray()[size[i]];
                    }

                    variants.add(String.format(stateString.substring(1), getString(objParameters)), (JsonElement) generateModelMethod.invoke(clazzBlock, objParameters));

                    int currentIndex = parameters.length - 1;
                    size[currentIndex]--;

                    while (currentIndex > 0 && size[currentIndex] < 0) {
                        size[currentIndex] = properties[currentIndex].getAllowedValues().size() - 1;
                        currentIndex--;

                        if (currentIndex >= 0) size[currentIndex]--;
                    }
                }

                JsonObject blockstates = new JsonObject();
                blockstates.add("variants", variants);

                FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/src/main/resources/assets/" + LibMisc.MOD_ID + "/blockstates/" + regName + ".json");
                writer.write(blockstates.toString());
                writer.close();
                System.out.println("Created blockstates for " + regName);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private static String[] getString(Object[] objParameters)
    {
        String[] strings = new String[objParameters.length];

        for (int i = 0; i < objParameters.length; i++) {
            strings[i] = String.valueOf(objParameters[i]);
        }

        return strings;
    }

    private static void registerBlock(String name, Class<? extends ModBlock> block)
    {
        blocks.put(name, block);
    }
}
