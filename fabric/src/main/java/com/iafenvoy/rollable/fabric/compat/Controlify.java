package com.iafenvoy.rollable.fabric.compat;

import com.iafenvoy.rollable.RollableKeybindings;
import com.iafenvoy.rollable.Rollable;
import com.iafenvoy.rollable.RollableClient;
import com.iafenvoy.rollable.config.RollableClientConfig;
import com.iafenvoy.rollable.config.Sensitivity;
import com.iafenvoy.rollable.event.RollEvents;
import com.iafenvoy.rollable.flight.RollContext;
import com.iafenvoy.rollable.flight.RotationInstant;
import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.bind.ControlifyBindApi;
import dev.isxander.controlify.api.bind.InputBindingSupplier;
import dev.isxander.controlify.api.entrypoint.ControlifyEntrypoint;
import dev.isxander.controlify.api.event.ControlifyEvents;
import dev.isxander.controlify.bindings.BindContext;
import dev.isxander.controlify.controller.ControllerEntity;
import net.minecraft.text.Text;

import java.util.Optional;

public class Controlify implements ControlifyEntrypoint {
    public static final BindContext FALL_FLYING = new BindContext(Rollable.id("fall_flying"), client -> RollableClient.isFallFlying());

    public static InputBindingSupplier PITCH_UP;
    public static InputBindingSupplier PITCH_DOWN;
    public static InputBindingSupplier ROLL_LEFT;
    public static InputBindingSupplier ROLL_RIGHT;
    public static InputBindingSupplier YAW_LEFT;
    public static InputBindingSupplier YAW_RIGHT;

    private RotationInstant applyToRotation(RotationInstant rotationDelta, RollContext context) {
        Optional<ControllerEntity> perhapsController = ControlifyApi.get().getCurrentController();
        if (perhapsController.isPresent()) {
            ControllerEntity controller = perhapsController.get();
            Sensitivity sensitivity = RollableClientConfig.INSTANCE.sensitivity.controller.getValue();

            if (PITCH_UP.on(controller) == null) return rotationDelta;

            double multiplier = context.getRenderDelta() * 1200;

            double pitchAxis = PITCH_DOWN.on(controller).analogueNow() - PITCH_UP.on(controller).analogueNow();
            double yawAxis = YAW_RIGHT.on(controller).analogueNow() - YAW_LEFT.on(controller).analogueNow();
            double rollAxis = ROLL_RIGHT.on(controller).analogueNow() - ROLL_LEFT.on(controller).analogueNow();

            pitchAxis *= multiplier * sensitivity.pitch;
            yawAxis *= multiplier * sensitivity.yaw;
            rollAxis *= multiplier * sensitivity.roll;

            return rotationDelta.add(pitchAxis, yawAxis, rollAxis);
        }

        return rotationDelta;
    }

    @Override
    public void onControlifyInit(ControlifyApi controlifyApi) {
        ControlifyBindApi bindings = ControlifyBindApi.get();
        bindings.registerBindContext(FALL_FLYING);

        PITCH_UP = bindings.registerBinding(builder -> builder
                .id(Rollable.id("pitch_up"))
                .category(Text.translatable(RollableKeybindings.PITCH_UP.getCategory()))
                .name(Text.translatable(RollableKeybindings.PITCH_UP.getTranslationKey()))
                .allowedContexts(FALL_FLYING, BindContext.IN_GAME)
                .addKeyCorrelation(RollableKeybindings.PITCH_UP)
        );
        PITCH_DOWN = bindings.registerBinding(builder -> builder
                .id(Rollable.id("pitch_down"))
                .category(Text.translatable(RollableKeybindings.PITCH_DOWN.getCategory()))
                .name(Text.translatable(RollableKeybindings.PITCH_DOWN.getTranslationKey()))
                .allowedContexts(FALL_FLYING, BindContext.IN_GAME)
                .addKeyCorrelation(RollableKeybindings.PITCH_DOWN)
        );
        ROLL_LEFT = bindings.registerBinding(builder -> builder
                .id(Rollable.id("roll_left"))
                .category(Text.translatable(RollableKeybindings.ROLL_LEFT.getCategory()))
                .name(Text.translatable(RollableKeybindings.ROLL_LEFT.getTranslationKey()))
                .allowedContexts(FALL_FLYING, BindContext.IN_GAME)
                .addKeyCorrelation(RollableKeybindings.ROLL_LEFT)
        );
        ROLL_RIGHT = bindings.registerBinding(builder -> builder
                .id(Rollable.id("roll_right"))
                .category(Text.translatable(RollableKeybindings.ROLL_RIGHT.getCategory()))
                .name(Text.translatable(RollableKeybindings.ROLL_RIGHT.getTranslationKey()))
                .allowedContexts(FALL_FLYING, BindContext.IN_GAME)
                .addKeyCorrelation(RollableKeybindings.ROLL_RIGHT)
        );
        YAW_LEFT = bindings.registerBinding(builder -> builder
                .id(Rollable.id("yaw_left"))
                .category(Text.translatable(RollableKeybindings.YAW_LEFT.getCategory()))
                .name(Text.translatable(RollableKeybindings.YAW_LEFT.getTranslationKey()))
                .allowedContexts(FALL_FLYING, BindContext.IN_GAME)
                .addKeyCorrelation(RollableKeybindings.YAW_LEFT)
        );
        YAW_RIGHT = bindings.registerBinding(builder -> builder
                .id(Rollable.id("yaw_right"))
                .category(Text.translatable(RollableKeybindings.YAW_RIGHT.getCategory()))
                .name(Text.translatable(RollableKeybindings.YAW_RIGHT.getTranslationKey()))
                .allowedContexts(FALL_FLYING, BindContext.IN_GAME)
                .addKeyCorrelation(RollableKeybindings.YAW_RIGHT)
        );

        RollEvents.LATE_CAMERA_MODIFIERS.register(context -> context.useModifier(this::applyToRotation), RollableClient::isFallFlying);

        ControlifyEvents.LOOK_INPUT_MODIFIER.register(event -> {
            if (RollableClient.isFallFlying()) event.lookInput().zero();
        });
    }

    @Override
    public void onControllersDiscovered(ControlifyApi controlifyApi) {
    }
}
