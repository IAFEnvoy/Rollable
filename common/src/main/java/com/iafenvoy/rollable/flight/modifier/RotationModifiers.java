package com.iafenvoy.rollable.flight.modifier;

import com.iafenvoy.rollable.RollableKeybindings;
import com.iafenvoy.rollable.config.RollableClientConfig;
import com.iafenvoy.rollable.expression.Expression;
import com.iafenvoy.rollable.flight.RollContext;
import com.iafenvoy.rollable.flight.RotateState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.SmoothUtil;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;

public class RotationModifiers {
    public static final double ROLL_REORIENT_CUTOFF = Math.sqrt(10.0 / 3.0);

    public static RotateState configureRotation(RotateState state, @SuppressWarnings("unused") RollContext context) {
        double pitch = state.pitch();
        double yaw = state.yaw();
        double roll = state.roll();
        if (!RollableClientConfig.INSTANCE.generals.switchRollAndYaw.getValue()) {
            double temp = yaw;
            yaw = roll;
            roll = temp;
        }
        if (RollableClientConfig.INSTANCE.generals.invertPitch.getValue()) pitch *= -1;
        return new RotateState(pitch, yaw, roll);
    }

    public static RollContext.ConfiguresRotation buttonControls(double power) {
        return (state, context) -> {
            double delta = power * context.getRenderDelta(), pitch = 0, yaw = 0, roll = 0;
            if (RollableKeybindings.PITCH_UP.isPressed()) pitch -= delta;
            if (RollableKeybindings.PITCH_DOWN.isPressed()) pitch += delta;
            if (RollableKeybindings.YAW_LEFT.isPressed()) yaw -= delta;
            if (RollableKeybindings.YAW_RIGHT.isPressed()) yaw += delta;
            if (RollableKeybindings.ROLL_LEFT.isPressed()) roll -= delta;
            if (RollableKeybindings.ROLL_RIGHT.isPressed()) roll += delta;
            return state.add(pitch, yaw, roll);
        };
    }

    public static RollContext.ConfiguresRotation smoothing(SmoothUtil pitchSmoother, SmoothUtil yawSmoother, SmoothUtil rollSmoother, RotateState smoothness) {
        return (state, context) -> new RotateState(
                smoothness.pitch() == 0 ? state.pitch() : pitchSmoother.smooth(state.pitch(), 1 / smoothness.pitch() * context.getRenderDelta()),
                smoothness.yaw() == 0 ? state.yaw() : yawSmoother.smooth(state.yaw(), 1 / smoothness.yaw() * context.getRenderDelta()),
                smoothness.roll() == 0 ? state.roll() : rollSmoother.smooth(state.roll(), 1 / smoothness.roll() * context.getRenderDelta()));
    }

    public static RotateState banking(RotateState state, RollContext context) {
        double delta = context.getRenderDelta();
        RotateState currentRotation = context.getCurrentRotation();
        double currentRoll = Math.toRadians(currentRotation.roll());

        Expression xExpression = RollableClientConfig.INSTANCE.advanced.bankingXFormula.getValue().getCompiledOrDefaulting(0);
        Expression yExpression = RollableClientConfig.INSTANCE.advanced.bankingYFormula.getValue().getCompiledOrDefaulting(0);

        Map<String, Double> vars = getVars(context);
        vars.put("banking_strength", RollableClientConfig.INSTANCE.banking.strength.getValue());

        double dX = xExpression.eval(vars);
        double dY = yExpression.eval(vars);

        // check if we accidentally got NaN, for some reason this happens sometimes
        if (Double.isNaN(dX)) dX = 0;
        if (Double.isNaN(dY)) dY = 0;

        return state.addAbsolute(dX * delta, dY * delta, currentRoll);
    }

    public static RotateState reorient(RotateState state, RollContext context) {
        double delta = context.getRenderDelta();
        double currentRoll = Math.toRadians(context.getCurrentRotation().roll());
        double strength = 10 * RollableClientConfig.INSTANCE.banking.rightingStrength.getValue();
        double cutoff = ROLL_REORIENT_CUTOFF;
        double rollDelta = 0;
        if (-cutoff < currentRoll && currentRoll < cutoff) rollDelta = -Math.pow(currentRoll, 3) / 3.0 + currentRoll;
        return state.add(0, 0, -rollDelta * strength * delta);
    }

    public static RotateState applyControlSurfaceEfficacy(RotateState state, RollContext context) {
        Expression elevatorExpression = RollableClientConfig.INSTANCE.advanced.elevatorEfficacyFormula.getValue().getCompiledOrDefaulting(1);
        Expression aileronExpression = RollableClientConfig.INSTANCE.advanced.aileronEfficacyFormula.getValue().getCompiledOrDefaulting(1);
        Expression rudderExpression = RollableClientConfig.INSTANCE.advanced.rudderEfficacyFormula.getValue().getCompiledOrDefaulting(1);
        Map<String, Double> vars = getVars(context);
        return state.multiply(elevatorExpression.eval(vars), rudderExpression.eval(vars), aileronExpression.eval(vars));
    }

    private static Map<String, Double> getVars(RollContext context) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        RotateState currentRotation = context.getCurrentRotation();
        Vec3d rotationVector = player.getRotationVector();
        return new HashMap<>() {{
            this.put("pitch", currentRotation.pitch());
            this.put("yaw", currentRotation.yaw());
            this.put("roll", currentRotation.roll());
            this.put("velocity_length", player.getVelocity().length());
            this.put("velocity_x", player.getVelocity().getX());
            this.put("velocity_y", player.getVelocity().getY());
            this.put("velocity_z", player.getVelocity().getZ());
            this.put("look_x", rotationVector.getX());
            this.put("look_y", rotationVector.getY());
            this.put("look_z", rotationVector.getZ());
        }};
    }
}
