package net.auoeke.paradoxslabs.mixin;

import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import net.auoeke.paradoxslabs.ParadoxSlabs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
abstract class WorldRendererMixin {
    @Shadow
    private ClientWorld world;

    @Shadow
    @Final
    private MinecraftClient client;

    @Unique
    private final Long2ReferenceOpenHashMap<BlockState> slabStates = new Long2ReferenceOpenHashMap<>();

    @Inject(method = "removeBlockBreakingInfo",
            at = @At(value = "INVOKE",
                     target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;remove(J)Ljava/lang/Object;"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void removeSlabState(BlockBreakingInfo blockBreakingInfo, CallbackInfo info, long key) {
        this.slabStates.remove(key);
    }

    @Inject(method = "setBlockBreakingInfo",
            at = @At(value = "INVOKE",
                     target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;computeIfAbsent(JLjava/util/function/LongFunction;)Ljava/lang/Object;"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void removeSlabState(int entityId, BlockPos pos, int stage, CallbackInfo info, BlockBreakingInfo blockBreakingInfo) {
        BlockState state = this.world.getBlockState(pos);

        if (ParadoxSlabs.hasAxis()) {
            switch (state.get(Properties.AXIS)) {
                case X:
                    this.slabStates.put(blockBreakingInfo.getPos().asLong(), ParadoxSlabs.xStates(this.world, pos, state, this.client.player).getLeft());

                    return;
                case Z:
                    this.slabStates.put(blockBreakingInfo.getPos().asLong(), ParadoxSlabs.zStates(this.world, pos, state, this.client.player).getLeft());

                    return;
            }
        }

        this.slabStates.put(blockBreakingInfo.getPos().asLong(), ParadoxSlabs.yStates(this.world, pos, state, this.client.player).getLeft());
    }

    @Redirect(method = "render",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/world/ClientWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
                       ordinal = 0))
    public BlockState renderSlab(ClientWorld world, BlockPos pos) {
        return this.slabStates.get(pos.asLong());
    }
}
