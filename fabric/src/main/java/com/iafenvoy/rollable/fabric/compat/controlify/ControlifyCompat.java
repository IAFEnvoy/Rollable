package com.iafenvoy.rollable.fabric.compat.controlify;

import com.iafenvoy.rollable.config.RollableClientConfig;
import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.bind.ControlifyBindApi;
import dev.isxander.controlify.api.bind.InputBindingSupplier;
import dev.isxander.controlify.api.entrypoint.ControlifyEntrypoint;
import dev.isxander.controlify.api.event.ControlifyEvents;
import dev.isxander.controlify.bindings.BindContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import com.iafenvoy.rollable.DoABarrelRoll;
import com.iafenvoy.rollable.DoABarrelRollClient;
import com.iafenvoy.rollable.ModKeybindings;
import com.iafenvoy.rollable.api.event.RollContext;
import com.iafenvoy.rollable.api.event.RollEvents;
import com.iafenvoy.rollable.api.event.ThrustEvents;
import com.iafenvoy.rollable.api.rotation.RotationInstant;

public class ControlifyCompat implements ControlifyEntrypoint {
    public static final BindContext FALL_FLYING = new BindContext(
            DoABarrelRoll.id("fall_flying"),
            mc -> DoABarrelRollClient.isFallFlying()
    );

    public static InputBindingSupplier PITCH_UP;
    public static InputBindingSupplier PITCH_DOWN;
    public static InputBindingSupplier ROLL_LEFT;
    public static InputBindingSupplier ROLL_RIGHT;
    public static InputBindingSupplier YAW_LEFT;
    public static InputBindingSupplier YAW_RIGHT;
    public static InputBindingSupplier THRUST_FORWARD;
    public static InputBindingSupplier THRUST_BACKWARD;

