package com.iafenvoy.rollable.impl.event;

import com.iafenvoy.rollable.api.event.RollContext;
import com.iafenvoy.rollable.api.rotation.RotationInstant;

import java.util.function.BooleanSupplier;

public final class RollContextImpl implements RollContext {
    private final RotationInstant currentRotation;
    private RotationInstant rotationDelta;
    private final double renderDelta;

    public RollContextImpl(RotationInstant currentRotation, RotationInstant rotationDelta, double renderDelta) {
        this.currentRotation = currentRotation;
        this.rotationDelta = rotationDelta;
        this.renderDelta = renderDelta;
    }

    @Override
    public RollContext useModifier(ConfiguresRotation modifier, BooleanSupplier condition) {
        this.rotationDelta = this.rotationDelta.useModifier(rotationInstant -> modifier.apply(rotationInstant, this), condition);
        return this;
    }

    @Override
    public RollContext useModifier(ConfiguresRotation modifier) {
        this.rotationDelta = this.rotationDelta.useModifier(rotationInstant -> modifier.apply(rotationInstant, this));
        return this;
    }

    @Override
    public RotationInstant getCurrentRotation() {
        return this.currentRotation;
    }

    @Override
    public RotationInstant getRotationDelta() {
        return this.rotationDelta;
    }

    @Override
    public double getRenderDelta() {
        return this.renderDelta;
    }
}
