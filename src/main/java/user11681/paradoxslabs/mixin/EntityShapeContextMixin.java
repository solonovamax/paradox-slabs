package user11681.paradoxslabs.mixin;

import net.minecraft.block.EntityShapeContext;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import user11681.paradoxslabs.access.EntityShapeContextAccess;

@Pseudo
@Mixin(EntityShapeContext.class)
abstract class EntityShapeContextMixin implements EntityShapeContextAccess {
    @Unique
    private Entity entity;

    @Inject(method = "<init>(Lnet/minecraft/entity/Entity;)V", at = @At("RETURN"))
    protected void storeEntity(Entity entity, CallbackInfo ci) {
        this.entity = entity;
    }

    @Override
    public Entity getEntity() {
        return this.entity;
    }
}