    private RotationInstant applyToRotation(RotationInstant rotationDelta, RollContext context) {
        var perhapsController = ControlifyApi.get().getCurrentController();
        if (perhapsController.isPresent()) {
            var controller = perhapsController.get();
            var sensitivity = RollableClientConfig.INSTANCE.sensitivity.controller.getValue();

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

    public static double getThrustModifier() {
        if (ControlifyApi.get().getCurrentController().isEmpty()) {
            return 0;
        }
        var controller = ControlifyApi.get().getCurrentController().get();

        float forward = THRUST_FORWARD.on(controller).analogueNow();
        float backward = THRUST_BACKWARD.on(controller).analogueNow();
        return forward - backward;
    }

    public static RotationInstant manageThrottle(RotationInstant rotationInstant, RollContext context) {
        var delta = context.getRenderDelta();

        DoABarrelRollClient.throttle += getThrustModifier() * delta;
        DoABarrelRollClient.throttle = MathHelper.clamp(DoABarrelRollClient.throttle, 0, RollableClientConfig.INSTANCE.thrust.max.getValue());

        return rotationInstant;
    }

    @Override
    public void onControlifyPreInit(ControlifyApi controlifyApi) {
        var bindings = ControlifyBindApi.get();
        bindings.registerBindContext(FALL_FLYING);

        PITCH_UP = bindings.registerBinding(builder -> builder
                .id(DoABarrelRoll.id("pitch_up"))
                .category(Text.translatable("controlify.category.do_a_barrel_roll.do_a_barrel_roll"))
                .name(Text.translatable("controlify.bind.do_a_barrel_roll.pitch_up"))
                .allowedContexts(FALL_FLYING, BindContext.IN_GAME)
                .addKeyCorrelation(ModKeybindings.PITCH_UP)
        );
        PITCH_DOWN = bindings.registerBinding(builder -> builder
                .id(DoABarrelRoll.id("pitch_down"))
                .category(Text.translatable("controlify.category.do_a_barrel_roll.do_a_barrel_roll"))
                .name(Text.translatable("controlify.bind.do_a_barrel_roll.pitch_down"))
                .allowedContexts(FALL_FLYING, BindContext.IN_GAME)
                .addKeyCorrelation(ModKeybindings.PITCH_DOWN)
        );
        ROLL_LEFT = bindings.registerBinding(builder -> builder
                .id(DoABarrelRoll.id("roll_left"))
                .category(Text.translatable("controlify.category.do_a_barrel_roll.do_a_barrel_roll"))
                .name(Text.translatable("controlify.bind.do_a_barrel_roll.roll_left"))
                .allowedContexts(FALL_FLYING, BindContext.IN_GAME)
                .addKeyCorrelation(ModKeybindings.ROLL_LEFT)
        );
        ROLL_RIGHT = bindings.registerBinding(builder -> builder
                .id(DoABarrelRoll.id("roll_right"))
                .category(Text.translatable("controlify.category.do_a_barrel_roll.do_a_barrel_roll"))
                .name(Text.translatable("controlify.bind.do_a_barrel_roll.roll_right"))
                .allowedContexts(FALL_FLYING, BindContext.IN_GAME)
                .addKeyCorrelation(ModKeybindings.ROLL_RIGHT)
        );
        YAW_LEFT = bindings.registerBinding(builder -> builder
                .id(DoABarrelRoll.id("yaw_left"))
                .category(Text.translatable("controlify.category.do_a_barrel_roll.do_a_barrel_roll"))
                .name(Text.translatable("controlify.bind.do_a_barrel_roll.yaw_left"))
                .allowedContexts(FALL_FLYING, BindContext.IN_GAME)
                .addKeyCorrelation(ModKeybindings.YAW_LEFT)
        );
        YAW_RIGHT = bindings.registerBinding(builder -> builder
                .id(DoABarrelRoll.id("yaw_right"))
                .category(Text.translatable("controlify.category.do_a_barrel_roll.do_a_barrel_roll"))
                .name(Text.translatable("controlify.bind.do_a_barrel_roll.yaw_right"))
                .allowedContexts(FALL_FLYING, BindContext.IN_GAME)
                .addKeyCorrelation(ModKeybindings.YAW_RIGHT)
        );
        THRUST_FORWARD = bindings.registerBinding(builder -> builder
                .id(DoABarrelRoll.id("thrust_forward"))
                .category(Text.translatable("controlify.category.do_a_barrel_roll.do_a_barrel_roll"))
                .name(Text.translatable("controlify.bind.do_a_barrel_roll.thrust_forward"))
                .allowedContexts(FALL_FLYING, BindContext.IN_GAME)
                .addKeyCorrelation(ModKeybindings.THRUST_FORWARD)
        );
        THRUST_BACKWARD = bindings.registerBinding(builder -> builder
                .id(DoABarrelRoll.id("thrust_backward"))
                .category(Text.translatable("controlify.category.do_a_barrel_roll.do_a_barrel_roll"))
                .name(Text.translatable("controlify.bind.do_a_barrel_roll.thrust_backward"))
                .allowedContexts(FALL_FLYING, BindContext.IN_GAME)
                .addKeyCorrelation(ModKeybindings.THRUST_BACKWARD)
        );

        RollEvents.EARLY_CAMERA_MODIFIERS.register(context -> context
                .useModifier(ControlifyCompat::manageThrottle, () -> RollableClientConfig.INSTANCE.thrust.enabled.getValue()),
                8, DoABarrelRollClient::isFallFlying);
        RollEvents.LATE_CAMERA_MODIFIERS.register(context -> context
                .useModifier(this::applyToRotation),
                5, DoABarrelRollClient::isFallFlying);

        ThrustEvents.MODIFY_THRUST_INPUT.register(input -> input + getThrustModifier());

        ControlifyEvents.LOOK_INPUT_MODIFIER.register(event -> {
            if (DoABarrelRollClient.isFallFlying()) {
                event.lookInput().zero();
            }
        });
    }

    @Override
    public void onControllersDiscovered(ControlifyApi controlifyApi) {
    }
}
