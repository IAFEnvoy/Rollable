package com.iafenvoy.rollable.config;

import com.iafenvoy.jupiter.config.container.AutoInitConfigContainer;
import com.iafenvoy.jupiter.config.entry.BooleanEntry;
import com.iafenvoy.jupiter.config.entry.DoubleEntry;
import com.iafenvoy.jupiter.config.entry.SeparatorEntry;
import com.iafenvoy.jupiter.interfaces.IConfigEntry;
import com.iafenvoy.rollable.Rollable;
import com.iafenvoy.rollable.config.entry.ExpressionParserEntry;
import com.iafenvoy.rollable.config.entry.RotateStateEntry;
import com.iafenvoy.rollable.expression.ExpressionParser;
import com.iafenvoy.rollable.flight.RotateState;
import net.minecraft.util.Identifier;

public class RollableClientConfig extends AutoInitConfigContainer {
    private static final String CONFIG_PATH = "./config/%s_client.json".formatted(Rollable.MOD_ID);
    public static final RollableClientConfig INSTANCE = new RollableClientConfig();
    public final Generals generals = new Generals();
    public final Banking banking = new Banking();
    public final SensitivityConfig sensitivity = new SensitivityConfig();
    public final AdvancedConfig advanced = new AdvancedConfig();

    public RollableClientConfig() {
        super(Identifier.of(Rollable.MOD_ID, "client"), "config.%s.client.title".formatted(Rollable.MOD_ID), CONFIG_PATH);
    }

    private static String format(String category, String jsonKey) {
        return "config.%s.%s.%s".formatted(Rollable.MOD_ID, category, jsonKey);
    }

    public static class Generals extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Boolean> enabled = new BooleanEntry(format("generals", "enabled"), true).json("enabled");
        public final SeparatorEntry s = new SeparatorEntry();
        public final IConfigEntry<Boolean> switchRollAndYaw = new BooleanEntry(format("generals", "switchRollAndYaw"), false).json("switchRollAndYaw");
        public final IConfigEntry<Boolean> invertPitch = new BooleanEntry(format("generals", "invertPitch"), false).json("invertPitch");
        public final IConfigEntry<Boolean> momentumBasedMouse = new BooleanEntry(format("generals", "momentumBasedMouse"), false).json("momentumBasedMouse");
        public final IConfigEntry<Double> momentumMouseDeadzone = new DoubleEntry(format("generals", "momentumMouseDeadzone"), 0.2, 0, Integer.MAX_VALUE).json("momentumMouseDeadzone");
        public final IConfigEntry<Boolean> disableWhenSubmerged = new BooleanEntry(format("generals", "disableWhenSubmerged"), true).json("disableWhenSubmerged");

        public Generals() {
            super("generals", format("generals", "title"));
        }
    }

    public static class Banking extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Boolean> enabled = new BooleanEntry(format("banking", "enabled"), true).json("enabled");
        public final IConfigEntry<Double> strength = new DoubleEntry(format("banking", "strength"), 20.0, 0, Integer.MAX_VALUE).json("strength");
        public final IConfigEntry<Boolean> simulateControlSurfaceEfficacy = new BooleanEntry(format("banking", "simulateControlSurfaceEfficacy"), false).json("simulateControlSurfaceEfficacy");
        public final IConfigEntry<Boolean> automaticRighting = new BooleanEntry(format("banking", "automaticRighting"), false).json("automaticRighting");
        public final IConfigEntry<Double> rightingStrength = new DoubleEntry(format("banking", "rightingStrength"), 50.0, 0, Integer.MAX_VALUE).json("rightingStrength");

        public Banking() {
            super("banking", format("banking", "title"));
        }
    }

    public static class SensitivityConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<RotateState> cameraSmoothing = new RotateStateEntry(format("advanced", "cameraSmoothing"), 1.0, 2.5, 1.0).json("cameraSmoothing");
        public final IConfigEntry<RotateState> desktop = new RotateStateEntry(format("advanced", "desktop"), 1, 0.4, 1).json("desktop");
        public final IConfigEntry<RotateState> controller = new RotateStateEntry(format("advanced", "controller"), 1, 0.4, 1).json("controller");

        public SensitivityConfig() {
            super("sensitivity", format("sensitivity", "title"));
        }
    }

    public static class AdvancedConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<ExpressionParser> bankingXFormula = new ExpressionParserEntry(format("advanced", "bankingXFormula"), "sin($roll * TO_RAD) * cos($pitch * TO_RAD) * 10 * $banking_strength").json("bankingXFormula");
        public final IConfigEntry<ExpressionParser> bankingYFormula = new ExpressionParserEntry(format("advanced", "bankingYFormula"), "(-1 + cos($roll * TO_RAD)) * cos($pitch * TO_RAD) * 10 * $banking_strength").json("bankingYFormula");
        public final IConfigEntry<ExpressionParser> elevatorEfficacyFormula = new ExpressionParserEntry(format("advanced", "elevatorEfficacyFormula"), "$velocity_x * $look_x + $velocity_y * $look_y + $velocity_z * $look_z").json("elevatorEfficacyFormula");
        public final IConfigEntry<ExpressionParser> aileronEfficacyFormula = new ExpressionParserEntry(format("advanced", "aileronEfficacyFormula"), "$velocity_x * $look_x + $velocity_y * $look_y + $velocity_z * $look_z").json("aileronEfficacyFormula");
        public final IConfigEntry<ExpressionParser> rudderEfficacyFormula = new ExpressionParserEntry(format("advanced", "rudderEfficacyFormula"), "$velocity_x * $look_x + $velocity_y * $look_y + $velocity_z * $look_z").json("rudderEfficacyFormula");

        public AdvancedConfig() {
            super("advanced", format("advanced", "title"));
        }
    }
}
