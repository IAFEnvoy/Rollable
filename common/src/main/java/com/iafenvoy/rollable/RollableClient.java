package com.iafenvoy.rollable;

import com.iafenvoy.jupiter.ConfigManager;
import com.iafenvoy.jupiter.render.screen.WidgetBuilderManager;
import com.iafenvoy.jupiter.render.widget.builder.TextFieldWidgetBuilder;
import com.iafenvoy.rollable.event.RollEvents;
import com.iafenvoy.rollable.flight.RotationInstant;
import com.iafenvoy.rollable.config.RollableClientConfig;
import com.iafenvoy.rollable.config.entry.ExpressionParserEntry;
import com.iafenvoy.rollable.config.entry.SensitivityEntry;
import com.iafenvoy.rollable.config.entry.dialog.SensitivityWidgetBuilder;
import com.iafenvoy.rollable.event.RollGroup;
import com.iafenvoy.rollable.flight.RotationModifiers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.SmoothUtil;

public class RollableClient {
    public static final SmoothUtil PITCH_SMOOTHER = new SmoothUtil();
    public static final SmoothUtil YAW_SMOOTHER = new SmoothUtil();
    public static final SmoothUtil ROLL_SMOOTHER = new SmoothUtil();
    public static final RollGroup FALL_FLYING_GROUP = RollGroup.of(Rollable.id("fall_flying"));
    public static double throttle = 0;

    public static void init() {
        //Register Config
        WidgetBuilderManager.register(ExpressionParserEntry.TYPE, TextFieldWidgetBuilder::new);
        WidgetBuilderManager.register(SensitivityEntry.TYPE, config -> new SensitivityWidgetBuilder((SensitivityEntry) config));
        ConfigManager.getInstance().registerConfigHandler(RollableClientConfig.INSTANCE);

        FALL_FLYING_GROUP.trueIf(RollableClient::isFallFlying);

        // Keyboard modifiers
        RollEvents.EARLY_CAMERA_MODIFIERS.register(context -> context.useModifier(RotationModifiers.buttonControls(1800)), FALL_FLYING_GROUP);

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
                            if (RollableClientConfig.INSTANCE.generals.invertPitch.getValue())
                                pitch *= -1;

                            return RotationInstant.of(pitch, yaw, roll);
                        }),
                FALL_FLYING_GROUP);

        // Generic movement modifiers, banking and such
        RollEvents.LATE_CAMERA_MODIFIERS.register(context -> context
                        .useModifier(RotationModifiers::applyControlSurfaceEfficacy, RollableClientConfig.INSTANCE.banking.simulateControlSurfaceEfficacy::getValue)
                        .useModifier(RotationModifiers.smoothing(
                                PITCH_SMOOTHER, YAW_SMOOTHER, ROLL_SMOOTHER,
                                RollableClientConfig.INSTANCE.sensitivity.cameraSmoothing.getValue()
                        ))
                        .useModifier(RotationModifiers::banking, RollableClientConfig.INSTANCE.banking.enabled::getValue)
                        .useModifier(RotationModifiers::reorient, RollableClientConfig.INSTANCE.banking.automaticRighting::getValue),
                FALL_FLYING_GROUP);
    }

    public static void clearValues() {
        PITCH_SMOOTHER.clear();
        YAW_SMOOTHER.clear();
        ROLL_SMOOTHER.clear();
        throttle = 0;
    }

    public static boolean isFallFlying() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return false;
        if (RollableClientConfig.INSTANCE.generals.disableWhenSubmerged.getValue() && player.isSubmergedInWater())
            return false;
        return player.isFallFlying();
    }
}
