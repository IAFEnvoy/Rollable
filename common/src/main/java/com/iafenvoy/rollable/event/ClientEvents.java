package com.iafenvoy.rollable.event;

import com.iafenvoy.rollable.RollableKeybindings;
import com.iafenvoy.rollable.api.RollEvents;
import com.iafenvoy.rollable.config.RollableClientConfig;
import com.iafenvoy.rollable.flight.RotateState;
import com.iafenvoy.rollable.flight.RotationModifiers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.SmoothUtil;

public class ClientEvents {
    private static final SmoothUtil PITCH_SMOOTHER = new SmoothUtil(), YAW_SMOOTHER = new SmoothUtil(), ROLL_SMOOTHER = new SmoothUtil();

    public static void registerEvents() {
        RollEvents.SHOULD_ROLL.register(ClientEvents::isFallFlying);
        // Keyboard modifiers
        RollEvents.EARLY_CAMERA_MODIFIERS.register(context -> context.useModifier(RotationModifiers.buttonControls(1800)), RollEvents.SHOULD_ROLL.invoker()::shouldRoll);
        // Mouse modifiers, including swapping axes
        RollEvents.EARLY_CAMERA_MODIFIERS.register(context -> context
                        .useModifier((rotationInstant, ctx) -> {
                            double pitch = rotationInstant.pitch();
                            double yaw = rotationInstant.yaw();
                            double roll = rotationInstant.roll();
                            if (!RollableClientConfig.INSTANCE.generals.switchRollAndYaw.getValue()) {
                                double temp = yaw;
                                yaw = roll;
                                roll = temp;
                            }
                            if (RollableClientConfig.INSTANCE.generals.invertPitch.getValue()) pitch *= -1;
                            return new RotateState(pitch, yaw, roll);
                        }),
                RollEvents.SHOULD_ROLL.invoker()::shouldRoll);
        // Generic movement modifiers, banking and such
        RollEvents.LATE_CAMERA_MODIFIERS.register(context -> context
                        .useModifier(RotationModifiers::applyControlSurfaceEfficacy, RollableClientConfig.INSTANCE.banking.simulateControlSurfaceEfficacy::getValue)
                        .useModifier(RotationModifiers.smoothing(PITCH_SMOOTHER, YAW_SMOOTHER, ROLL_SMOOTHER, RollableClientConfig.INSTANCE.sensitivity.cameraSmoothing.getValue()))
                        .useModifier(RotationModifiers::banking, RollableClientConfig.INSTANCE.banking.enabled::getValue)
                        .useModifier(RotationModifiers::reorient, RollableClientConfig.INSTANCE.banking.automaticRighting::getValue),
                RollEvents.SHOULD_ROLL.invoker()::shouldRoll);
    }

    public static void clientTick(MinecraftClient client) {
        if (!isFallFlying()) {
            PITCH_SMOOTHER.clear();
            YAW_SMOOTHER.clear();
            ROLL_SMOOTHER.clear();
        }
        RollableKeybindings.clientTick(client);
    }

    public static boolean isFallFlying() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return false;
        if (RollableClientConfig.INSTANCE.generals.disableWhenSubmerged.getValue() && player.isSubmergedInWater())
            return false;
        return player.isFallFlying();
    }
}
