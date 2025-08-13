package com.iafenvoy.rollable.api;

import net.minecraft.client.network.ClientPlayerEntity;

public interface RollMouse {
    boolean doABarrelRoll$updateMouse(ClientPlayerEntity player, double cursorDeltaX, double cursorDeltaY, double mouseDelta);
}
