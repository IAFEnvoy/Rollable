package com.iafenvoy.rollable.impl.event;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.rollable.api.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class EventImpl<T> implements Event<T> {
    private final List<Entry> listeners = new ArrayList<>();

    @Override
    public void register(T listener) {
        this.listeners.add(new Entry(listener, 0, () -> true));
    }

    @Override
    public void register(T listener, int priority) {
        this.listeners.add(new Entry(listener, priority, () -> true));
    }

    @Override
    public void register(T listener, Supplier<Boolean> enabled) {
        this.listeners.add(new Entry(listener, 0, enabled));
    }

    @Override
    public void register(T listener, int priority, Supplier<Boolean> enabled) {
        this.listeners.add(new Entry(listener, priority, enabled));
    }

    @Override
    public void unregister(T listener) {
        this.listeners.removeIf(entry -> entry.listener == listener);
    }

    @Override
    public List<T> getListeners() {
        ImmutableList.Builder<T> result = ImmutableList.builder();

        this.listeners.sort(null);
        for (Entry entry : this.listeners) {
            if (entry.condition.get()) {
                result.add(entry.listener);
            }
        }

        return result.build();
    }

    private class Entry implements Comparable<Entry> {
        private final T listener;
        private final int index;
        private final Supplier<Boolean> condition;

        public Entry(T listener, int index, Supplier<Boolean> condition) {
            this.listener = listener;
            this.index = index;
            this.condition = condition;
        }

        public T getListener() {
            return this.listener;
        }

        public int getIndex() {
            return this.index;
        }

        public Supplier<Boolean> getCondition() {
            return this.condition;
        }

        @Override
        public int compareTo(@NotNull EventImpl<T>.Entry o) {
            return Integer.compare(this.index, o.index);
        }
    }
}
