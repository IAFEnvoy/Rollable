package com.iafenvoy.rollable.mixin.client.roll;

import com.iafenvoy.rollable.api.RollEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Environment(EnvType.CLIENT)
@Mixin(DebugHud.class)
public abstract class DebugHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    // Not using ModifyArg**s** here to be compatible with Forge
    @ModifyArg(
            method = "getLeftText",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;",
                    ordinal = 6
            ),
            index = 1,
            require = 0
    )
    private String doABarrelRoll$modifyDebugHudText(String format) {
        Entity cameraEntity = this.client.getCameraEntity();
        if (cameraEntity == null) return null;

        // Carefully insert a new number format specifier into the facing string
        String firstHalf = format.substring(0, format.length() - 1);
        String secondHalf = format.substring(format.length() - 1);
        return firstHalf + " / %.1f" + secondHalf;
    }

    @ModifyArg(
            method = "getLeftText",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;",
                    ordinal = 6
            ),
            index = 2,
            require = 0
    )
    private Object[] doABarrelRoll$modifyDebugHudText2(Object[] args) {
        Entity cameraEntity = this.client.getCameraEntity();
        if (cameraEntity == null) return args;

        // Add the roll value to the format arguments
        float roll = ((RollEntity) this.client.getCameraEntity()).doABarrelRoll$getRoll();
        Object[] newFmtArgs = new Object[args.length + 1];
        System.arraycopy(args, 0, newFmtArgs, 0, args.length);
        newFmtArgs[args.length] = roll;
        return newFmtArgs;
    }
}
