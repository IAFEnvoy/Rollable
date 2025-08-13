package com.iafenvoy.rollable.mixin.client.roll;

import com.iafenvoy.rollable.api.RollEntity;
import com.iafenvoy.rollable.api.RollMouse;
import com.iafenvoy.rollable.config.RollableClientConfig;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.joml.Vector2d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Mouse.class)
public abstract class MouseMixin implements RollMouse {
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private double lastMouseUpdateTime;

    @Unique
    private final Vector2d mouseTurnVec = new Vector2d();

    @ModifyVariable(method = "updateMouse", index = 3, at = @At(value = "STORE", ordinal = 0))
    private double doABarrelRoll$captureDelta(double original, @Share("mouseDelta") LocalDoubleRef mouseDeltaRef) {
        if (this.lastMouseUpdateTime != Double.MIN_VALUE) mouseDeltaRef.set(original);
        return original;
    }

    @Inject(method = "updateMouse", at = @At(value = "RETURN", ordinal = 0))
    private void doABarrelRoll$maintainMouseMomentum(CallbackInfo ci, @Share("mouseDelta") LocalDoubleRef mouseDeltaRef) {
        if (this.client.player != null && !this.client.isPaused()) {
            this.doABarrelRoll$updateMouse(this.client.player, 0, 0, mouseDeltaRef.get());
        }
    }

    @WrapWithCondition(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    private boolean doABarrelRoll$changeLookDirection(ClientPlayerEntity player, double cursorDeltaX, double cursorDeltaY, @Share("mouseDelta") LocalDoubleRef mouseDeltaRef) {
        return !this.doABarrelRoll$updateMouse(player, cursorDeltaX, cursorDeltaY, mouseDeltaRef.get());
    }

    @Override
    public boolean doABarrelRoll$updateMouse(ClientPlayerEntity player, double cursorDeltaX, double cursorDeltaY, double mouseDelta) {
        RollEntity rollPlayer = (RollEntity) player;

        if (rollPlayer.doABarrelRoll$isRolling()) {

            if (RollableClientConfig.INSTANCE.generals.momentumBasedMouse.getValue()) {

                // add the mouse movement to the current vector and normalize if needed
                this.mouseTurnVec.add(new Vector2d(cursorDeltaX, cursorDeltaY).mul(1f / 300));
                if (this.mouseTurnVec.lengthSquared() > 1.0) {
                    this.mouseTurnVec.normalize();
                }
                Vector2d readyTurnVec = new Vector2d(this.mouseTurnVec);

                // check if the vector is within the deadzone
                double deadzone = RollableClientConfig.INSTANCE.generals.momentumMouseDeadzone.getValue();
                if (readyTurnVec.lengthSquared() < deadzone * deadzone) readyTurnVec.zero();

                // enlarge the vector and apply it to the camera
                readyTurnVec.mul(1200 * (float) mouseDelta);
                rollPlayer.doABarrelRoll$changeElytraLook(readyTurnVec.y, readyTurnVec.x, 0, RollableClientConfig.INSTANCE.sensitivity.desktop.getValue(), mouseDelta);

            } else {

                // if we are not using a momentum based mouse, we can reset it and apply the values directly
                this.mouseTurnVec.zero();
                rollPlayer.doABarrelRoll$changeElytraLook(cursorDeltaY, cursorDeltaX, 0, RollableClientConfig.INSTANCE.sensitivity.desktop.getValue(), mouseDelta);
            }

            return true;
        }

        this.mouseTurnVec.zero();
        return false;
    }
}
