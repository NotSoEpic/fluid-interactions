package com.dindcrzy.fluid.criteria;

import com.dindcrzy.fluid.CustomConfig;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class TemperatureCriteria extends InteractionCriteria {
    public final Double min_temp;
    public final Double max_temp;
    
    public TemperatureCriteria(Double min_temp, Double max_temp) {
        this.min_temp = min_temp;
        this.max_temp = max_temp;
    }

    @Override
    public boolean test(FluidState flow, FluidState a_fluid, BlockState a_block, BlockPos pos, World world, Direction dir) {
        // computeTemperature() is private and also probably a bit hard to understand as a player
        double temp = world.getBiome(pos).value().getTemperature();
        if (min_temp != null && max_temp == null) { // only minimum defined
            return temp >= min_temp;
        }
        else if (min_temp == null && max_temp != null) { // only maximum defined
            return temp <= max_temp;
        }
        else if (min_temp != null && max_temp != null) {
            if (min_temp <= max_temp) { // inclusive band ( [-64] false [min_y] true [max_y] false [320] )
                return temp >= min_temp && temp <= max_temp;
            } else { // exclusive band ( [-64] true [max_y] false [min_y] true [320] )
                return temp > min_temp || temp < max_temp;
            }
        }
        
        return true; // this should never happen
    }

    public static InteractionCriteria readFrom(JsonObject json) {
        Integer min_temp = CustomConfig.getI(json, "min_temp");
        Integer max_temp = CustomConfig.getI(json, "max_temp");
        if (min_temp != null && max_temp != null) {
            return new HeightCriteria(min_temp, max_temp);
        }
        return null;
    }
}
