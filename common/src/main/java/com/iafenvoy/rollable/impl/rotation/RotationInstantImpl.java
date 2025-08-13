package com.iafenvoy.rollable.impl.rotation;

import com.iafenvoy.rollable.api.rotation.RotationInstant;
import com.iafenvoy.rollable.config.Sensitivity;

import java.util.function.BooleanSupplier;

public record RotationInstantImpl(double pitch, double yaw, double roll) implements RotationInstant {
    @Override
    public RotationInstant add(double pitch, double yaw, double roll) {
        return new RotationInstantImpl(this.pitch + pitch, this.yaw + yaw, this.roll + roll);
    }

    @Override
    public RotationInstant multiply(double pitch, double yaw, double roll) {
        return new RotationInstantImpl(this.pitch * pitch, this.yaw * yaw, this.roll * roll);
    }

    @Override
    public RotationInstant addAbsolute(double x, double y, double currentRoll) {
        double cos = Math.cos(currentRoll);
        double sin = Math.sin(currentRoll);
        return new RotationInstantImpl(this.pitch - y * cos - x * sin, this.yaw - y * sin + x * cos, this.roll);
    }

    @Override
    public RotationInstant applySensitivity(Sensitivity sensitivity) {
        return new RotationInstantImpl(
                this.pitch * sensitivity.pitch,
                this.yaw * sensitivity.yaw,
                this.roll * sensitivity.roll
        );
    }

    @Override
    public RotationInstant useModifier(ConfiguresRotation modifier, BooleanSupplier condition) {
        return condition.getAsBoolean() ? modifier.apply(this) : this;
    }

    @Override
    public RotationInstant useModifier(ConfiguresRotation modifier) {
        return this.useModifier(modifier, () -> true);
    }
}
