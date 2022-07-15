package com.dindcrzy.fluid;

import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;

public class ShittyCustomConfig {
    // a logger specifically for the config. goes to show my confidence
    private static final Logger LOGGER = LogManager.getLogger("FluidInteractions - config");
    
    private final HashMap<String, JsonObject> configs = new HashMap<>();
    
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
}
