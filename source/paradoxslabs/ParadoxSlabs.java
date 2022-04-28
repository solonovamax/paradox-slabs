package paradoxslabs;

import paradoxslabs.access.EntityShapeContextAccess;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.Entity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class ParadoxSlabs {
    private static boolean checkedAxis;
    private static boolean hasAxis;

    /** For Better Slabs compatibility. */
    public static boolean hasAxis() {
        if (!checkedAxis) {
            checkedAxis = true;
            hasAxis = Blocks.ACACIA_SLAB.getDefaultState().contains(Properties.AXIS);
        }

        return hasAxis;
    }

    public static BlockHitResult raycast(ShapeContext context, BlockView world, BlockPos pos, BlockState state) {
        if (context instanceof EntityShapeContextAccess entityContext) {
            var entity = entityContext.paradoxslabs_getEntity();

            if (entity != null) {
                var position = entity.getPos();
                position = new Vec3d(position.x, entity.getEyeY(), position.z);

                return world.raycastBlock(position, position.add(entity.getRotationVector().multiply(20)), pos, VoxelShapes.fullCube(), state);
            }
        }

        return null;
    }

    public static double raycastX(ShapeContext context, BlockView world, BlockPos pos, BlockState state) {
        return raycast(Direction.Axis.X, context, world, pos, state);
    }

    public static double raycastY(ShapeContext context, BlockView world, BlockPos pos, BlockState state) {
        return raycast(Direction.Axis.Y, context, world, pos, state);
    }

    public static double raycastZ(ShapeContext context, BlockView world, BlockPos pos, BlockState state) {
        return raycast(Direction.Axis.Z, context, world, pos, state);
    }

    public static Pair<BlockState, BlockState> xStates(BlockView world, BlockPos pos, BlockState block, Entity entity) {
        return horizontalStates(Direction.Axis.X, world, pos, block, entity);
    }

    public static Pair<BlockState, BlockState> yStates(BlockView world, BlockPos pos, BlockState block, Entity entity) {
        if (block.getBlock() instanceof SlabBlock && block.get(SlabBlock.TYPE) == SlabType.DOUBLE) {
            var y = ParadoxSlabs.raycastY(ShapeContext.of(entity), world, pos, block);

            if (y >= 0.5) {
                return hasAxis()
                    ? new Pair<>(block.with(SlabBlock.TYPE, SlabType.TOP).with(Properties.AXIS, Direction.Axis.Y), block.with(SlabBlock.TYPE, SlabType.BOTTOM).with(Properties.AXIS, Direction.Axis.Y))
                    : new Pair<>(block.with(SlabBlock.TYPE, SlabType.TOP), block.with(SlabBlock.TYPE, SlabType.BOTTOM));
            }

            if (y >= 0) {
                return hasAxis()
                    ? new Pair<>(block.with(SlabBlock.TYPE, SlabType.BOTTOM).with(Properties.AXIS, Direction.Axis.Y), block.with(SlabBlock.TYPE, SlabType.TOP).with(Properties.AXIS, Direction.Axis.Y))
                    : new Pair<>(block.with(SlabBlock.TYPE, SlabType.BOTTOM), block.with(SlabBlock.TYPE, SlabType.TOP));
            }
        }

        return new Pair<>(block, null);
    }

    public static Pair<BlockState, BlockState> zStates(BlockView world, BlockPos pos, BlockState block, Entity entity) {
        return horizontalStates(Direction.Axis.Z, world, pos, block, entity);
    }

    private static double raycast(Direction.Axis axis, ShapeContext context, BlockView world, BlockPos pos, BlockState state) {
        var raycast = raycast(context, world, pos, state);
        return raycast == null ? -1 : raycast.getPos().getComponentAlongAxis(axis) - pos.getComponentAlongAxis(axis);
    }

    private static Pair<BlockState, BlockState> horizontalStates(Direction.Axis axis, BlockView world, BlockPos pos, BlockState block, Entity entity) {
        if (block.getBlock() instanceof SlabBlock && block.get(SlabBlock.TYPE) == SlabType.DOUBLE) {
            var coordinate = axis == Direction.Axis.X ? raycastX(ShapeContext.of(entity), world, pos, block) : raycastZ(ShapeContext.of(entity), world, pos, block);

            if (coordinate >= 0.5) {
                return new Pair<>(block.with(SlabBlock.TYPE, SlabType.TOP).with(Properties.AXIS, axis), block.with(SlabBlock.TYPE, SlabType.BOTTOM).with(Properties.AXIS, axis));
            }

            if (coordinate >= 0) {
                return new Pair<>(block.with(SlabBlock.TYPE, SlabType.BOTTOM).with(Properties.AXIS, axis), block.with(SlabBlock.TYPE, SlabType.TOP).with(Properties.AXIS, axis));
            }
        }

        return new Pair<>(block, null);
    }
}
