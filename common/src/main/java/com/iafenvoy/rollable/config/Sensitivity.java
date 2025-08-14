package com.iafenvoy.rollable.config;

public class Sensitivity {
    public double pitch = 1;
    public double yaw = 0.4;
    public double roll = 1;

    public Sensitivity() {
    }

    public Sensitivity(double pitch, double yaw, double roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    @Override
    public String toString() {
        return "pitch=%.2f, yaw=%.2f, roll=%.2f".formatted(this.pitch, this.yaw, this.roll);
    }
}
