package com.iafenvoy.rollable.event;

import com.iafenvoy.rollable.RollableKeybindings;
import com.iafenvoy.rollable.api.RollEvents;
import com.iafenvoy.rollable.config.RollableClientConfig;
import com.iafenvoy.rollable.flight.RollContext;
import com.iafenvoy.rollable.flight.RotateState;
import com.iafenvoy.rollable.flight.modifier.RotationModifiers;
import com.iafenvoy.rollable.flight.modifier.StrafeRollModifiers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.SmoothUtil;

public class ClientEvents {
    private static final SmoothGroup ELYTRA_FLY = new SmoothGroup(), SWIMMING = new SmoothGroup();

    public static void registerEvents() {
        // For Elytra Rolling
        RollEvents.SHOULD_ROLL.register(() -> RollEvents.SHOULD_FLYING_ROLL.invoker().getAsBoolean());
        RollEvents.SHOULD_FLYING_ROLL.register(ClientEvents::isFallFlying);
        // Keyboard modifiers
        RollEvents.EARLY_CAMERA_MODIFIERS.register(context -> context.useModifier(RotationModifiers.buttonControls(1800)), RollEvents.SHOULD_FLYING_ROLL.invoker());
        // Mouse modifiers, including swapping axes
        RollEvents.EARLY_CAMERA_MODIFIERS.register(context -> context.useModifier(RotationModifiers::configureRotation), RollEvents.SHOULD_FLYING_ROLL.invoker());
        // Generic movement modifiers, banking and such
        RollEvents.LATE_CAMERA_MODIFIERS.register(context -> context
                        .useModifier(RotationModifiers::applyControlSurfaceEfficacy, RollableClientConfig.INSTANCE.banking.simulateControlSurfaceEfficacy::getValue)
                        .useModifier(ELYTRA_FLY.smoothing(RollableClientConfig.INSTANCE.sensitivity.cameraSmoothing.getValue()))
                        .useModifier(RotationModifiers::banking, RollableClientConfig.INSTANCE.banking.enabled::getValue)
                        .useModifier(RotationModifiers::reorient, RollableClientConfig.INSTANCE.banking.automaticRighting::getValue),
                RollEvents.SHOULD_FLYING_ROLL.invoker());

        // For Swimming Rolling
        RollEvents.SHOULD_ROLL.register(() -> RollEvents.SHOULD_SWIMMING_ROLL.invoker().getAsBoolean());
        RollEvents.SHOULD_SWIMMING_ROLL.register(ClientEvents::isSwimming);
        RollEvents.EARLY_CAMERA_MODIFIERS.register(context -> context
                        .useModifier(StrafeRollModifiers::applyStrafeRoll),
                () -> RollEvents.SHOULD_SWIMMING_ROLL.invoker().getAsBoolean() && !RollEvents.SHOULD_FLYING_ROLL.invoker().getAsBoolean());
        RollEvents.EARLY_CAMERA_MODIFIERS.register(context -> context
                        .useModifier(RotationModifiers::configureRotation),
                () -> RollEvents.SHOULD_SWIMMING_ROLL.invoker().getAsBoolean() && !RollEvents.SHOULD_FLYING_ROLL.invoker().getAsBoolean());
        RollEvents.LATE_CAMERA_MODIFIERS.register(context -> context
                        .useModifier(SWIMMING.smoothing(RollableClientConfig.INSTANCE.swim.values.getValue())),
                () -> RollEvents.SHOULD_SWIMMING_ROLL.invoker().getAsBoolean() && !RollEvents.SHOULD_FLYING_ROLL.invoker().getAsBoolean() && RollableClientConfig.INSTANCE.swim.smoothingEnabled.getValue());

    }

    public static void clientTick(MinecraftClient client) {
        if (!isFallFlying()) ELYTRA_FLY.clear();
        RollableKeybindings.clientTick(client);
    }

    public static boolean isFallFlying() {
        if (!RollableClientConfig.INSTANCE.generals.enabled.getValue()) return false;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return false;
        if (RollableClientConfig.INSTANCE.generals.disableWhenSubmerged.getValue() && player.isSubmergedInWater())
            return false;
        return player.isFallFlying();
    }

    public static boolean isSwimming() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        return RollableClientConfig.INSTANCE.swim.enabled.getValue() && player != null && player.isSwimming() && player.isSubmergedInWater();
    }

    private record SmoothGroup(SmoothUtil pitch, SmoothUtil yaw, SmoothUtil roll) {
        public SmoothGroup() {
            this(new SmoothUtil(), new SmoothUtil(), new SmoothUtil());
        }

        public RollContext.ConfiguresRotation smoothing(RotateState smoothness) {
            return RotationModifiers.smoothing(this.pitch, this.yaw, this.roll, smoothness);
        }

        public void clear() {
            this.pitch.clear();
            this.yaw.clear();
            this.roll.clear();
        }
    }
}
