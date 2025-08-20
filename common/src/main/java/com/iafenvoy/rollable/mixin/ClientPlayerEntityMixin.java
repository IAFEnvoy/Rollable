package com.iafenvoy.rollable.mixin;

import com.iafenvoy.rollable.api.RollableEntity;
import com.iafenvoy.rollable.flight.RollContext;
import com.iafenvoy.rollable.flight.RollProcessGroup;
import com.iafenvoy.rollable.flight.RotateState;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity implements RollableEntity {
    @Shadow
    public float renderYaw;
    @Shadow
    public float lastRenderYaw;
    @Unique
    @Nullable
    protected RollProcessGroup rollable$processGroup;
    @Unique
    protected float rollable$prevRoll;
    @Unique
    protected float rollable$roll;

    public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickTail(CallbackInfo ci) {
        this.rollable$processGroup = RollProcessGroup.SHOULD_ROLL.invoker().get();
        this.rollable$prevRoll = this.rollable$getRoll();
        if (!this.rollable$isRolling()) this.rollable$setRoll(0.0f);
    }

    @Override
    public boolean rollable$isRolling() {
        return this.rollable$processGroup != null;
    }

    @Override
    public float rollable$getRoll() {
        return this.rollable$roll;
    }

    @Override
    public float rollable$getRoll(float tickDelta) {
        if (tickDelta == 1.0f) return this.rollable$getRoll();
        return MathHelper.lerp(tickDelta, this.rollable$prevRoll, this.rollable$getRoll());
    }

    @Override
    public void rollable$setRoll(float roll) {
        if (!Float.isFinite(roll)) {
            Util.error("Invalid entity rotation: " + roll + ", discarding.");
            return;
        }
        float lastRoll = this.rollable$getRoll();
        this.rollable$roll = roll;

        if (roll < -90 && lastRoll > 90) this.rollable$prevRoll -= 360;
        else if (roll > 90 && lastRoll < -90) this.rollable$prevRoll += 360;
    }

    @Override
    @Nullable
    public RollProcessGroup rollable$getCurrentProcessGroup() {
        return this.rollable$processGroup;
    }

    @Override
    public void rollable$changeLook(double pitch, double yaw, double roll, RotateState state, double mouseDelta) {
        RotateState rotDelta = new RotateState(pitch, yaw, roll);
        float currentRoll = this.rollable$getRoll();
        double pitch1 = this.getPitch();
        double yaw1 = this.getYaw();
        RotateState currentRotation = new RotateState(pitch1, yaw1, currentRoll);
        RollContext context = new RollContext(currentRotation, rotDelta, mouseDelta);

        if (this.rollable$processGroup != null) this.rollable$processGroup.processBeforeModifier(context);
        context.useModifier((rotation, ctx) -> rotation.multiply(state));
        if (this.rollable$processGroup != null) this.rollable$processGroup.processAfterModifier(context);

        rotDelta = context.getRotationDelta();
        this.rollable$changeLook((float) rotDelta.pitch(), (float) rotDelta.yaw(), (float) rotDelta.roll());
    }

    @Override
    public void rollable$changeLook(float pitch, float yaw, float roll) {
        float currentPitch = this.getPitch();
        float currentYaw = this.getYaw();
        float currentRoll = this.rollable$getRoll();

        // Convert pitch, yaw, and roll to a facing and left vector
        Vector3d facing = new Vector3d(this.getRotationVecClient().toVector3f());
        Vector3d left = new Vector3d(1, 0, 0);
        left.rotateZ(-Math.toRadians(currentRoll));
        left.rotateX(-Math.toRadians(currentPitch));
        left.rotateY(-Math.toRadians(currentYaw + 180));

        // Apply pitch
        facing.rotateAxis(-0.15 * Math.toRadians(pitch), left.x, left.y, left.z);

        // Apply yaw
        Vector3d up = facing.cross(left, new Vector3d());
        facing.rotateAxis(0.15 * Math.toRadians(yaw), up.x, up.y, up.z);
        left.rotateAxis(0.15 * Math.toRadians(yaw), up.x, up.y, up.z);

        // Apply roll
        left.rotateAxis(0.15 * Math.toRadians(roll), facing.x, facing.y, facing.z);

        // Extract new pitch, yaw, and roll
        double newPitch = Math.toDegrees(-Math.asin(facing.y));
        double newYaw = Math.toDegrees(-Math.atan2(facing.x, facing.z));

        Vector3d normalLeft = new Vector3d(1, 0, 0).rotateY(Math.toRadians(-newYaw - 180));
        double newRoll = Math.toDegrees(-Math.atan2(left.cross(normalLeft, new Vector3d()).dot(facing), left.dot(normalLeft)));

        // Calculate deltas
        double deltaY = newPitch - currentPitch;
        double deltaX = newYaw - currentYaw;
        double deltaRoll = newRoll - currentRoll;

        // Apply vanilla pitch and yaw
        this.changeLookDirection(deltaX / 0.15, deltaY / 0.15);

        // Apply roll
        this.rollable$roll += (float) deltaRoll;
        this.rollable$prevRoll += (float) deltaRoll;

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