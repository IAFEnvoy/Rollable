package com.iafenvoy.rollable.compat;

import com.iafenvoy.rollable.Rollable;
import ru.octol1ttle.flightassistant.api.autoflight.roll.RollSourceRegistrationCallback;
import ru.octol1ttle.flightassistant.api.computer.ComputerRegistrationCallback;

public class FlightAssistantIntegration {
    private static FlightAssistantComputer rollComputer;

    public static void init() {
        Rollable.LOGGER.info("[Rollable] Initializing support for Flight Assistant");
        ComputerRegistrationCallback.EVENT.register((bus, registrar) -> registrar.accept(FlightAssistantComputer.ID, rollComputer = new FlightAssistantComputer(bus)));
        RollSourceRegistrationCallback.EVENT.register(source -> source.accept(rollComputer));
    }
}
