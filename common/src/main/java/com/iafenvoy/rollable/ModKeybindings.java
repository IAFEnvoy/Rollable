package com.iafenvoy.rollable;

import com.iafenvoy.rollable.api.key.InputContext;
import com.iafenvoy.rollable.config.RollableClientConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ModKeybindings {
    public static final KeyBinding TOGGLE_ENABLED = new KeyBinding(
            "key.do_a_barrel_roll.toggle_enabled",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            "category.do_a_barrel_roll.do_a_barrel_roll"
    );
    public static final KeyBinding TOGGLE_THRUST = new KeyBinding(
            "key.do_a_barrel_roll.toggle_thrust",
            InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.getCode(),
            "category.do_a_barrel_roll.do_a_barrel_roll"
    );
    public static final KeyBinding OPEN_CONFIG = new KeyBinding(
            "key.do_a_barrel_roll.open_config",
            InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.getCode(),
            "category.do_a_barrel_roll.do_a_barrel_roll"
    );

    public static final KeyBinding PITCH_UP = new KeyBinding(
            "key.do_a_barrel_roll.pitch_up",
            InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.getCode(),
            "category.do_a_barrel_roll.do_a_barrel_roll.movement"
    );
    public static final KeyBinding PITCH_DOWN = new KeyBinding(
            "key.do_a_barrel_roll.pitch_down",
            InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.getCode(),
            "category.do_a_barrel_roll.do_a_barrel_roll.movement"
    );
    public static final KeyBinding YAW_LEFT = new KeyBinding(
            "key.do_a_barrel_roll.yaw_left",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_A,
            "category.do_a_barrel_roll.do_a_barrel_roll.movement"
    );
    public static final KeyBinding YAW_RIGHT = new KeyBinding(
            "key.do_a_barrel_roll.yaw_right",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_D,
            "category.do_a_barrel_roll.do_a_barrel_roll.movement"
    );
    public static final KeyBinding ROLL_LEFT = new KeyBinding(
            "key.do_a_barrel_roll.roll_left",
            InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.getCode(),
            "category.do_a_barrel_roll.do_a_barrel_roll.movement"
    );
    public static final KeyBinding ROLL_RIGHT = new KeyBinding(
            "key.do_a_barrel_roll.roll_right",
            InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.getCode(),
            "category.do_a_barrel_roll.do_a_barrel_roll.movement"
    );
    public static final KeyBinding THRUST_FORWARD = new KeyBinding(
            "key.do_a_barrel_roll.thrust_forward",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_W,
            "category.do_a_barrel_roll.do_a_barrel_roll.movement"
    );
    public static final KeyBinding THRUST_BACKWARD = new KeyBinding(
            "key.do_a_barrel_roll.thrust_backward",
            InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.getCode(),
            "category.do_a_barrel_roll.do_a_barrel_roll.movement"
    );

    public static final List<KeyBinding> ALL = List.of(
            TOGGLE_ENABLED,
            TOGGLE_THRUST,
            OPEN_CONFIG,
            PITCH_UP,
            PITCH_DOWN,
            YAW_LEFT,
            YAW_RIGHT,
            ROLL_LEFT,
            ROLL_RIGHT,
            THRUST_FORWARD,
            THRUST_BACKWARD
    );

    public static final InputContext CONTEXT = InputContext.of(
            DoABarrelRoll.id("fall_flying"),
            DoABarrelRollClient.FALL_FLYING_GROUP
    );

    static {
        CONTEXT.addKeyBinding(PITCH_UP);
        CONTEXT.addKeyBinding(PITCH_DOWN);
        CONTEXT.addKeyBinding(YAW_LEFT);
        CONTEXT.addKeyBinding(YAW_RIGHT);
        CONTEXT.addKeyBinding(ROLL_LEFT);
        CONTEXT.addKeyBinding(ROLL_RIGHT);
        CONTEXT.addKeyBinding(THRUST_FORWARD);
        CONTEXT.addKeyBinding(THRUST_BACKWARD);
    }

    public static void clientTick(MinecraftClient client) {
        while (TOGGLE_ENABLED.wasPressed()) {
            RollableClientConfig.INSTANCE.generals.enabled.setValue(!RollableClientConfig.INSTANCE.generals.enabled.getValue());
            RollableClientConfig.INSTANCE.save();

            if (client.player != null) {
                client.player.sendMessage(
                        Text.translatable(
                                "key.do_a_barrel_roll." +
                                        (RollableClientConfig.INSTANCE.generals.enabled.getValue() ? "toggle_enabled.enable" : "toggle_enabled.disable")
                        ),
                        true
                );
            }
        }
        while (TOGGLE_THRUST.wasPressed()) {
            if (client.player != null) {
                client.player.sendMessage(
                        Text.translatable("key.do_a_barrel_roll.toggle_thrust.disallowed"),
                        true
                );
            }
        }
        while (OPEN_CONFIG.wasPressed()) {
//            client.setScreen(ModConfigScreen.create(client.currentScreen));
        }
    }
}
