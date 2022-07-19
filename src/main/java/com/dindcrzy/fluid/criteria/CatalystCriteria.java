package com.dindcrzy.fluid.criteria;

import com.dindcrzy.fluid.CustomConfig;
import com.dindcrzy.fluid.Helper;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CatalystCriteria extends InteractionCriteria {
    public final String block;
    
    public CatalystCriteria(String block) {
        this.block = block;
    }

    @Override
    public boolean test(FluidState flow, FluidState a_fluid, BlockState a_block, BlockPos pos, World world, Direction dir) {
        Block catalyst = world.getBlockState(pos.down()).getBlock();
        return this.block.equals(Helper.getId(catalyst));
    }
    
    public static InteractionCriteria readFrom(JsonObject json) {
        String catalyst = CustomConfig.getS(json, "catalyst");
        if (catalyst != null) {
            return new CatalystCriteria(catalyst);
        }
        return null;
    }
}
