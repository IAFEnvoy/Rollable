package com.iafenvoy.rollable.mixin;

import com.iafenvoy.rollable.api.RollCamera;
import com.iafenvoy.rollable.api.RollEntity;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
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
    private boolean rollable$isRolling;
    @Unique
    private float rollable$lastRollBack;
    @Unique
    private float rollable$rollBack;
    @Unique
    private float rollable$roll;
    @Unique
    private final ThreadLocal<Float> rollable$tempRoll = new ThreadLocal<>();

    @Inject(method = "updateEyeHeight", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/Camera;cameraY:F", ordinal = 0))
    private void rollable$interpolateRollnt(CallbackInfo ci) {
        if (!((RollEntity) this.focusedEntity).rollable$isRolling()) {
            this.rollable$lastRollBack = this.rollable$rollBack;
            this.rollable$rollBack -= this.rollable$rollBack * 0.5f;
        }
    }

    @Inject(method = "update", at = @At("HEAD"))
    private void rollable$captureTickDeltaAndUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci, @Share("tickDelta") LocalFloatRef tickDeltaRef) {
        tickDeltaRef.set(tickDelta);
        this.rollable$isRolling = ((RollEntity) focusedEntity).rollable$isRolling();
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void rollable$updateRollBack(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (this.rollable$isRolling) {
            this.rollable$rollBack = this.rollable$roll;
            this.rollable$lastRollBack = this.rollable$roll;
        }
    }

    @WrapWithCondition(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 0))
    private boolean rollable$addRoll1(Camera thiz, float yaw, float pitch, @Share("tickDelta") LocalFloatRef tickDelta) {
        if (this.rollable$isRolling)
            this.rollable$tempRoll.set(((RollEntity) this.focusedEntity).rollable$getRoll(tickDelta.get()));
        else
            this.rollable$tempRoll.set(MathHelper.lerp(tickDelta.get(), this.rollable$lastRollBack, this.rollable$rollBack));
        return true;
    }

    @WrapWithCondition(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 1))
    private boolean rollable$addRoll2(Camera thiz, float yaw, float pitch) {
        this.rollable$tempRoll.set(-this.rollable$roll);
        return true;
    }

    @WrapWithCondition(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 2))
    private boolean rollable$addRoll3(Camera thiz, float yaw, float pitch) {
        this.rollable$tempRoll.set(0.0f);
        return true;
    }

    @ModifyArg(method = "setRotation", at = @At(value = "INVOKE", target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;", remap = false), index = 2)
    private float rollable$setRoll(float original) {
        Float roll = this.rollable$tempRoll.get();
        if (roll != null) {
            this.rollable$roll = roll;
            return (float) Math.toRadians(this.rollable$roll);
        }
        return original;
    }

    @Override
    public float rollable$getRoll() {
        return this.rollable$roll;
    }
}
