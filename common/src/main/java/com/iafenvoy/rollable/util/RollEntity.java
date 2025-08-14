package com.iafenvoy.rollable.util;

import com.iafenvoy.rollable.config.Sensitivity;

public interface RollEntity {
    void rollable$changeElytraLook(double pitch, double yaw, double roll, Sensitivity sensitivity, double mouseDelta);

    void rollable$changeElytraLook(float pitch, float yaw, float roll);

    boolean rollable$isRolling();

    void rollable$setRolling(boolean rolling);

    float rollable$getRoll();

    float rollable$getRoll(float tickDelta);

    void rollable$setRoll(float roll);
}
