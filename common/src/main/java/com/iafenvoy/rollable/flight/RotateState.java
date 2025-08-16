package com.iafenvoy.rollable.flight;

import org.jetbrains.annotations.NotNull;

public record RotateState(double pitch, double yaw, double roll) {
    public RotateState add(double pitch, double yaw, double roll) {
        return new RotateState(this.pitch + pitch, this.yaw + yaw, this.roll + roll);
    }

    public RotateState multiply(double pitch, double yaw, double roll) {
        return new RotateState(this.pitch * pitch, this.yaw * yaw, this.roll * roll);
    }

    public RotateState multiply(RotateState state) {
        return new RotateState(this.pitch * state.pitch(), this.yaw * state.yaw(), this.roll * state.roll());
    }

    public RotateState fixNaN() {
        return new RotateState(Double.isNaN(this.pitch) ? 0 : this.pitch, Double.isNaN(this.yaw) ? 0 : this.yaw, Double.isNaN(this.roll) ? 0 : this.roll);
    }

    public RotateState addAbsolute(double x, double y, double currentRoll) {
        double cos = Math.cos(currentRoll);
        double sin = Math.sin(currentRoll);
        return new RotateState(this.pitch - y * cos - x * sin, this.yaw - y * sin + x * cos, this.roll);
    }

    @Override
    public @NotNull String toString() {
        return "pitch=%.2f, yaw=%.2f, roll=%.2f".formatted(this.pitch, this.yaw, this.roll);
    }
}
