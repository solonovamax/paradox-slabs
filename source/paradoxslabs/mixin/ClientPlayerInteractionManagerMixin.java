package paradoxslabs.mixin;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import paradoxslabs.ParadoxSlabs;


@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
@SuppressWarnings({ "AbstractClassNeverImplemented", "AbstractClassWithoutAbstractMethods" })
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
        if (ParadoxSlabs.hasAxis()) {
            switch (block.get(Properties.AXIS)) {
                case X -> {
                    var xStates = ParadoxSlabs.xStates(this.client.world, pos, block, this.client.player);
                    this.newState = xStates.getRight();
                    
                    return xStates.getLeft();
                }
                case Z -> {
                    var zStates = ParadoxSlabs.zStates(this.client.world, pos, block, this.client.player);
                    this.newState = zStates.getRight();
                    
                    return zStates.getLeft();
                }
            }
        }
        
        var yStates = ParadoxSlabs.yStates(this.client.world, pos, block, this.client.player);
        this.newState = yStates.getRight();
        
        return yStates.getLeft();
    }
    
    @Redirect(method = "breakBlock",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    public boolean removeSlab(World world, BlockPos pos, BlockState state, int flags) {
        if (this.newState == null) {
            return world.setBlockState(pos, state, flags);
        }
        
        world.setBlockState(pos, this.newState);
        
        return true;
    }
}
