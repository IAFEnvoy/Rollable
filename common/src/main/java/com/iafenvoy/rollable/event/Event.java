package com.iafenvoy.rollable.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public class Event<T> {
    private final List<Listener<T>> listeners = new ArrayList<>();
    private final Function<List<T>, T> provider;

    public Event(Function<List<T>, T> provider) {
        this.provider = provider;
    }

    public void register(T listener) {
        this.register(listener, () -> true);
    }

    public void register(T listener, BooleanSupplier enabled) {
        this.listeners.add(new Listener<>(listener, enabled));
    }

    public T invoker() {
        return this.provider.apply(this.listeners.stream().filter(x -> x.enabled.getAsBoolean()).map(Listener::listener).toList());
    }

    private record Listener<T>(T listener, BooleanSupplier enabled) {
    }
}
