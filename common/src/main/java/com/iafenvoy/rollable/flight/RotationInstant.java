package com.iafenvoy.rollable.flight;

import com.iafenvoy.rollable.config.Sensitivity;

import java.util.function.BooleanSupplier;

public record RotationInstant(double pitch, double yaw, double roll) {
    public static RotationInstant of(double pitch, double yaw, double roll) {
        return new RotationInstant(pitch, yaw, roll);
    }

    public RotationInstant add(double pitch, double yaw, double roll) {
        return new RotationInstant(this.pitch + pitch, this.yaw + yaw, this.roll + roll);
    }

    public RotationInstant multiply(double pitch, double yaw, double roll) {
        return new RotationInstant(this.pitch * pitch, this.yaw * yaw, this.roll * roll);
    }

    public RotationInstant addAbsolute(double x, double y, double currentRoll) {
        double cos = Math.cos(currentRoll);
        double sin = Math.sin(currentRoll);
        return new RotationInstant(this.pitch - y * cos - x * sin, this.yaw - y * sin + x * cos, this.roll);
    }

    public RotationInstant applySensitivity(Sensitivity sensitivity) {
        return new RotationInstant(
                this.pitch * sensitivity.pitch,
                this.yaw * sensitivity.yaw,
                this.roll * sensitivity.roll
        );
    }

    public RotationInstant useModifier(ConfiguresRotation modifier, BooleanSupplier condition) {
        return condition.getAsBoolean() ? modifier.apply(this) : this;
    }

    public RotationInstant useModifier(ConfiguresRotation modifier) {
        return this.useModifier(modifier, () -> true);
    }

    @FunctionalInterface
    public interface ConfiguresRotation {
        RotationInstant apply(RotationInstant rotationInstant);
    }
}
