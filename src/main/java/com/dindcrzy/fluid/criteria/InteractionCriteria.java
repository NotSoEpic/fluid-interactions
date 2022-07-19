package com.dindcrzy.fluid.criteria;

import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

// base class for testing whether an interaction should go through
/*
To add a new criteria:
Create new class extending this one
Add in relevant test and readFrom code
Add readFrom call to FluidInteractions.generateInteractions()
 */
public class InteractionCriteria {
    public InteractionCriteria() {
        
    }
    
    public boolean test(FluidState flow, FluidState a_fluid, BlockState a_block, BlockPos pos, World world, Direction dir) {
        return true;
    }
    
    public static InteractionCriteria readFrom(JsonObject json) {
        return new InteractionCriteria();
    }
}
