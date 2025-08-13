package com.iafenvoy.rollable.mixin;

import com.iafenvoy.rollable.util.Timeout;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "tick", at = @At("RETURN"))
    private void endTick(CallbackInfo ci) {
        Timeout.runTimeout((MinecraftServer) (Object) this);
    }
}
