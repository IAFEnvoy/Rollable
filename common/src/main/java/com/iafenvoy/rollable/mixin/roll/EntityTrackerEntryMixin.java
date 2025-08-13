package com.iafenvoy.rollable.mixin.roll;

import com.iafenvoy.rollable.api.RollEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.EntityTrackerEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTrackerEntry.class)
public abstract class EntityTrackerEntryMixin {
    @Shadow
    @Final
    private Entity entity;

    @Unique
    private boolean lastIsRolling;
    @Unique
    private float lastRoll;

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void doABarrelRoll$syncRollS2C(CallbackInfo ci) {
        RollEntity rollEntity = (RollEntity) this.entity;
        boolean isRolling = rollEntity.doABarrelRoll$isRolling();
        float roll = rollEntity.doABarrelRoll$getRoll();

        if (isRolling != this.lastIsRolling || roll != this.lastRoll) {
            this.lastIsRolling = isRolling;
            this.lastRoll = roll;
        }
    }
}
