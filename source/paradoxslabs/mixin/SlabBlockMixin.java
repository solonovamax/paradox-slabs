package paradoxslabs.mixin;


import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SlabBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paradoxslabs.ParadoxSlabs;


@Mixin(SlabBlock.class)
@SuppressWarnings({ "AbstractClassNeverImplemented", "AbstractClassWithoutAbstractMethods" })
abstract class SlabBlockMixin {
    @Shadow
    @Final
    @NotNull
    @SuppressWarnings("ConstantConditions")
    protected static VoxelShape TOP_SHAPE = null;
    
    @Shadow
    @Final
    @NotNull
    @SuppressWarnings("ConstantConditions")
    protected static VoxelShape BOTTOM_SHAPE = null;
    
    @Unique
    private static final VoxelShape east = VoxelShapes.cuboid(0.5, 0, 0, 1, 1, 1);
    
    @Unique
    private static final VoxelShape north = VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.5);
    
    @Unique
    private static final VoxelShape west = VoxelShapes.cuboid(0, 0, 0, 0.5, 1, 1);
    
    @Unique
    private static final VoxelShape south = VoxelShapes.cuboid(0, 0, 0.5, 1, 1, 1);
    
    @Inject(method = "getOutlineShape",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/util/shape/VoxelShapes;fullCube()Lnet/minecraft/util/shape/VoxelShape;"), cancellable = true)
    public void fixOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context,
                                CallbackInfoReturnable<VoxelShape> info) {
        if (ParadoxSlabs.hasAxis()) {
            switch (state.get(Properties.AXIS)) {
                case X -> {
                    var x = ParadoxSlabs.raycastX(context, world, pos, state);
                    
                    if (x >= 0.5) {
                        info.setReturnValue(east);
                    } else if (x >= 0) {
                        info.setReturnValue(west);
                    }
                    
                    return;
                }
                case Z -> {
                    var z = ParadoxSlabs.raycastZ(context, world, pos, state);
                    
                    if (z >= 0.5) {
                        info.setReturnValue(south);
                    } else if (z >= 0) {
                        info.setReturnValue(north);
                    }
                    
                    return;
                }
            }
        }
        
        var y = ParadoxSlabs.raycastY(context, world, pos, state);
        
        if (y >= 0.5) {
            info.setReturnValue(TOP_SHAPE);
        } else if (y >= 0) {
            info.setReturnValue(BOTTOM_SHAPE);
        }
    }
}
