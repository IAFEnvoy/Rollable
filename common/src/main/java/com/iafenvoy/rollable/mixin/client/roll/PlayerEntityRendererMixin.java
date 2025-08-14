package com.iafenvoy.rollable.mixin.client.roll;

import com.iafenvoy.rollable.api.RollEntity;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
    @ModifyArg(method = "setupTransforms(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/util/math/MatrixStack;FFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionf;)V", ordinal = 1), index = 0)
    private Quaternionf rollable$modifyRoll(Quaternionf original, @Local(argsOnly = true) AbstractClientPlayerEntity player, @Local(argsOnly = true, ordinal = 2) float tickDelta) {
        RollEntity rollEntity = (RollEntity) player;
        if (rollEntity.rollable$isRolling()) {
            float roll = rollEntity.rollable$getRoll(tickDelta);
            return RotationAxis.POSITIVE_Y.rotationDegrees(roll);
        }
        return original;
    }
}
