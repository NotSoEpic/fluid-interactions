package com.dindcrzy.fluid.criteria;

import com.dindcrzy.fluid.CustomConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BiomeCriteria extends InteractionCriteria {
    public final List<String> biomes = new ArrayList<>();
    public final boolean blacklist;
    
    public BiomeCriteria(boolean blacklist) {
        this.blacklist = blacklist;
    }
    public BiomeCriteria add(String id) {
        biomes.add(id);
        return this;
    }
    
    @Override
    public boolean test(FluidState flow, FluidState a_fluid, BlockState a_block, BlockPos pos, World world, Direction dir) {
        Optional<RegistryKey<Biome>> biome_optional = world.getBiome(pos).getKey();
        if (biome_optional.isPresent()) {
            String id = biome_optional.get().getValue().toString();
            if (id.startsWith("#")) {
                // TODO: make this work
                TagKey<Biome> biomeTag = TagKey.of(Registry.BIOME_KEY, Identifier.tryParse(id.substring(1)));
                return world.getBiome(pos).isIn(biomeTag) ^ blacklist; // XOR
            } else {
                return biomes.contains(id) ^ blacklist;
            }
        }
        return false;
    }
    
    public static InteractionCriteria readFrom(JsonObject json) {
        JsonObject biomes = CustomConfig.getJO(json, "biomes");
        if (biomes != null) {
            boolean blacklist = false;
            if (biomes.has("blacklist")) {
                blacklist = biomes.get("blacklist").getAsBoolean();
            }
            BiomeCriteria biomeCriteria = new BiomeCriteria(blacklist);
            JsonArray biomes_array = CustomConfig.getJA(biomes, "biomes");
            if (biomes_array != null) {
                for (JsonElement je_biome : biomes_array) {
                    String biome = je_biome.getAsString();
                    if (biome != null) {
                        biomeCriteria.add(biome);
                    }
                }
                return biomeCriteria;
            }
        }
        return null;
    }
}
