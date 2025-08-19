package com.iafenvoy.rollable.event;

import com.iafenvoy.rollable.Rollable;
import com.iafenvoy.rollable.RollableKeybindings;
import com.iafenvoy.rollable.config.RollableClientConfig;
import com.iafenvoy.rollable.flight.RollContext;
import com.iafenvoy.rollable.flight.RollProcessGroup;
import com.iafenvoy.rollable.flight.RotateState;
import com.iafenvoy.rollable.flight.modifier.RotationModifiers;
import com.iafenvoy.rollable.flight.modifier.StrafeRollModifiers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.SmoothUtil;

public class ClientEvents {
    private static final RollProcessGroup ELYTRA_GROUP = RollProcessGroup.get(Rollable.id("elytra")), SWIMMING_GROUP = RollProcessGroup.get(Rollable.id("swimming"));
    private static final SmoothGroup ELYTRA = new SmoothGroup(), SWIMMING = new SmoothGroup();

    public static void registerEvents() {
        // For Elytra Rolling
        ELYTRA_GROUP.registerEnablePredicate(ClientEvents::isFallFlying);
        // Keyboard modifiers
        ELYTRA_GROUP.registerBeforeModifier(context -> context.useModifier(RotationModifiers.buttonControls(1800)));
        // Mouse modifiers, including swapping axes
        ELYTRA_GROUP.registerBeforeModifier(context -> context.useModifier(RotationModifiers::configureRotation));
        // Generic movement modifiers, banking and such
        ELYTRA_GROUP.registerAfterModifier(context -> context
                .useModifier(RotationModifiers::applyControlSurfaceEfficacy, RollableClientConfig.INSTANCE.banking.simulateControlSurfaceEfficacy::getValue)
                .useModifier(ELYTRA.smoothing(RollableClientConfig.INSTANCE.sensitivity.cameraSmoothing.getValue()))
                .useModifier(RotationModifiers::banking, RollableClientConfig.INSTANCE.banking.enabled::getValue)
                .useModifier(RotationModifiers::reorient, RollableClientConfig.INSTANCE.banking.automaticRighting::getValue));
        // For Swimming Rolling
        SWIMMING_GROUP.registerEnablePredicate(ClientEvents::isSwimming);
        SWIMMING_GROUP.registerBeforeModifier(context -> context.useModifier(StrafeRollModifiers::applyStrafeRoll));
        SWIMMING_GROUP.registerBeforeModifier(context -> context.useModifier(RotationModifiers::configureRotation));
        SWIMMING_GROUP.registerAfterModifier(context -> context.useModifier(SWIMMING.smoothing(RollableClientConfig.INSTANCE.swim.values.getValue())));

    }

    public static void clientTick(MinecraftClient client) {
        if (!isFallFlying()) ELYTRA.clear();
        if (!isSwimming()) SWIMMING.clear();
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
