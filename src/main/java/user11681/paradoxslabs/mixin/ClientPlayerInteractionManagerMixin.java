package user11681.paradoxslabs.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import user11681.paradoxslabs.ParadoxSlabs;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
abstract class ClientPlayerInteractionManagerMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Unique
    private BlockState newState;

    @ModifyVariable(method = "breakBlock",
                    at = @At(value = "INVOKE",
                             target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V"))
    public BlockState fixState(BlockState block, BlockPos pos) {
        Pair<BlockState, BlockState> states = ParadoxSlabs.getStates(this.client.world, pos, block, this.client.player);

        this.newState = states.getRight();

        return states.getLeft();
    }

    @Redirect(method = "breakBlock",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    public boolean removeSlab(World world, BlockPos pos, BlockState state, int flags) {
        if (this.newState != null) {
            world.setBlockState(pos, this.newState);

            return true;
        }

        return world.setBlockState(pos, state, flags);
    }
}
