package com.iafenvoy.rollable.mixin.roll.entity;

import com.iafenvoy.rollable.api.RollEntity;
import com.iafenvoy.rollable.config.Sensitivity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityMixin implements RollEntity {
    @Shadow
    public abstract float getPitch();

    @Shadow
    public abstract float getYaw();

    @Shadow
    public abstract void setPitch(float pitch);

    @Shadow
    public abstract void setYaw(float yaw);

    @Shadow
    public abstract void changeLookDirection(double cursorDeltaX, double cursorDeltaY);

    @Shadow
    public abstract Vec3d getRotationVecClient();

    @Override
    public void doABarrelRoll$changeElytraLook(double pitch, double yaw, double roll, Sensitivity sensitivity, double mouseDelta) {
    }

    @Override
    public void doABarrelRoll$changeElytraLook(float pitch, float yaw, float roll) {
    }

    @Override
    public boolean doABarrelRoll$isRolling() {
        return false;
    }

    @Override
    public void doABarrelRoll$setRolling(boolean rolling) {
    }

    @Override
    public float doABarrelRoll$getRoll() {
        return 0;
    }

    @Override
    public float doABarrelRoll$getRoll(float tickDelta) {
        return 0;
    }

    @Override
    public void doABarrelRoll$setRoll(float roll) {
    }
}
