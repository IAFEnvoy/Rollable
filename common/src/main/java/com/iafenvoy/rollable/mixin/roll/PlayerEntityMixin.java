package com.iafenvoy.rollable.mixin.roll;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin {
    @Unique
    protected boolean rollable$isRolling;
    @Unique
    protected float rollable$prevRoll;
    @Unique
    protected float rollable$roll;

    @Override
    protected void rollable$baseTickTail(CallbackInfo ci) {
        this.rollable$baseTickTail2();

        this.rollable$prevRoll = this.rollable$getRoll();

        if (!this.rollable$isRolling()) {
            this.rollable$setRoll(0.0f);
        }
    }

    @Unique
    protected void rollable$baseTickTail2() {
    }

    @Override
    public boolean rollable$isRolling() {
        return this.rollable$isRolling;
    }

    @Override
    public void rollable$setRolling(boolean rolling) {
        this.rollable$isRolling = rolling;
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
}
