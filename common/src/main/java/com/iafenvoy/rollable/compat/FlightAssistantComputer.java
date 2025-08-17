package com.iafenvoy.rollable.compat;

import com.iafenvoy.rollable.Rollable;
import com.iafenvoy.rollable.api.RollEntity;
import net.minecraft.util.Identifier;
import ru.octol1ttle.flightassistant.api.autoflight.roll.RollSource;
import ru.octol1ttle.flightassistant.api.computer.Computer;
import ru.octol1ttle.flightassistant.api.computer.ComputerBus;
import ru.octol1ttle.flightassistant.api.util.FATickCounter;

public class FlightAssistantComputer extends Computer implements RollSource {
    public static final Identifier ID = Rollable.id("roll");

    public FlightAssistantComputer(ComputerBus computers) {
        super(computers);
    }

    @Override
    public boolean isActive() {
        return ((RollEntity) this.getComputers().getData().getPlayer()).rollable$isRolling();
    }

    @Override
    public float getRoll() {
        return ((RollEntity) this.getComputers().getData().getPlayer()).rollable$getRoll(FATickCounter.INSTANCE.getPartialTick());
    }

    @Override
    public void addRoll(float diff) {
        ((RollEntity) this.getComputers().getData().getPlayer()).rollable$setRoll(this.getRoll() + diff);
    }

    @Override
    public void tick() {
    }

    @Override
    public void reset() {
    }
}
