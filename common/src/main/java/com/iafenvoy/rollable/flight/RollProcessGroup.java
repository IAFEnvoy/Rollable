package com.iafenvoy.rollable.flight;

import com.iafenvoy.rollable.event.Event;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class RollProcessGroup {
    /**
     * If any listener returns true, roll will be unlocked.
     */
    public static final Event<Supplier<RollProcessGroup>> SHOULD_ROLL = new Event<>(listeners -> () -> listeners.stream().map(Supplier::get).filter(RollProcessGroup::enabled).findFirst().orElse(null));
    private static final Map<Identifier, RollProcessGroup> BY_ID = new HashMap<>();
    private final Identifier id;
    private final Event<BooleanSupplier> enabled = new Event<>(listeners -> () -> {
        for (BooleanSupplier listener : listeners)
            if (listener.getAsBoolean())
                return true;
        return false;
    });
    private final Event<Consumer<RollContext>> beforeModifier = new Event<>(listeners -> context -> listeners.forEach(x -> x.accept(context)));
    private final Event<Consumer<RollContext>> afterModifier = new Event<>(listeners -> context -> listeners.forEach(x -> x.accept(context)));

    private RollProcessGroup(Identifier id) {
        this.id = id;
        SHOULD_ROLL.register(() -> this);
    }

    public static RollProcessGroup get(Identifier id) {
        return BY_ID.computeIfAbsent(id, RollProcessGroup::new);
    }

    public Identifier getId() {
        return this.id;
    }

    public void registerEnablePredicate(BooleanSupplier predicate) {
        this.enabled.register(predicate);
    }

    public void registerBeforeModifier(Consumer<RollContext> modifier) {
        this.beforeModifier.register(modifier);
    }

    public void registerAfterModifier(Consumer<RollContext> modifier) {
        this.afterModifier.register(modifier);
    }

    public boolean enabled() {
        return this.enabled.invoker().getAsBoolean();
    }

    public void processBeforeModifier(RollContext context) {
        this.beforeModifier.invoker().accept(context);
    }

    public void processAfterModifier(RollContext context) {
        this.afterModifier.invoker().accept(context);
    }
}
