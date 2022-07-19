package com.dindcrzy.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.registry.Registry;

public class Helper {
    public static String getId(Block block) {
        return Registry.BLOCK.getId(block).toString();
    }
    public static String getId(BlockState blockState) {
        return getId(blockState.getBlock());
    }
    
    public static String getId(Fluid fluid) {
        // minecraft:flowing_water is distinct from minecraft:water
        if (fluid instanceof FlowableFluid flowableFluid) {
            fluid = flowableFluid.getStill();
        }
        return Registry.FLUID.getId(fluid).toString();
    }
    public static String getId(FluidState fluid) {
        return getId(fluid.getFluid());
    }
}
