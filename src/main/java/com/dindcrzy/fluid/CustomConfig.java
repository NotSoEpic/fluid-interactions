package com.dindcrzy.fluid;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class CustomConfig {
    public static final Logger LOGGER = LogManager.getLogger("FluidInteractions - config");
    
    public static String of(String filename, String fallback) {
        Path path = FabricLoader.getInstance().getConfigDir();
        File file = path.resolve(filename + ".json").toFile();
        StringBuilder s = new StringBuilder();
        Scanner reader;
        if (!file.exists()) {
            LOGGER.info("config " + filename + " doesn't exist, attempting to create");
            try {
                if (file.createNewFile()) {
                    FileWriter writer = new FileWriter(file);
                    writer.write(fallback);
                    writer.close();
                    return fallback;
                }
            } catch (IOException e) {
                //e.printStackTrace();
                LOGGER.error(e);
                return "";
            }
        }
        try {
            reader = new Scanner(file);
            while (reader.hasNextLine()) {
                s.append(reader.nextLine());
            }
            return s.toString();
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            LOGGER.error(e);
            return "";
        }
    }
    
    // expect member of type, complain if it doesn't exist
    public static JsonObject toJO(JsonElement element) {
        if (element.isJsonObject()) {
            return element.getAsJsonObject();
        } else {
            LOGGER.warn("A JsonElement wasn't a JsonObject (uses { })");
        }
        return null;
    }
    public static JsonObject getJO(JsonObject root, String key) {
        if (root.has(key)) {
            JsonElement element = root.get(key);
            if (element.isJsonObject()) {
                return element.getAsJsonObject();
            } else {
                LOGGER.warn("Element " + key + " of " + root + " is not a JsonObject (uses { })");
            }
        } else {
            //LOGGER.warn("(JO)" + root + " has no member " + key);
        }
        return null;
    }
    public static JsonArray getJA(JsonObject root, String key) {
        if (root.has(key)) {
            JsonElement element = root.get(key);
            if (element.isJsonArray()) {
                return element.getAsJsonArray();
            } else {
                LOGGER.warn("Element " + key + " of " + root + " is not a JsonArray (uses [ ])");
            }
        } else {
            //LOGGER.warn("(JA)" + root + " has no member " + key);
        }
        return null;
    }
    public static Boolean getB(JsonObject root, String key) { // lol
        if (root.has(key)) {
            JsonElement element = root.get(key);
            return element.getAsBoolean();
        } else {
            //LOGGER.warn("(B)" + root + " has no member " + key);
        }
        return null;
    }
    public static Integer getI(JsonObject root, String key) { // lol
        if (root.has(key)) {
            JsonElement element = root.get(key);
            try {
                return element.getAsInt();
            } catch (NumberFormatException e) {
                LOGGER.warn("Element " + key + " of JsonObject is not an integer (whole number)");
            }
        } else {
            //LOGGER.warn("(I)" + root + " has no member " + key);
        }
        return null;
    }
    public static Double getD(JsonObject root, String key) { // lol
        if (root.has(key)) {
            JsonElement element = root.get(key);
            try {
                return element.getAsDouble();
            } catch (NumberFormatException e) {
                LOGGER.warn("Element " + key + " of " + root + " is not a double (decimal number)");
            }
        } else {
            //LOGGER.warn("(D)" + root + " has no member " + key);
        }
        return null;
    }
    public static String getS(JsonObject root, String key) {
        if (root.has(key)) {
            JsonElement element = root.get(key);
            return element.getAsString();
        } else {
            //LOGGER.warn("(S)" + root + " has no member " + key);
        }
        return null;
    }
    
    public static Block tryGetBlock(JsonObject root, String key) {
        String id = getS(root, key);
        if (id != null) {
            Identifier identifier = Identifier.tryParse(id);
            Block block = Registry.BLOCK.get(identifier);
            if (block != Blocks.AIR) { // air is the default fallback for a wrong id
                return block;
            }
            LOGGER.warn("Cannot find block " + id);
        }
        return null;
    }
    
    public static List<Block> tryGetBlocks(JsonObject root, String key) {
        String id = getS(root, key);
        if (id != null) {
            // TODO: make this work
            // hypothesis: this is all done before any tagkeys are actually made
            if (id.startsWith("#")) {
                // #minecraft:logs -> minecraft:logs
                TagKey<Block> blockTag = TagKey.of(Registry.BLOCK_KEY, Identifier.tryParse(id.substring(1)));
                Optional<RegistryEntryList.Named<Block>> oEntryList = Registry.BLOCK.getEntryList(blockTag);
                if (oEntryList.isPresent()) {
                    RegistryEntryList.Named<Block> entryList = oEntryList.get();
                    List<Block> blocks = new ArrayList<>();
                    for (RegistryEntry<Block> block : entryList) {
                        blocks.add(block.value());
                    }
                    return blocks;
                }
                LOGGER.warn("Cannot find block tag " + id);
            } else {
                return Collections.singletonList(tryGetBlock(root, key));
            }
        }
        return null;
    }

    public static Fluid tryGetFluid(JsonObject root, String key) {
        String id = getS(root, key);
        if (id != null) {
            Identifier identifier = Identifier.tryParse(id);
            Fluid fluid = Registry.FLUID.get(identifier);
            if (fluid != Fluids.EMPTY) {
                return fluid;
            }
        }
        return null;
    }
}
