package com.iafenvoy.rollable.event;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

//TODO: new enable logic
public class RollGroup extends Event<RollGroup.RollCondition> implements BooleanSupplier {
    public static final HashMap<Identifier, RollGroup> instances = new HashMap<>();

    public RollGroup() {
        super(conditions -> () -> {
            for (RollCondition condition : conditions) {
                TriState result = condition.shouldRoll();
                if (result != TriState.PASS) {
                    return result;
                }
            }
            return TriState.FALSE;
        });
        RollEvents.SHOULD_ROLL.register(this::getAsBoolean);
    }

    public void trueIf(Supplier<Boolean> condition) {
        this.register(() -> condition.get() ? TriState.TRUE : TriState.PASS);
    }

    @Override
    public boolean getAsBoolean() {
        return this.invoker().shouldRoll() == TriState.TRUE;
    }

    public static RollGroup of(Identifier id) {
        return instances.computeIfAbsent(id, id2 -> new RollGroup());
    }

    interface RollCondition {
        TriState shouldRoll();
    }
}
