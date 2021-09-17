package net.auoeke.paradoxslabs.mixin;

import net.auoeke.paradoxslabs.access.EntityShapeContextAccess;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(EntityShapeContext.class)
abstract class EntityShapeContextMixin implements EntityShapeContextAccess {
    @Unique
    private Entity entity;

    @Inject(method = "<init>(Lnet/minecraft/entity/Entity;)V", at = @At("RETURN"))
    protected void storeEntity(Entity entity, CallbackInfo info) {
        this.entity = entity;
    }

    @Override
    public Entity paradoxslabs_getEntity() {
        return this.entity;
    }
}
