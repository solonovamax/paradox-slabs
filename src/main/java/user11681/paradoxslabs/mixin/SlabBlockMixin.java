package user11681.paradoxslabs.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SlabBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import user11681.paradoxslabs.ParadoxSlabs;

@Mixin(SlabBlock.class)
abstract class SlabBlockMixin {
    @Shadow
    @Final
    protected static VoxelShape TOP_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape BOTTOM_SHAPE;

    @Inject(method = "getOutlineShape",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/util/shape/VoxelShapes;fullCube()Lnet/minecraft/util/shape/VoxelShape;"), cancellable = true)
    public void fixOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> info) {
        double y = ParadoxSlabs.raycast(context, world, pos, state);

        if (y >= 0.5) {
            info.setReturnValue(TOP_SHAPE);
        } else if (y >= 0) {
            info.setReturnValue(BOTTOM_SHAPE);
        }
    }
}
