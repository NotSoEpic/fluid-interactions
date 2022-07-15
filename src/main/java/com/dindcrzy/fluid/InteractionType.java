package com.dindcrzy.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class InteractionType {
    private final NavigableMap<Double, BlockState> weights = new TreeMap<>();
    private double total = 0;
    
    public final Fluid flow;
    public final Fluid adjacent_fluid;
    public final Block adjacent_block;
    public final Block catalyst;
    
    public boolean silent = false;
    
    InteractionType(Fluid flow, Fluid adjacent, @Nullable Block catalyst) {
        this.flow = flow;
        this.adjacent_fluid = adjacent;
        this.adjacent_block = null;
        this.catalyst = catalyst;
    }

    InteractionType(Fluid flow, Block adjacent, @Nullable Block catalyst) {
        this.flow = flow;
        this.adjacent_fluid = null;
        this.adjacent_block = adjacent;
        this.catalyst = catalyst;
    }

    public boolean test(Fluid flow, Fluid adjacent_fluid, Block adjacent_block, @Nullable Block catalyst) {
        return flow.matchesType(this.flow) &&
                (adjacent_fluid.matchesType(this.adjacent_fluid) ||  adjacent_block == this.adjacent_block) &&
                (this.catalyst == null || this.catalyst == catalyst);
    }
    
    public boolean test(Fluid flow, Fluid adjacent_fluid, @Nullable Block catalyst) {
        return test(flow, adjacent_fluid, null, catalyst);
    }
    
    public boolean test(Fluid flow, Block adjacent_block, @Nullable Block catalyst) {
        return test(flow, null, adjacent_block, catalyst);
    }
    
    public InteractionType add(BlockState block, Double weight) {
        if (weight <= 0) return this; // add(b, 0) messes with stuff
        total += weight;
        weights.put(total, block);
        return this;
    }
    
    public InteractionType add(Block block, Double weight) {
        return add(block.getDefaultState(), weight);
    }

    public InteractionType setSilent(boolean b) {
        silent = b;
        return this;
    }
    
    public BlockState next(Random random) {
        double value = random.nextDouble() * total;
        return weights.higherEntry(value).getValue();
    }

    @Override
    public String toString() {
        return "InteractionType{" +
                ", total=" + total +
                ", flow=" + flow +
                ", adjacent_fluid=" + adjacent_fluid +
                ", adjacent_block=" + adjacent_block +
                ", catalyst=" + catalyst +
                ", silent=" + silent +
                '}';
    }
}
