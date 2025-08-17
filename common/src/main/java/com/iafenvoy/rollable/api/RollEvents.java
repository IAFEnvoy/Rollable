package com.iafenvoy.rollable.api;

import com.iafenvoy.rollable.event.Event;
import com.iafenvoy.rollable.flight.RollContext;

public interface RollEvents {
    /**
     * If any listener returns true, roll will be unlocked.
     */
    Event<ShouldRollCheckEvent> SHOULD_ROLL = new Event<>(listeners -> () -> {
        for (ShouldRollCheckEvent listener : listeners)
            if (listener.shouldRoll())
                return true;
        return false;
    });

    /**
     * Modifiers registered here will be applied <b>before</b> sensitivity. So will be affected by it.
     */
    Event<CameraModifiersEvent> EARLY_CAMERA_MODIFIERS = new Event<>(listeners -> context -> {
        for (CameraModifiersEvent listener : listeners)
            listener.applyCameraModifiers(context);
    });

    /**
     * Modifiers registered here will be applied <b>after</b> sensitivity. So will not be affected by it.
     */
    Event<CameraModifiersEvent> LATE_CAMERA_MODIFIERS = new Event<>(listeners -> context -> {
        for (CameraModifiersEvent listener : listeners)
            listener.applyCameraModifiers(context);
    });

    interface ShouldRollCheckEvent {
        boolean shouldRoll();
    }

    interface CameraModifiersEvent {
        void applyCameraModifiers(RollContext context);
    }
}
