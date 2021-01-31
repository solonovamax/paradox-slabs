package user11681.paradoxslabs.mixin.compatibility;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;
import user11681.paradoxslabs.access.EntityShapeContextAccess;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.lithium.common.block.LithiumEntityShapeContext")
abstract class LithiumEntityShapeContextMixin implements EntityShapeContextAccess {
    @Accessor("entity")
    public abstract Entity getEntity();
}
