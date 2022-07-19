package com.dindcrzy.fluid.criteria;

import com.dindcrzy.fluid.CustomConfig;
import com.dindcrzy.fluid.Helper;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FlowCriteria extends InteractionCriteria {
    public final String fluid;
    
    public FlowCriteria(String fluid) {
        this.fluid = fluid;
    }

    @Override
    public boolean test(FluidState flow, FluidState a_fluid, BlockState a_block, BlockPos pos, World world, Direction dir) {
        return this.fluid.equals(Helper.getId(flow));
    }
    
    public static InteractionCriteria readFrom(JsonObject json) {
        String flowing = CustomConfig.getS(json, "flowing");
        if (flowing != null) {
            return new FlowCriteria(flowing);
        }
        return null;
    }
}
