package com.iafenvoy.rollable.mixin.roll;

import com.iafenvoy.rollable.util.RollEntity;
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
    public void rollable$changeElytraLook(double pitch, double yaw, double roll, Sensitivity sensitivity, double mouseDelta) {
    }

    @Override
    public void rollable$changeElytraLook(float pitch, float yaw, float roll) {
    }

    @Override
    public boolean rollable$isRolling() {
        return false;
    }

    @Override
    public void rollable$setRolling(boolean rolling) {
    }

    @Override
    public float rollable$getRoll() {
        return 0;
    }

    @Override
    public float rollable$getRoll(float tickDelta) {
        return 0;
    }

    @Override
    public void rollable$setRoll(float roll) {
    }
}
