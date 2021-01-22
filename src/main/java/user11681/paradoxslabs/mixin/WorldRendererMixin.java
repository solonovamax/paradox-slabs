package user11681.paradoxslabs.mixin;

import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
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
import user11681.paradoxslabs.ParadoxSlabs;

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
        this.slabStates.put(blockBreakingInfo.getPos().asLong(), ParadoxSlabs.getStates(this.world, pos, this.world.getBlockState(pos), this.client.player).getLeft());
    }

    @Redirect(method = "render",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/world/ClientWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
                       ordinal = 0))
    public BlockState renderSlab(ClientWorld world, BlockPos pos) {
        return this.slabStates.get(pos.asLong());
    }
}
