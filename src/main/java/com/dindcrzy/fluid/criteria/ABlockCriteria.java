package com.dindcrzy.fluid.criteria;

import com.dindcrzy.fluid.CustomConfig;
import com.dindcrzy.fluid.Helper;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ABlockCriteria extends InteractionCriteria {
    public final String block;
    
    public ABlockCriteria(String block) {
        this.block = block;
    }

    @Override
    public boolean test(FluidState flow, FluidState a_fluid, BlockState a_block, BlockPos pos, World world, Direction dir) {
        return this.block.equals(Helper.getId(a_block));
    }
    
    public static InteractionCriteria readFrom(JsonObject json) {
        String a_block = CustomConfig.getS(json, "adjacent_block");
        if (a_block != null) {
            return new ABlockCriteria(a_block);
        }
        return null;
    }
}
