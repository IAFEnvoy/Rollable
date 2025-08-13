package com.iafenvoy.rollable.impl.event;

import com.iafenvoy.rollable.api.event.RollEvents;
import com.iafenvoy.rollable.api.event.RollGroup;
import com.iafenvoy.rollable.api.event.TriState;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.function.Supplier;

public class RollGroupImpl extends EventImpl<RollGroup.RollCondition> implements RollGroup {
    public static final HashMap<Identifier, RollGroup> instances = new HashMap<>();

    public RollGroupImpl() {
        RollEvents.SHOULD_ROLL_CHECK.register(this::get);
    }

    @Override
    public void trueIf(Supplier<Boolean> condition, int priority) {
        this.register(() -> condition.get() ? TriState.TRUE : TriState.PASS, priority);
    }

    @Override
    public void trueIf(Supplier<Boolean> condition) {
        this.trueIf(condition, 0);
    }

    @Override
    public void falseUnless(Supplier<Boolean> condition, int priority) {
        this.register(() -> condition.get() ? TriState.PASS : TriState.FALSE, priority);
    }

    @Override
    public void falseUnless(Supplier<Boolean> condition) {
        this.falseUnless(condition, 0);
    }

    @Override
    public Boolean get() {
        for (RollCondition condition : this.getListeners()) {
            TriState result = condition.shouldRoll();
            if (result != TriState.PASS) {
                return result == TriState.TRUE;
            }
        }
        return false;
    }
}
