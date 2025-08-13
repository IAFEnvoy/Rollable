package com.iafenvoy.rollable.forge.mixin;

import com.iafenvoy.rollable.EventCallbacksClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow
    protected int scaledWidth;
    @Shadow
    protected int scaledHeight;

    @Inject(
            method = "renderCrosshair",
            at = @At(value = "HEAD")
    )
    private void doABarrelRoll$renderCrosshairHead(DrawContext context, CallbackInfo ci) {
        context.getMatrices().push();
        // There's really no way for me to get it, so no tickdelta for you Forge!
        EventCallbacksClient.onRenderCrosshair(context, 0, scaledWidth, scaledHeight);
    }

    @Inject(
            method = "renderCrosshair",
            at = @At(value = "RETURN")
    )
    private void doABarrelRoll$renderCrosshairReturn(DrawContext context, CallbackInfo ci) {
        context.getMatrices().pop();
    }
}
