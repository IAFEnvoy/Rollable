package com.iafenvoy.rollable.mixin.client.roll.entity;

import com.iafenvoy.rollable.api.event.RollContext;
import com.iafenvoy.rollable.api.event.RollEvents;
import com.iafenvoy.rollable.api.rotation.RotationInstant;
import com.iafenvoy.rollable.config.Sensitivity;
import com.iafenvoy.rollable.flight.RotationModifiers;
import com.iafenvoy.rollable.math.MagicNumbers;
import com.iafenvoy.rollable.mixin.roll.entity.PlayerEntityMixin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntityMixin {
    @Shadow
    public float renderYaw;
    @Shadow
    public float lastRenderYaw;

    @Unique
    private boolean lastSentIsRolling;
    @Unique
    private float lastSentRoll;

    @Inject(
            method = "sendMovementPackets",
            at = @At("TAIL")
    )
    private void doABarrelRoll$sendRollPacket(CallbackInfo ci) {
        boolean isRolling = this.doABarrelRoll$isRolling();
        float rollDiff = this.doABarrelRoll$getRoll() - this.lastSentRoll;
        if (isRolling != this.lastSentIsRolling || rollDiff != 0.0f) {
            this.lastSentIsRolling = isRolling;
            this.lastSentRoll = this.doABarrelRoll$getRoll();
        }
    }

    @Override
    @Unique
    protected void doABarrelRoll$baseTickTail2() {
        // Update rolling status
        this.doABarrelRoll$setRolling(RollEvents.shouldRoll());
    }

    @Override
    public void doABarrelRoll$changeElytraLook(double pitch, double yaw, double roll, Sensitivity sensitivity, double mouseDelta) {
        RotationInstant rotDelta = RotationInstant.of(pitch, yaw, roll);
        float currentRoll = this.doABarrelRoll$getRoll();
        RotationInstant currentRotation = RotationInstant.of(
                this.getPitch(),
                this.getYaw(),
                currentRoll
        );
        RollContext context = RollContext.of(currentRotation, rotDelta, mouseDelta);

        context.useModifier(RotationModifiers.fixNaN("INPUT"));
        RollEvents.earlyCameraModifiers(context);
        context.useModifier(RotationModifiers.fixNaN("EARLY_CAMERA_MODIFIERS"));
        context.useModifier((rotation, ctx) -> rotation.applySensitivity(sensitivity));
        context.useModifier(RotationModifiers.fixNaN("SENSITIVITY"));
        RollEvents.lateCameraModifiers(context);
        context.useModifier(RotationModifiers.fixNaN("LATE_CAMERA_MODIFIERS"));

        rotDelta = context.getRotationDelta();

        this.doABarrelRoll$changeElytraLook((float) rotDelta.pitch(), (float) rotDelta.yaw(), (float) rotDelta.roll());
    }

    @Override
    public void doABarrelRoll$changeElytraLook(float pitch, float yaw, float roll) {
        float currentPitch = this.getPitch();
        float currentYaw = this.getYaw();
        float currentRoll = this.doABarrelRoll$getRoll();

        // Convert pitch, yaw, and roll to a facing and left vector
        Vector3d facing = new Vector3d(this.getRotationVecClient().toVector3f());
        Vector3d left = new Vector3d(1, 0, 0);
        left.rotateZ(-currentRoll * MagicNumbers.TORAD);
        left.rotateX(-currentPitch * MagicNumbers.TORAD);
        left.rotateY(-(currentYaw + 180) * MagicNumbers.TORAD);


        // Apply pitch
        facing.rotateAxis(-0.15 * pitch * MagicNumbers.TORAD, left.x, left.y, left.z);

        // Apply yaw
        Vector3d up = facing.cross(left, new Vector3d());
        facing.rotateAxis(0.15 * yaw * MagicNumbers.TORAD, up.x, up.y, up.z);
        left.rotateAxis(0.15 * yaw * MagicNumbers.TORAD, up.x, up.y, up.z);

        // Apply roll
        left.rotateAxis(0.15 * roll * MagicNumbers.TORAD, facing.x, facing.y, facing.z);


        // Extract new pitch, yaw, and roll
        double newPitch = -Math.asin(facing.y) * MagicNumbers.TODEG;
        double newYaw = -Math.atan2(facing.x, facing.z) * MagicNumbers.TODEG;

        Vector3d normalLeft = new Vector3d(1, 0, 0).rotateY(-(newYaw + 180) * MagicNumbers.TORAD);
        double newRoll = -Math.atan2(left.cross(normalLeft, new Vector3d()).dot(facing), left.dot(normalLeft)) * MagicNumbers.TODEG;

        // Calculate deltas
        double deltaY = newPitch - currentPitch;
        double deltaX = newYaw - currentYaw;
        double deltaRoll = newRoll - currentRoll;

        // Apply vanilla pitch and yaw
        this.changeLookDirection(deltaX / 0.15, deltaY / 0.15);

        // Apply roll
        this.roll += (float) deltaRoll;
        this.prevRoll += (float) deltaRoll;

        // fix hand spasm when wrapping yaw value
        if (this.getYaw() < -90 && this.renderYaw > 90) {
            this.renderYaw -= 360;
            this.lastRenderYaw -= 360;
        } else if (this.getYaw() > 90 && this.renderYaw < -90) {
            this.renderYaw += 360;
            this.lastRenderYaw += 360;
        }
    }
}