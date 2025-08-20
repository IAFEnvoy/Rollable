package com.iafenvoy.rollable;

import com.iafenvoy.jupiter.render.screen.ClientConfigScreen;
import com.iafenvoy.rollable.config.RollableClientConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class RollableKeybindings {
    private static final String CATEGORY_MAIN = "category.%s.%s".formatted(Rollable.MOD_ID, Rollable.MOD_ID);

    private static String format(String key) {
        return "key.%s.%s".formatted(Rollable.MOD_ID, key);
    }

    public static final KeyBinding OPEN_CONFIG = new KeyBinding(format("open_config"), InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), CATEGORY_MAIN);
    public static final KeyBinding TOGGLE_ENABLE_ELYTRA = new KeyBinding(format("toggle_enable_elytra"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, CATEGORY_MAIN);
    public static final KeyBinding TOGGLE_ENABLE_SWIMMING = new KeyBinding(format("toggle_enable_swimming"), GLFW.GLFW_KEY_O, CATEGORY_MAIN);
    public static final KeyBinding PITCH_UP = new KeyBinding(format("pitch_up"), InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), CATEGORY_MAIN);
    public static final KeyBinding PITCH_DOWN = new KeyBinding(format("pitch_down"), InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), CATEGORY_MAIN);
    public static final KeyBinding YAW_LEFT = new KeyBinding(format("yaw_left"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_A, CATEGORY_MAIN);
    public static final KeyBinding YAW_RIGHT = new KeyBinding(format("yaw_right"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_D, CATEGORY_MAIN);
    public static final KeyBinding ROLL_LEFT = new KeyBinding(format("roll_left"), InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), CATEGORY_MAIN);
    public static final KeyBinding ROLL_RIGHT = new KeyBinding(format("roll_right"), InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), CATEGORY_MAIN);

    public static final List<KeyBinding> ALL = List.of(
            OPEN_CONFIG,
            TOGGLE_ENABLE_ELYTRA,
            TOGGLE_ENABLE_SWIMMING,
            PITCH_UP,
            PITCH_DOWN,
            YAW_LEFT,
            YAW_RIGHT,
            ROLL_LEFT,
            ROLL_RIGHT
    );

    public static void clientTick(MinecraftClient client) {
        while (OPEN_CONFIG.wasPressed())
            client.setScreen(new ClientConfigScreen(client.currentScreen, RollableClientConfig.INSTANCE));
        while (TOGGLE_ENABLE_ELYTRA.wasPressed()) {
            boolean b = !RollableClientConfig.INSTANCE.generals.enabled.getValue();
            RollableClientConfig.INSTANCE.generals.enabled.setValue(b);
            RollableClientConfig.INSTANCE.save();
            if (client.player != null)
                client.player.sendMessage(Text.translatable("key.%s.%s".formatted(Rollable.MOD_ID, b ? "toggle_enabled.enable" : "toggle_enabled.disable")), true);
        }
        while (TOGGLE_ENABLE_SWIMMING.wasPressed()) {
            boolean enable = !RollableClientConfig.INSTANCE.swim.enabled.getValue();
            RollableClientConfig.INSTANCE.swim.enabled.setValue(enable);
            RollableClientConfig.INSTANCE.save();
            if (client.player != null)
                client.player.sendMessage(Text.translatable("key.rolling_down_in_the_deep." + (enable ? "toggle_enabled.enable" : "toggle_enabled.disable")), true);
        }
    }
}
