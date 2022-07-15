package com.dindcrzy.fluid;

import com.google.gson.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.lwjgl.system.CallbackI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FluidInteractions implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("FluidInteractions");
	
	/*public static List<InteractionType> interactions = Arrays.asList(
		new InteractionType(Fluids.LAVA, Fluids.WATER, null)
			.add(Blocks.IRON_ORE, 10d)
			.add(Blocks.GOLD_ORE, 5d)
			.add(Blocks.DIAMOND_ORE, 1d),
		new InteractionType(Fluids.LAVA, Fluids.WATER, Blocks.DEEPSLATE)
			.add(Blocks.DEEPSLATE_IRON_ORE, 10d)
			.add(Blocks.DEEPSLATE_GOLD_ORE, 5d)
			.add(Blocks.DEEPSLATE_DIAMOND_ORE, 1d),
		new InteractionType(Fluids.LAVA, Blocks.SOUL_SOIL, Blocks.BLUE_ICE)
			.add(Blocks.BASALT, 5d)
			.add(Blocks.BLACKSTONE, 4d)
			.add(Blocks.GILDED_BLACKSTONE, 1d),
		new InteractionType(Fluids.WATER, Blocks.BLUE_ICE, Blocks.BLUE_ICE)
			.add(Blocks.ICE, 1d)
			.setSilent(true)
	);*/
	
	// this is 1000% a terrible practice
	public static final List<InteractionType> interactions = generateConstants();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		
		//generateConstants();
	}
	
	public static Fluid getFluid(String id) {
		// returns a default value if not specified, which I don't want
		return Registry.FLUID.get(Identifier.tryParse(id));
	}

	public static Block getBlock(String id) {
		// returns a default value if not specified, which I don't want
		return Registry.BLOCK.get(Identifier.tryParse(id));
	}
	
	// horrible code ahead - beware
	private static String tryString(JsonObject o, String key) {
		if (o.has(key)) {
			return o.get(key).getAsString();
		}
		return "";
	}
	
	private static double tryDouble(JsonObject o, String key) {
		if (o.has(key)) {
			return o.get(key).getAsDouble();
		}
		return 0;
	}
	
	private static List<InteractionType> generateConstants() {
		List<InteractionType> interactionTypes = new ArrayList<>();
		String rawJson = ShittyCustomConfig.of("fluid_interactions", cfg_fallback);
		JsonObject interactionJson = JsonParser.parseString(rawJson).getAsJsonObject();
		JsonArray interactions = interactionJson.getAsJsonArray("interactions");
		for (JsonElement _interaction : interactions) {
			JsonObject interaction = _interaction.getAsJsonObject();
			Fluid flowing = getFluid(tryString(interaction, "flowing"));
			Fluid adjacent_fluid = getFluid(tryString(interaction, "adjacent_fluid"));
			Block adjacent_block = getBlock(tryString(interaction, "adjacent_block"));
			Block catalyst = getBlock(tryString(interaction, "catalyst"));
			InteractionType interactionType;
			if (interaction.has("adjacent_fluid")) {
				interactionType = new InteractionType(flowing, adjacent_fluid, catalyst);
			} else if (interaction.has("adjacent_block")) {
				interactionType = new InteractionType(flowing, adjacent_block, catalyst);
			} else {
				LOGGER.warn("No adjacent block or adjacent fluid specified");
				break;
			}
			for (JsonElement _result : interaction.getAsJsonArray("results")) {
				JsonObject result = _result.getAsJsonObject();
				Block result_block = getBlock(result.get("block").getAsString());
				double weight = tryDouble(result, "weight");
				if (weight > 0) {
					interactionType.add(result_block, weight);
				} else {
					LOGGER.warn("invalid weight");
				}
			}
			interactionTypes.add(interactionType);
		}
		return interactionTypes;
	}
	
	private static final String cfg_fallback = """
			{
			  "interactions": [
			    {
			      "flowing": "minecraft:lava",
			      "adjacent_fluid": "minecraft:water",
			      "catalyst": "minecraft:deepslate",
			      "results": [
			        {
			          "block": "minecraft:deepslate_iron_ore",
			          "weight": 14
			        },
			        {
			          "block": "minecraft:deepslate_gold_ore",
			          "weight": 5
			        },
			        {
			          "block": "minecraft:deepslate_redstone_ore",
			          "weight": 10
			        },
			        {
			          "block": "minecraft:deepslate_diamond_ore",
			          "weight": 1
			        }
			      ]
			    },
			    {
			      "flowing": "minecraft:lava",
			      "adjacent_block": "minecraft:blue_ice",
			      "catalyst": "minecraft:soul_soil",
			      "results": [
			        {
			          "block": "minecraft:basalt",
			          "weight": 5
			        },
			        {
			          "block": "minecraft:blackstone",
			          "weight": 4
			        },
			        {
			          "block": "minecraft:gilded_blackstone",
			          "weight": 1
			        }
			      ]
			    }
			  ]
			}""";
}
