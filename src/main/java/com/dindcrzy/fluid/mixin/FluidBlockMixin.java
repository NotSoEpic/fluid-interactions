package com.dindcrzy.fluid.mixin;

import com.dindcrzy.fluid.FluidInteractions;
import com.dindcrzy.fluid.InteractionType;
import com.google.common.collect.ImmutableList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidBlock.class)
public abstract class FluidBlockMixin {
	@Shadow @Final public static ImmutableList<Direction> FLOW_DIRECTIONS;

	@Shadow @Final protected FlowableFluid fluid;

	@Shadow protected abstract void playExtinguishSound(WorldAccess world, BlockPos pos);

	@Inject(method = "receiveNeighborFluids", at = @At("HEAD"), cancellable = true)
	private void replaceInteractionBehaviour(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		Block down = world.getBlockState(pos.down()).getBlock(); // gets blockstate below event position
		InteractionType queued = null;
		for (Direction dir : FLOW_DIRECTIONS) { // down and horizontal
			BlockPos blockPos = pos.offset(dir.getOpposite());
			for (InteractionType interaction : FluidInteractions.interactions) {
				if (interaction.test(this.fluid, world.getFluidState(blockPos).getFluid(), world.getBlockState(blockPos).getBlock(), down)) {
					// an interaction hasn't been detected yet
					if (queued == null) {
						queued = interaction;
					} // an interaction with a higher priority (uses catalyst) than the previously detected one
					else if (queued.catalyst == null && interaction.catalyst != null) {
						queued = interaction;
					}
				}
			}
		}
		if (queued != null) {
			BlockState block = queued.next(world.random);
			if (block != null) {
				//world.setBlockState(pos, Blocks.IRON_ORE.getDefaultState());
				world.setBlockState(pos, block);
				if (!queued.silent) {
					playExtinguishSound(world, pos);
				}
				cir.setReturnValue(false);
				cir.cancel();
			} else {
				FluidInteractions.LOGGER.warn(
					"interaction " +
					queued +
					" next() returned null");
			}
		}
		//cir.setReturnValue(true);
		//cir.cancel();
	}
}
