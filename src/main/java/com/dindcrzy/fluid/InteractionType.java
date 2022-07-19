package com.dindcrzy.fluid;

import com.dindcrzy.fluid.criteria.*;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.*;

public class InteractionType {
    private final NavigableMap<Double, String> resultWeights = new TreeMap<>();
    private double total = 0;
    
    public final double priority;
    
    public final List<InteractionCriteria> criteria = new ArrayList<>();
    
    public boolean silent = false;
    
    InteractionType(double priority) {
        this.priority = priority; // required?
    }
    InteractionType(double priority, String flow, String a_fluid, String a_block, String catalyst) {
        this(priority);
        addCriteria(new FlowCriteria(flow));
        if (a_fluid != null) {addCriteria(new AFluidCriteria(a_fluid));}
        if (a_block != null) {addCriteria(new ABlockCriteria(a_block));}
        if (catalyst != null) {addCriteria(new CatalystCriteria(catalyst));}
    }

    public InteractionType setSilent(boolean b) {
        silent = b;
        return this;
    }
    
    public InteractionType addCriteria(InteractionCriteria criterion) {
        if (criterion != null) {
            this.criteria.add(criterion);
        }
        return this;
    }
    
    public InteractionType addResult(String block, Double weight) {
        if (weight <= 0) return this; // add(b, 0) messes with stuff
        total += weight;
        resultWeights.put(total, block);
        return this;
    }
    public BlockState getResult(Random random) {
        if (total > 0) {
            double value = random.nextDouble() * total;
            String id = resultWeights.higherEntry(value).getValue();
            return Registry.BLOCK.get(Identifier.tryParse(id)).getDefaultState();
        }
        return null;
    }

    public boolean test(FluidState flow, FluidState a_fluid, BlockState a_block, BlockPos pos, World world, Direction dir) {
        for(InteractionCriteria criterion : criteria) {
            if (!criterion.test(flow, a_fluid, a_block, pos, world, dir)) {
                return false;
            }
        }
        return !criteria.isEmpty();
    }

    @Override
    public String toString() {
        return "InteractionType{" +
                "resultWeights=" + resultWeights +
                ", total=" + total +
                ", priority=" + priority +
                ", criteria=" + criteria +
                ", silent=" + silent +
                '}';
    }
}
