package com.iafenvoy.rollable.api;

import com.iafenvoy.rollable.flight.RollProcessGroup;
import com.iafenvoy.rollable.flight.RotateState;
import org.jetbrains.annotations.Nullable;

public interface RollableEntity {
    void rollable$changeLook(double pitch, double yaw, double roll, RotateState state, double mouseDelta);

    void rollable$changeLook(float pitch, float yaw, float roll);

    boolean rollable$isRolling();

    float rollable$getRoll();

    float rollable$getRoll(float tickDelta);

    void rollable$setRoll(float roll);

    @Nullable
    RollProcessGroup rollable$getCurrentProcessGroup();
}
