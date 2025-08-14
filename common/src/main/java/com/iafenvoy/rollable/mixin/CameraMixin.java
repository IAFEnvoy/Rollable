package com.iafenvoy.rollable.mixin;

import com.iafenvoy.rollable.api.RollCamera;
import com.iafenvoy.rollable.api.RollEntity;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Camera.class)
public abstract class CameraMixin implements RollCamera {
    @Shadow
    private Entity focusedEntity;

    @Unique
    private boolean isRolling;
    @Unique
    private float lastRollBack;
    @Unique
    private float rollBack;
    @Unique
    private float roll;
    @Unique
    private final ThreadLocal<Float> tempRoll = new ThreadLocal<>();

    @Inject(method = "updateEyeHeight", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/Camera;cameraY:F", ordinal = 0))
    private void rollable$interpolateRollnt(CallbackInfo ci) {
        if (!((RollEntity) this.focusedEntity).rollable$isRolling()) {
            this.lastRollBack = this.rollBack;
            this.rollBack -= this.rollBack * 0.5f;
        }
    }

    @Inject(method = "update", at = @At("HEAD"))
    private void rollable$captureTickDeltaAndUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci, @Share("tickDelta") LocalFloatRef tickDeltaRef) {
        tickDeltaRef.set(tickDelta);
        this.isRolling = ((RollEntity) focusedEntity).rollable$isRolling();
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void rollable$updateRollBack(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (this.isRolling) {
            this.rollBack = this.roll;
            this.lastRollBack = this.roll;
        }
    }

    @WrapWithCondition(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 0))
    private boolean rollable$addRoll1(Camera thiz, float yaw, float pitch, @Share("tickDelta") LocalFloatRef tickDelta) {
        if (this.isRolling) {
            this.tempRoll.set(((RollEntity) this.focusedEntity).rollable$getRoll(tickDelta.get()));
        } else {
            this.tempRoll.set(MathHelper.lerp(tickDelta.get(), this.lastRollBack, this.rollBack));
        }
        return true;
    }

    @WrapWithCondition(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 1))
    private boolean rollable$addRoll2(Camera thiz, float yaw, float pitch) {
        this.tempRoll.set(-this.roll);
        return true;
    }

    @WrapWithCondition(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 2))
    private boolean rollable$addRoll3(Camera thiz, float yaw, float pitch) {
        this.tempRoll.set(0.0f);
        return true;
    }

    @ModifyArg(method = "setRotation", at = @At(value = "INVOKE", target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;"), index = 2)
    private float rollable$setRoll(float original) {
        Float roll = this.tempRoll.get();
        if (roll != null) {
            this.roll = roll;
            return (float) Math.toRadians(this.roll);
        }
        return original;
    }

    @Override
    public float rollable$getRoll() {
        return this.roll;
    }
}
