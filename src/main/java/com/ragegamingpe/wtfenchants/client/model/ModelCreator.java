package com.ragegamingpe.wtfenchants.client.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ragegamingpe.wtfenchants.common.block.BlockBookshelf;
import com.ragegamingpe.wtfenchants.common.block.BlockDisenchantment;
import com.ragegamingpe.wtfenchants.common.block.BlockSorter;
import com.ragegamingpe.wtfenchants.common.block.base.ModBlock;
import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import net.minecraft.block.properties.IProperty;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Add getAllProperties, generateBlockState, generateBlockModel and register block to create models
 * Item models coming soon
 */
public class ModelCreator
{
    private static final Map<String, Class<? extends ModBlock>> BLOCKS = new HashMap<>();
    private static final Map<String, Boolean> CREATED_MODELS = new HashMap<>();

    public static void main(String[] args) throws Exception
    {
        registerBlock("bookshelf", BlockBookshelf.class);
        registerBlock("sorter", BlockSorter.class);
        registerBlock("disenchanting_table", BlockDisenchantment.class);

        for (Map.Entry<String, Class<? extends ModBlock>> block : BLOCKS.entrySet()) {
            if (createBlockInformation(block.getKey())) {
                generateBlockStates(block);
                CREATED_MODELS.put(block.getKey(), true);
            } else {
                System.out.println("Skipping " + block.getKey());
            }
        }

        saveCreateModels();
    }

    private static void generateBlockStates(Map.Entry<String, Class<? extends ModBlock>> block) throws Exception
    {
        String regName = block.getKey();
        Class<? extends ModBlock> clazzBlock = block.getValue();

        try { // Global Try-Catch (Allows me for custom stuff for NoSuchMethodExceptions)
            IProperty[] properties;
            try {
                properties = (IProperty[]) clazzBlock.getMethod("getAllProperties").invoke(clazzBlock);
            } catch (NoSuchMethodException e) {
                properties = new IProperty[0];
            }

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

            JsonObject variants = new JsonObject();
            Method generateBlockState;
            try {
                generateBlockState = clazzBlock.getMethod("generateBlockState", parameters);
            } catch (NoSuchMethodException e) {
                generateBlockState = null;
            }

            if (generateBlockState != null) {
                if (generateBlockState.getReturnType() != JsonObject.class)
                    throw new Exception("Return type MUST be JsonObject");

                List<String> modelsGenerated = new ArrayList<>();
                while (size[0] >= 0) {
                    Object[] objParameters = new Object[parameters.length];
                    for (int i = 0; i < objParameters.length; i++) {
                        objParameters[i] = values[i].toArray()[size[i]];
                    }

                    JsonObject jsonObject = (JsonObject) generateBlockState.invoke(clazzBlock, objParameters);
                    if (!jsonObject.has("model"))
                        throw new Exception("generateBlockState must return a model within the JSON object.");

                    variants.add(String.format(stateString.substring(1), getString(objParameters)), jsonObject);

                    if (!modelsGenerated.contains(jsonObject.get("model").toString())) {
                        createBlockModel(clazzBlock, regName, parameters, objParameters);
                        modelsGenerated.add(jsonObject.get("model").toString());
                    }

                    int currentIndex = parameters.length - 1;
                    size[currentIndex]--;

                    while (currentIndex > 0 && size[currentIndex] < 0) {
                        size[currentIndex] = properties[currentIndex].getAllowedValues().size() - 1;
                        currentIndex--;

                        if (currentIndex >= 0) size[currentIndex]--;
                    }
                }
            } else {
                System.out.println("Generating default block state for " + regName);
                JsonObject object = new JsonObject();
                object.addProperty("model", LibMisc.MOD_ID + ":" + regName);
                variants.add("normal", object);

                createBlockModel(clazzBlock, regName);
            }

            JsonObject blockstates = new JsonObject();
            blockstates.add("variants", variants);

            FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/src/main/resources/assets/" + LibMisc.MOD_ID + "/blockstates/" + regName + ".json");
            writer.write(blockstates.toString());
            writer.close();
            System.out.println("Created blockstates for " + regName);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static void createBlockModel(Class<? extends ModBlock> block, String regName, Class[] parameters, Object[] objParameters) throws Exception
    {
        String file = "";
        String fileName = regName;
        try {
            Method generateBlockModel = block.getMethod("generateBlockModel", parameters);
            Object modelInformation = generateBlockModel.invoke(block, objParameters); // String|JsonObject|Pair<String, String|JsonObject>

            if (generateBlockModel.getReturnType() == String.class) {
                file = (String) modelInformation;
            } else if (generateBlockModel.getReturnType() == JsonObject.class) {
                file = ((JsonObject) modelInformation).toString();
            } else if (generateBlockModel.getReturnType() == Pair.class) { // Pair<String, String|JsonObject>
                Pair info = (Pair) modelInformation;
                if (!(info.getLeft() instanceof String)) {
                    throw new Exception("Left side of Pair MUST be a String of the file name");
                }

                fileName = (String) info.getLeft();
                if (info.getRight() instanceof String) {
                    file = (String) info.getRight();
                } else if (info.getRight() instanceof JsonObject) {
                    file = ((JsonObject) info.getRight()).toString();
                } else {
                    throw new Exception("Right side of Pair MUST be a String or JsonObject");
                }
            } else {
                throw new Exception("generateBlockModel method MUST return either String or JsonObject");
            }

        } catch (NoSuchMethodException e) {
            System.out.println("Creating default model for " + fileName);
            file = "{\n" +
                    "    \"parent\": \"block/cube_all\",\n" +
                    "    \"textures\": {\n" +
                    "        \"all\": \"" + LibMisc.MOD_ID + ":blocks/" + regName + "\"\n" +
                    "    }\n" +
                    "}\n";
        }

        FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/src/main/resources/assets/" + LibMisc.MOD_ID + "/models/block/" + fileName + ".json");
        writer.write(file);
        writer.close();
        System.out.println("Created model " + fileName + ".json");
    }

    private static void createBlockModel(Class<? extends ModBlock> block, String regName) throws Exception
    {
        createBlockModel(block, regName, new Class[0], new Object[0]);
    }

    private static String[] getString(Object[] objParameters)
    {
        String[] strings = new String[objParameters.length];

        for (int i = 0; i < objParameters.length; i++) {
            strings[i] = String.valueOf(objParameters[i]);
        }

        return strings;
    }

    private static boolean createBlockInformation(String key)
    {
        if (CREATED_MODELS.isEmpty()) {
            File file = new File(System.getProperty("user.dir") + "/created_models.json");
            if (file.exists() && file.canRead()) {
                try {
                    FileReader reader = new FileReader(file);
                    JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonObject();
                    reader.close();

                    for (Map.Entry<String, JsonElement> map : jsonObject.entrySet()) {
                        CREATED_MODELS.put(map.getKey(), map.getValue().getAsBoolean());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return !CREATED_MODELS.getOrDefault(key, false);
    }

    private static void saveCreateModels()
    {
        try {
            FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/created_models.json");
            JsonObject jsonObject = new JsonObject();
            for (Map.Entry<String, Boolean> entry : CREATED_MODELS.entrySet()) {
                jsonObject.addProperty(entry.getKey(), entry.getValue());
            }
            writer.write(jsonObject.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void registerBlock(String name, Class<? extends ModBlock> block)
    {
        BLOCKS.put(name, block);
    }
}
