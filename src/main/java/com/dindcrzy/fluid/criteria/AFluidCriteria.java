package com.dindcrzy.fluid.criteria;

import com.dindcrzy.fluid.CustomConfig;
import com.dindcrzy.fluid.Helper;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class AFluidCriteria extends InteractionCriteria {
    public final String fluid;
    
    public AFluidCriteria(String fluid) {
        this.fluid = fluid;
    }

    @Override
    public boolean test(FluidState flow, FluidState a_fluid, BlockState a_block, BlockPos pos, World world, Direction dir) {
        return this.fluid.equals(Helper.getId(a_fluid));
    }
    
    public static InteractionCriteria readFrom(JsonObject json) {
        String a_fluid = CustomConfig.getS(json, "adjacent_fluid");
        if (a_fluid != null) {
            return new AFluidCriteria(a_fluid);
        }
        return null;
    }
}
