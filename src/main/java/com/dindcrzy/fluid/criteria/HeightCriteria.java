package com.dindcrzy.fluid.criteria;

import com.dindcrzy.fluid.CustomConfig;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HeightCriteria extends InteractionCriteria {
    public final Integer min_y;
    public final Integer max_y;
    
    public HeightCriteria(Integer min_y, Integer max_y) {
        this.min_y = min_y;
        this.max_y = max_y;
    }

    @Override
    public boolean test(FluidState flow, FluidState a_fluid, BlockState a_block, BlockPos pos, World world, Direction dir) {
        int y = pos.getY();
        if (min_y != null && max_y == null) { // only minimum defined
            return y >= min_y;
        }
        else if (min_y == null && max_y != null) { // only maximum defined
            return y <= max_y;
        }
        else if (min_y != null && max_y != null) {
            if (min_y <= max_y) { // inclusive band ( [-64] false [min_y] true [max_y] false [320] )
                return y >= min_y && y <= max_y;
            } else { // exclusive band ( [-64] true [max_y] false [min_y] true [320] )
                return y > min_y || y < max_y;
            }
        }
        
        return true; // this should never happen
    }

    public static InteractionCriteria readFrom(JsonObject json) {
        Integer min_y = CustomConfig.getI(json, "min_y");
        Integer max_y = CustomConfig.getI(json, "max_y");
        if (min_y != null || max_y != null) {
            return new HeightCriteria(min_y, max_y);
        }
        return null;
    }
}
