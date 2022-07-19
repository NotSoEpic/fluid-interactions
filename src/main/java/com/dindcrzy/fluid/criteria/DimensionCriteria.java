package com.dindcrzy.fluid.criteria;

import com.dindcrzy.fluid.CustomConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class DimensionCriteria extends InteractionCriteria {
    public final List<String> dimensions = new ArrayList<>();
    public final boolean blacklist;

    public DimensionCriteria(boolean blacklist) {
        this.blacklist = blacklist;
    }
    public DimensionCriteria add(String id) {
        dimensions.add(id);
        return this;
    }

    @Override
    public boolean test(FluidState flow, FluidState a_fluid, BlockState a_block, BlockPos pos, World world, Direction dir) {
        String id = world.getRegistryKey().getValue().toString();
        // XOR
        return dimensions.contains(id) ^ blacklist;
    }

    public static InteractionCriteria readFrom(JsonObject json) {
        JsonObject dimensions = CustomConfig.getJO(json, "dimensions");
        if (dimensions != null) {
            boolean blacklist = false;
            if (dimensions.has("blacklist")) {
                blacklist = dimensions.get("blacklist").getAsBoolean();
            }
            DimensionCriteria dimensionCriteria = new DimensionCriteria(blacklist);
            JsonArray dimensions_array = CustomConfig.getJA(dimensions, "dimensions");
            if (dimensions_array != null) {
                for (JsonElement je_dimension : dimensions_array) {
                    String dimension = je_dimension.getAsString();
                    if (dimension != null) {
                        dimensionCriteria.add(dimension);
                    }
                }
                return dimensionCriteria;
            }
        }
        return null;
    }
}
