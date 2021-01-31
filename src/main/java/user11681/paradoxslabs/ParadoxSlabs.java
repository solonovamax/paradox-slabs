package user11681.paradoxslabs;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.Entity;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import user11681.paradoxslabs.access.EntityShapeContextAccess;

public class ParadoxSlabs {
    public static double raycast(ShapeContext context, BlockView world, BlockPos pos, BlockState state) {
        if (context instanceof EntityShapeContextAccess) {
            Entity entity = ((EntityShapeContextAccess) context).paradoxslabs_getEntity();

            if (entity != null) {
                Vec3d position = entity.getPos();
                position = new Vec3d(position.x, entity.getEyeY(), position.z);

                BlockHitResult raycast = world.raycastBlock(position, position.add(entity.getRotationVector().multiply(20)), pos, VoxelShapes.fullCube(), state);

                if (raycast != null) {
                    return raycast.getPos().y - pos.getY();
                }
            }
        }

        return -1;
    }

    public static Pair<BlockState, BlockState> getStates(BlockView world, BlockPos pos, BlockState block, Entity entity) {
        if (block.getBlock() instanceof SlabBlock && block.get(SlabBlock.TYPE) == SlabType.DOUBLE) {
            double y = ParadoxSlabs.raycast(ShapeContext.of(entity), world, pos, block);

            if (y >= 0.5) {
                return new Pair<>(block.with(SlabBlock.TYPE, SlabType.TOP), block.with(SlabBlock.TYPE, SlabType.BOTTOM));
            }

            if (y >= 0) {
                return new Pair<>(block.with(SlabBlock.TYPE, SlabType.BOTTOM), block.with(SlabBlock.TYPE, SlabType.TOP));
            }
        }

        return new Pair<>(block, null);
    }
}
