package com.iafenvoy.rollable.mixin.client.roll;

import com.iafenvoy.rollable.api.RollCamera;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    private Camera camera;

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/RotationAxis;rotationDegrees(F)Lorg/joml/Quaternionf;",
                    ordinal = 2
            )
    )
    public void doABarrelRoll$renderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        matrix.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(((RollCamera) this.camera).doABarrelRoll$getRoll()));
    }
}
