package com.iafenvoy.rollable.mixin.roll.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin {
    @Unique
    protected boolean isRolling;
    @Unique
    protected float prevRoll;
    @Unique
    protected float roll;

    @Override
    protected void rollable$baseTickTail(CallbackInfo ci) {
        this.rollable$baseTickTail2();

        this.prevRoll = this.rollable$getRoll();

        if (!this.rollable$isRolling()) {
            this.rollable$setRoll(0.0f);
        }
    }

    @Unique
    protected void rollable$baseTickTail2() {
    }

    @Override
    public boolean rollable$isRolling() {
        return this.isRolling;
    }

    @Override
    public void rollable$setRolling(boolean rolling) {
        this.isRolling = rolling;
    }

    @Override
    public float rollable$getRoll() {
        return this.roll;
    }

    @Override
    public float rollable$getRoll(float tickDelta) {
        if (tickDelta == 1.0f) return this.rollable$getRoll();
        return MathHelper.lerp(tickDelta, this.prevRoll, this.rollable$getRoll());
    }

    @Override
    public void rollable$setRoll(float roll) {
        if (!Float.isFinite(roll)) {
            Util.error("Invalid entity rotation: " + roll + ", discarding.");
            return;
        }
        float lastRoll = this.rollable$getRoll();
        this.roll = roll;

        if (roll < -90 && lastRoll > 90) this.prevRoll -= 360;
        else if (roll > 90 && lastRoll < -90) this.prevRoll += 360;
    }
}
