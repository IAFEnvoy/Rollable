package com.iafenvoy.rollable.flight.modifier;

import com.iafenvoy.rollable.config.RollableClientConfig;
import com.iafenvoy.rollable.flight.RollContext;
import com.iafenvoy.rollable.flight.RotateState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.SmoothUtil;

public class StrafeRollModifiers {
    public static final SmoothUtil STRAFE_ROLL_SMOOTHER = new SmoothUtil();
    public static final SmoothUtil STRAFE_YAW_SMOOTHER = new SmoothUtil();

    public static RotateState applyStrafeRoll(RotateState state, RollContext context) {
        if (!RollableClientConfig.INSTANCE.swim.smoothingEnabled.getValue()) return state;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return state;

        GameOptions options = MinecraftClient.getInstance().options;
        double rollDelta = 0;
        double yawDelta = 0;

        if (options.leftKey.isPressed() && !options.rightKey.isPressed()) {
            rollDelta = -(double) RollableClientConfig.INSTANCE.swim.strafeRollStrength.getValue();
            yawDelta = -(double) RollableClientConfig.INSTANCE.swim.strafeYawStrength.getValue();
        } else if (options.rightKey.isPressed() && !options.leftKey.isPressed()) {
            rollDelta = RollableClientConfig.INSTANCE.swim.strafeRollStrength.getValue();
            yawDelta = RollableClientConfig.INSTANCE.swim.strafeYawStrength.getValue();
        }

        if (RollableClientConfig.INSTANCE.swim.strafeSmoothingEnabled.getValue()) {
            rollDelta = STRAFE_ROLL_SMOOTHER.smooth(rollDelta, 1 / RollableClientConfig.INSTANCE.swim.values.getValue().roll() * context.getRenderDelta());
            yawDelta = STRAFE_YAW_SMOOTHER.smooth(yawDelta, 1 / RollableClientConfig.INSTANCE.swim.values.getValue().yaw() * context.getRenderDelta());
        }

        return state.add(0, yawDelta, rollDelta);
    }
}
