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
            Entity entity = entityContext.paradoxslabs_getEntity();

            if (entity != null) {
                Vec3d position = entity.getPos();
                position = new Vec3d(position.x, entity.getEyeY(), position.z);

                return world.raycastBlock(position, position.add(entity.getRotationVector().multiply(20)), pos, VoxelShapes.fullCube(), state);
            }
        }

        return null;
    }

    public static double raycastX(ShapeContext context, BlockView world, BlockPos pos, BlockState state) {
        BlockHitResult raycast = raycast(context, world, pos, state);

        return raycast == null ? -1 : raycast.getPos().x - pos.getX();

    }

    public static double raycastY(ShapeContext context, BlockView world, BlockPos pos, BlockState state) {
        BlockHitResult raycast = raycast(context, world, pos, state);

        return raycast == null ? -1 : raycast.getPos().y - pos.getY();

    }

    public static double raycastZ(ShapeContext context, BlockView world, BlockPos pos, BlockState state) {
        BlockHitResult raycast = raycast(context, world, pos, state);

        return raycast == null ? -1 : raycast.getPos().z - pos.getZ();

    }

    public static Pair<BlockState, BlockState> xStates(BlockView world, BlockPos pos, BlockState block, Entity entity) {
        if (block.getBlock() instanceof SlabBlock && block.get(SlabBlock.TYPE) == SlabType.DOUBLE) {
            double x = ParadoxSlabs.raycastX(ShapeContext.of(entity), world, pos, block);

            if (x >= 0.5) {
                return new Pair<>(block.with(SlabBlock.TYPE, SlabType.TOP).with(Properties.AXIS, Direction.Axis.X), block.with(SlabBlock.TYPE, SlabType.BOTTOM).with(Properties.AXIS, Direction.Axis.X));
            }

            if (x >= 0) {
                return new Pair<>(block.with(SlabBlock.TYPE, SlabType.BOTTOM).with(Properties.AXIS, Direction.Axis.X), block.with(SlabBlock.TYPE, SlabType.TOP).with(Properties.AXIS, Direction.Axis.X));
            }
        }

        return new Pair<>(block, null);
    }

    public static Pair<BlockState, BlockState> yStates(BlockView world, BlockPos pos, BlockState block, Entity entity) {
        if (block.getBlock() instanceof SlabBlock && block.get(SlabBlock.TYPE) == SlabType.DOUBLE) {
            double y = ParadoxSlabs.raycastY(ShapeContext.of(entity), world, pos, block);

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
        if (block.getBlock() instanceof SlabBlock && block.get(SlabBlock.TYPE) == SlabType.DOUBLE) {
            double z = ParadoxSlabs.raycastZ(ShapeContext.of(entity), world, pos, block);

            if (z >= 0.5) {
                return new Pair<>(block.with(SlabBlock.TYPE, SlabType.TOP).with(Properties.AXIS, Direction.Axis.Z), block.with(SlabBlock.TYPE, SlabType.BOTTOM).with(Properties.AXIS, Direction.Axis.Z));
            }

            if (z >= 0) {
                return new Pair<>(block.with(SlabBlock.TYPE, SlabType.BOTTOM).with(Properties.AXIS, Direction.Axis.Z), block.with(SlabBlock.TYPE, SlabType.TOP).with(Properties.AXIS, Direction.Axis.Z));
            }
        }

        return new Pair<>(block, null);
    }
}
