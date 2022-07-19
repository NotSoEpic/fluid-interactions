package com.dindcrzy.fluid.mixin;

import com.dindcrzy.fluid.FluidInteractions;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidBlock.class)
public class FluidBlockMixin {
    @Shadow @Final protected FlowableFluid fluid;

    @Inject(method = "receiveNeighborFluids", at = @At("HEAD"), cancellable = true)
    public void customInteract(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        boolean success = FluidInteractions.flowInteract(world, pos, this.fluid.getDefaultState());
        if (success) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
