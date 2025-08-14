package com.iafenvoy.rollable.mixin.compat;

import com.iafenvoy.rollable.api.RollEntity;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(targets = "mirsario.cameraoverhaul.common.systems.CameraSystem")
public abstract class CameraSystemMixin {
    private boolean allowModifications() {
        return !(MinecraftClient.getInstance().getCameraEntity() instanceof RollEntity rollEntity && rollEntity.rollable$isRolling());
    }

    @Dynamic
    @ModifyArg(
            method = "OnCameraUpdate(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/Camera;Lmirsario/cameraoverhaul/core/structures/Transform;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lmirsario/cameraoverhaul/common/systems/CameraSystem;VerticalVelocityPitchOffset(Lmirsario/cameraoverhaul/core/structures/Transform;Lmirsario/cameraoverhaul/core/structures/Transform;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec2f;DFF)V"
            ),
            index = 5
    )
    private float rollable$cancelVerticalVelocityPitchOffset(float original) {
        return this.allowModifications() ? original : 0f;
    }

    @Dynamic
    @ModifyArg(
            method = "OnCameraUpdate(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/Camera;Lmirsario/cameraoverhaul/core/structures/Transform;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lmirsario/cameraoverhaul/common/systems/CameraSystem;ForwardVelocityPitchOffset(Lmirsario/cameraoverhaul/core/structures/Transform;Lmirsario/cameraoverhaul/core/structures/Transform;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec2f;DFF)V"
            ),
            index = 5
    )
    private float rollable$cancelForwardVelocityPitchOffset(float original) {
        return this.allowModifications() ? original : 0f;
    }

    @Dynamic
    @ModifyArg(
            method = "OnCameraUpdate(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/Camera;Lmirsario/cameraoverhaul/core/structures/Transform;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lmirsario/cameraoverhaul/common/systems/CameraSystem;YawDeltaRollOffset(Lmirsario/cameraoverhaul/core/structures/Transform;Lmirsario/cameraoverhaul/core/structures/Transform;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec2f;DFFF)V"
            ),
            index = 5
    )
    private float rollable$cancelYawDeltaRollOffset(float original) {
        return this.allowModifications() ? original : 0f;
    }

    @Dynamic
    @ModifyArg(
            method = "OnCameraUpdate(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/Camera;Lmirsario/cameraoverhaul/core/structures/Transform;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lmirsario/cameraoverhaul/common/systems/CameraSystem;StrafingRollOffset(Lmirsario/cameraoverhaul/core/structures/Transform;Lmirsario/cameraoverhaul/core/structures/Transform;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec2f;DFF)V"
            ),
            index = 5
    )
    private float rollable$cancelStrafingRollOffset(float original) {
        return this.allowModifications() ? original : 0f;
    }
}
