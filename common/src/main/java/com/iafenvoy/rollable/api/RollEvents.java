package com.iafenvoy.rollable.api;

import com.iafenvoy.rollable.event.Event;
import com.iafenvoy.rollable.flight.RollContext;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public interface RollEvents {
    /**
     * If any listener returns true, roll will be unlocked.
     */
    Event<BooleanSupplier> SHOULD_ROLL = new Event<>(listeners -> () -> {
        for (BooleanSupplier listener : listeners)
            if (listener.getAsBoolean())
                return true;
        return false;
    });
    /**
     * If any listener returns true, fly roll will be unlocked.
     */
    Event<BooleanSupplier> SHOULD_FLYING_ROLL = new Event<>(listeners -> () -> {
        for (BooleanSupplier listener : listeners)
            if (listener.getAsBoolean())
                return true;
        return false;
    });
    /**
     * If any listener returns true, swim roll will be unlocked.
     */
    Event<BooleanSupplier> SHOULD_SWIMMING_ROLL = new Event<>(listeners -> () -> {
        for (BooleanSupplier listener : listeners)
            if (listener.getAsBoolean())
                return true;
        return false;
    });

    /**
     * Modifiers registered here will be applied <b>before</b> sensitivity. So will be affected by it.
     */
    Event<Consumer<RollContext>> EARLY_CAMERA_MODIFIERS = new Event<>(listeners -> context -> {
        for (Consumer<RollContext> listener : listeners)
            listener.accept(context);
    });

    /**
     * Modifiers registered here will be applied <b>after</b> sensitivity. So will not be affected by it.
     */
    Event<Consumer<RollContext>> LATE_CAMERA_MODIFIERS = new Event<>(listeners -> context -> {
        for (Consumer<RollContext> listener : listeners)
            listener.accept(context);
    });
}
