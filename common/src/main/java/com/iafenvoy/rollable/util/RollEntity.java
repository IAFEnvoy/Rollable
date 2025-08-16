package com.iafenvoy.rollable.util;

import com.iafenvoy.rollable.flight.RotateState;

public interface RollEntity {
    void rollable$changeElytraLook(double pitch, double yaw, double roll, RotateState state, double mouseDelta);

    void rollable$changeElytraLook(float pitch, float yaw, float roll);

    boolean rollable$isRolling();

    float rollable$getRoll();

    float rollable$getRoll(float tickDelta);

    void rollable$setRoll(float roll);
}
