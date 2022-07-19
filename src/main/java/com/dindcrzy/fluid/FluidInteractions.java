package com.dindcrzy.fluid;

import com.dindcrzy.fluid.criteria.*;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class FluidInteractions implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("FluidInteractions");
	
	// TODO: this feels wrong
	public static List<InteractionType> interactions = generateInteractions();

	@Override
	public void onInitialize() {
		
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
	
	private static List<InteractionType> generateInteractions() {
		List<InteractionType> interactionTypes = new ArrayList<>();
		String rawJson = CustomConfig.of("fluid_interactions", cfg_fallback);
		JsonObject interactionJson = JsonParser.parseString(rawJson).getAsJsonObject();
		JsonArray jo_interactions = CustomConfig.getJA(interactionJson, "interactions");
		if (jo_interactions != null) {
			for(JsonElement je_interaction : jo_interactions) {
				JsonObject interaction = CustomConfig.toJO(je_interaction);
				if (interaction != null) {
					Double priority = CustomConfig.getD(interaction, "priority");
					InteractionType itype = new InteractionType(priority == null ? 0d : priority);
					
					// RESULTS
					JsonArray results = CustomConfig.getJA(interaction, "results");
					if (results != null) {
						for (JsonElement je_result : results) {
							JsonObject result = CustomConfig.toJO(je_result);
							if (result != null) {
								String block = CustomConfig.getS(result, "block");
								Double weight = CustomConfig.getD(result, "weight");
								if (block != null && weight != null) {
									itype.addResult(block, weight);
								}
							}
						}
					}

					// misc
					itype.setSilent(Boolean.TRUE.equals(CustomConfig.getB(interaction, "silent")));
					
					// CRITERIA
					// flowing fluid (lava of skyblock setup)
					itype.addCriteria(FlowCriteria.readFrom(interaction));
					// adjacent fluid (water of skyblock setup)
					itype.addCriteria(AFluidCriteria.readFrom(interaction));
					// adjacent block (blue ice of basalt generator)
					itype.addCriteria(ABlockCriteria.readFrom(interaction));
					// catalyst (soul soil of basalt generator)
					itype.addCriteria(CatalystCriteria.readFrom(interaction));
					// height
					itype.addCriteria(HeightCriteria.readFrom(interaction));
					// biome
					itype.addCriteria(BiomeCriteria.readFrom(interaction));
					// temperature
					itype.addCriteria(TemperatureCriteria.readFrom(interaction));
					// dimensions
					itype.addCriteria(DimensionCriteria.readFrom(interaction));
					
					LOGGER.info(itype.toString());
					
					interactionTypes.add(itype);
				}
			}
		}
		
		return interactionTypes;
	}
	
	private static final ImmutableList<Direction> FLOW_DIRECTIONS =
			ImmutableList.of(Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST);
	
	// returns success in replacing behaviour
	public static boolean flowInteract(World world, BlockPos pos, FluidState flow) {
		InteractionType queued = null;
		for (Direction dir : FLOW_DIRECTIONS) { // down and horizontal
			BlockPos blockPos = pos.offset(dir.getOpposite());
			FluidState a_fluid = world.getFluidState(blockPos);
			BlockState a_block = world.getBlockState(blockPos);
			for (InteractionType interaction : FluidInteractions.interactions) {
				if (interaction.test(flow, a_fluid, a_block, pos, world, dir.getOpposite())) {
					// an interaction hasn't been detected yet
					if (queued == null) {
						LOGGER.info(interaction.toString());
						queued = interaction;
					} // an interaction with a higher priority than the previously detected one
					else if (queued.priority < interaction.priority) {
						queued = interaction;
					}
				}
			}
		}
		if (queued != null) {
			BlockState block = queued.getResult(world.random);
			if (block != null) {
				world.setBlockState(pos, block);
				if (!queued.silent) {
					// playExtinguishSound()
					world.syncWorldEvent(1501, pos, 0);
				}
				return true;
			}
		}
		return false;
	}
	
	private static final String cfg_fallback = """
			{
			  "interactions": [
			    {
			      "priority": 0,
			      "flowing": "minecraft:lava",
			      "adjacent_fluid": "minecraft:water",
			      "results": [
			        {
			          "block": "minecraft:cobblestone",
			          "weight": 15
			        },
			        {
			          "block": "minecraft:coal_ore",
			          "weight": 7
			        },
			        {
			          "block": "minecraft:iron_ore",
			          "weight": 3
			        },
			        {
			          "block": "minecraft:lapis_ore",
			          "weight": 2
			        }
			      ]
			    },
			    {
			      "priority": 2,
			      "flowing": "minecraft:lava",
			      "adjacent_fluid": "minecraft:water",
			      "max_y": 0,
			      "results": [
			        {
			          "block": "minecraft:cobbled_deepslate",
			          "weight": 15
			        },
			        {
			          "block": "minecraft:deepslate_iron_ore",
			          "weight": 7
			        },
			        {
			          "block": "minecraft:deepslate_gold_ore",
			          "weight": 3
			        },
			        {
			          "block": "minecraft:deepslate_redstone_ore",
			          "weight": 5
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
			      ],
			      "dimensions": {
			        "blacklist": false,
			        "dimensions": [
			          "minecraft:the_nether"
			        ]
			      }
			    },
			    {
			      "priority": 1,
			      "flowing": "minecraft:lava",
			      "adjacent_fluid": "minecraft:water",
			      "results": [
			        {
			          "block": "minecraft:sandstone",
			          "weight": 1
			        }
			      ],
			      "biomes": {
			        "blacklist": false,
			        "biomes": [
			          "minecraft:beach",
			          "minecraft:snowy_beach",
			          "minecraft:desert"
			        ]
			      }
			    },
			    {
			      "flowing": "create:honey",
			      "adjacent_fluid": "create:chocolate",
			      "min_temp": 0.5,
			      "results": [
			        {
			          "block": "create:andesite_casing",
			          "weight": 4
			        },
			        {
			          "block": "create:brass_casing",
			          "weight": 1
			        }
			      ],
			      "silent": true
			    }
			  ]
			}""";
}
