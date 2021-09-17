package user11681.paradoxslabs.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import user11681.paradoxslabs.ParadoxSlabs;

@Mixin(ServerPlayerInteractionManager.class)
abstract class ServerPlayerInteractionManagerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Shadow
    public ServerWorld world;

    @Unique
    private BlockState newState;

    @ModifyVariable(method = "tryBreakBlock",
                    at = @At(value = "INVOKE",
                             target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V"))
    public BlockState fixState(BlockState block, BlockPos pos) {
        if (ParadoxSlabs.hasAxis()) {
            switch (block.get(Properties.AXIS)) {
                case X:
                    Pair<BlockState, BlockState> xStates = ParadoxSlabs.xStates(this.world, pos, block, this.player);

                    this.newState = xStates.getRight();

                    return xStates.getLeft();
                case Z:
                    Pair<BlockState, BlockState> zStates = ParadoxSlabs.zStates(this.world, pos, block, this.player);

                    this.newState = zStates.getRight();

                    return zStates.getLeft();
            }
        }

        Pair<BlockState, BlockState> yStates = ParadoxSlabs.yStates(this.world, pos, block, this.player);

        this.newState = yStates.getRight();

        return yStates.getLeft();
    }

    @Redirect(method = "tryBreakBlock",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/server/world/ServerWorld;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
    public boolean removeSlab(ServerWorld world, BlockPos pos, boolean move) {
        if (this.newState != null) {
            world.setBlockState(pos, this.newState);

            return true;
        }

        return world.removeBlock(pos, move);
    }
}
