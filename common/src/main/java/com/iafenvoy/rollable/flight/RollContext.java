package com.iafenvoy.rollable.flight;

import java.util.function.BooleanSupplier;

public class RollContext {
    public static RollContext of(RotationInstant currentRotation, RotationInstant rotationDelta, double delta) {
        return new RollContext(currentRotation, rotationDelta, delta);
    }

    private final RotationInstant currentRotation;
    private RotationInstant rotationDelta;
    private final double renderDelta;

    public RollContext(RotationInstant currentRotation, RotationInstant rotationDelta, double renderDelta) {
        this.currentRotation = currentRotation;
        this.rotationDelta = rotationDelta;
        this.renderDelta = renderDelta;
    }

    public RollContext useModifier(ConfiguresRotation modifier, BooleanSupplier condition) {
        this.rotationDelta = this.rotationDelta.useModifier(rotationInstant -> modifier.apply(rotationInstant, this), condition);
        return this;
    }

    public RollContext useModifier(ConfiguresRotation modifier) {
        this.rotationDelta = this.rotationDelta.useModifier(rotationInstant -> modifier.apply(rotationInstant, this));
        return this;
    }

    public RotationInstant getCurrentRotation() {
        return this.currentRotation;
    }

    public RotationInstant getRotationDelta() {
        return this.rotationDelta;
    }

    public double getRenderDelta() {
        return this.renderDelta;
    }

    @FunctionalInterface
    public interface ConfiguresRotation {
        RotationInstant apply(RotationInstant rotationInstant, RollContext context);
    }
}
