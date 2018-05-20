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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Add getAllProperties, generateBlockState, generateBlockModel, generateItemModel and register block to create models
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

            IProperty[] ignoredProperties;
            try {
                ignoredProperties = (IProperty[]) clazzBlock.getMethod("getIgnoredProperties").invoke(clazzBlock);
            } catch (NoSuchMethodException e) {
                ignoredProperties = new IProperty[0];
            }

            Class[] parameters = new Class[properties.length];
            int[] size = new int[properties.length];
            Collection[] values = new Collection[properties.length];
            StringBuilder stateString = new StringBuilder();
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = properties[i].getValueClass();
                size[i] = properties[i].getAllowedValues().size() - 1;
                values[i] = properties[i].getAllowedValues();
                if (!Arrays.asList(ignoredProperties).contains(properties[i])) {
                    stateString.append(",").append(properties[i].getName()).append("=%s");
                }
            }

            Method generateBlockState;
            try {
                generateBlockState = clazzBlock.getMethod("generateBlockState", parameters);
            } catch (NoSuchMethodException e) {
                generateBlockState = null;
            }

//            JsonObject variants = new JsonObject();
            Map<String, JsonObject> blockstates = new HashMap<>();
            if (generateBlockState != null) {
                if (generateBlockState.getReturnType() != JsonObject.class && generateBlockState.getReturnType() != Pair.class)
                    throw new Exception("Return type MUST be JsonObject");

                List<String> blockModelsGenerated = new ArrayList<>();
                List<String> itemModelsGenerated = new ArrayList<>();
                while (size[0] >= 0) {
                    Object[] objParameters = new Object[parameters.length];
                    for (int i = 0; i < objParameters.length; i++) {
                        objParameters[i] = values[i].toArray()[size[i]];
                    }

                    String fileName;
                    JsonObject variant;
                    if (generateBlockState.getReturnType() == Pair.class) {
                        Pair<String, JsonObject> pair = (Pair) generateBlockState.invoke(clazzBlock, objParameters);
                        fileName = pair.getLeft();
                        variant = pair.getRight();
                    } else {
                        fileName = regName;
                        variant = (JsonObject) generateBlockState.invoke(clazzBlock, objParameters);
                    }
                    if (!variant.has("model"))
                        throw new Exception("generateBlockState must return a model within the JSON object.");

                    JsonObject variants = blockstates.getOrDefault(fileName, new JsonObject());
                    variants.add(String.format(stateString.substring(1), getStrings(objParameters, ignoredProperties)), variant);
                    blockstates.put(fileName, variants);

                    createBlockModel(clazzBlock, regName, parameters, objParameters, blockModelsGenerated);
                    createItemModel(clazzBlock, regName, parameters, objParameters, itemModelsGenerated);

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
                blockstates.put(regName, generateDefaultBlockState(regName));
                createBlockModel(clazzBlock, regName);
                createItemModel(clazzBlock, regName);
            }

            for (Map.Entry<String, JsonObject> blockstate : blockstates.entrySet()) {
                JsonObject object = new JsonObject();
                object.add("variants", blockstate.getValue());

                FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/src/main/resources/assets/" + LibMisc.MOD_ID + "/blockstates/" + blockstate.getKey() + ".json");
                writer.write(object.toString());
                writer.close();
                System.out.println("Created blockstates for " + blockstate.getKey());
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static void createBlockModel(Class<? extends ModBlock> block, String regName, Class[] parameters, Object[] objParameters, List<String> blockModelsGenerated) throws Exception
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
            System.out.println("Creating default block model for " + fileName);
            file = generateDefaultBlockModel(regName).toString();
        }

        if (blockModelsGenerated.contains(fileName)) return;

        String[] split;
        if ((split = fileName.split("/")).length > 1) {
            split[split.length - 1] = "";
            Files.createDirectories(Paths.get(System.getProperty("user.dir") + "/src/main/resources/assets/" + LibMisc.MOD_ID + "/models/block/" + String.join("/", split)));
        }
        FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/src/main/resources/assets/" + LibMisc.MOD_ID + "/models/block/" + fileName + ".json");
        writer.write(file);
        writer.close();

        blockModelsGenerated.add(fileName);
        System.out.println("Created block model " + fileName + ".json");
    }

    private static void createBlockModel(Class<? extends ModBlock> block, String regName) throws Exception
    {
        createBlockModel(block, regName, new Class[0], new Object[0], new ArrayList<>());
    }

    private static void createItemModel(Class<? extends ModBlock> block, String regName, Class[] parameters, Object[] objParameters, List<String> itemModelsGenerated) throws Exception
    {
        String file = "";
        String fileName = regName;
        try {
            Method generateBlockModel = block.getMethod("generateItemModel", parameters);
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
                throw new Exception("generateItemModel method MUST return either String or JsonObject");
            }

        } catch (NoSuchMethodException e) {
            System.out.println("Creating default item model for " + fileName);
            file = generateDefaultItemModel(regName).toString();
        }

        if (itemModelsGenerated.contains(fileName)) return;

        String[] split;
        if ((split = fileName.split("/")).length > 1) {
            split[split.length - 1] = "";
            Files.createDirectories(Paths.get(System.getProperty("user.dir") + "/src/main/resources/assets/" + LibMisc.MOD_ID + "/models/item/" + String.join("/", split)));
        }
        FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/src/main/resources/assets/" + LibMisc.MOD_ID + "/models/item/" + fileName + ".json");
        writer.write(file);
        writer.close();

        itemModelsGenerated.add(fileName);
        System.out.println("Created item model " + fileName + ".json");
    }

    private static void createItemModel(Class<? extends ModBlock> block, String regName) throws Exception
    {
        createItemModel(block, regName, new Class[0], new Object[0], new ArrayList<>());
    }

    private static String[] getStrings(Object[] objParameters, IProperty[] ignoredProperties)
    {
        List<String> strings = new ArrayList<>();

        for (int i = 0; i < objParameters.length; i++) {
            boolean flag = false;
            for (int j = 0; j < ignoredProperties.length; j++) {
                if (objParameters[i].getClass() == ignoredProperties[j].getValueClass())
                    flag = true;
            }

            if (!flag)
                strings.add(String.valueOf(objParameters[i]));
        }

        return strings.toArray(new String[0]);
    }

    public static JsonObject generateDefaultBlockState(String regName)
    {
        JsonObject object = new JsonObject();
        object.addProperty("model", LibMisc.MOD_ID + ":" + regName);
        JsonObject variants = new JsonObject();
        variants.add("normal", object);

        return variants;
    }

    public static JsonObject generateDefaultBlockModel(String regName)
    {
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse("{" +
                "    \"parent\": \"block/cube_all\"," +
                "    \"textures\": {" +
                "        \"all\": \"" + LibMisc.MOD_ID + ":blocks/" + regName + "\"" +
                "    }" +
                "}").getAsJsonObject();
        return object;
    }

    public static JsonObject generateDefaultItemModel(String regName)
    {
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse("{" +
                "  \"parent\": \"" + LibMisc.MOD_ID + ":block/" + regName + "\"," +
                "  \"display\": {" +
                "    \"gui\": {" +
                "      \"rotation\": [30, 225, 0]," +
                "      \"translation\": [0, 0, 0]," +
                "      \"scale\": [0.625, 0.625, 0.625]" +
                "    }," +
                "    \"ground\": {" +
                "      \"rotation\": [0, 0, 0]," +
                "      \"translation\": [0, 3, 0]," +
                "      \"scale\": [0.25, 0.25, 0.25]" +
                "    }," +
                "    \"fixed\": {" +
                "      \"rotation\": [0, 0, 0]," +
                "      \"translation\": [0, 0, 0]," +
                "      \"scale\": [0.5, 0.5, 0.5]" +
                "    }," +
                "    \"thirdperson_righthand\": {" +
                "      \"rotation\": [75, 45, 0]," +
                "      \"translation\": [0, 2.5, 0]," +
                "      \"scale\": [0.375, 0.375, 0.375]" +
                "    }," +
                "    \"firstperson_righthand\": {" +
                "      \"rotation\": [0, 45, 0]," +
                "      \"translation\": [0, 0, 0]," +
                "      \"scale\": [0.40, 0.40, 0.40]" +
                "    }," +
                "    \"firstperson_lefthand\": {" +
                "      \"rotation\": [0, 225, 0]," +
                "      \"translation\": [0, 0, 0]," +
                "      \"scale\": [0.40, 0.40, 0.40]" +
                "    }" +
                "  }" +
                "}").getAsJsonObject();

        return object;
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
