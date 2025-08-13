package com.iafenvoy.rollable.api.event;

import com.iafenvoy.rollable.impl.event.EventImpl;
import net.minecraft.client.network.ClientPlayerEntity;

public interface StarFox64Events {
    Event<DoesABarrelRollEvent> DOES_A_BARREL_ROLL = new EventImpl<>();

    static void doesABarrelRoll(ClientPlayerEntity player) {
        for (DoesABarrelRollEvent listener : DOES_A_BARREL_ROLL.getListeners()) {
            listener.onBarrelRoll(player);
        }
    }

    interface DoesABarrelRollEvent {
        void onBarrelRoll(ClientPlayerEntity player);
    }
}
