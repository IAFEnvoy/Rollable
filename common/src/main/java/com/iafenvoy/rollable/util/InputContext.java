package com.iafenvoy.rollable.util;

import com.iafenvoy.rollable.util.key.ContextualKeyBinding;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;

public class InputContext {
    private final BooleanSupplier activeCondition;
    private final List<KeyBinding> keyBindings = new ReferenceArrayList<>();
    private final Map<InputUtil.Key, KeyBinding> bindingsByKey = new HashMap<>();
    private boolean active;

    public InputContext(BooleanSupplier activeCondition) {
        this.activeCondition = activeCondition;
    }

    public void tick() {
        boolean active = this.activeCondition.getAsBoolean();
        if (active != this.active) {
            this.active = active;
            KeyBinding.updatePressedStates();
        }
    }

    public boolean isActive() {
        return this.active;
    }

    public void addKeyBinding(KeyBinding keyBinding) {
        Objects.requireNonNull(keyBinding);
        this.keyBindings.add(keyBinding);
        ((ContextualKeyBinding) keyBinding).rollable$addToContext(this);
    }

    public List<KeyBinding> getKeyBindings() {
        return this.keyBindings;
    }

    public KeyBinding getKeyBinding(InputUtil.Key key) {
        return this.bindingsByKey.get(key);
    }

    public void updateKeysByCode() {
        this.bindingsByKey.clear();
        for (KeyBinding keyBinding : this.keyBindings) {
            //TODO
            this.bindingsByKey.put(keyBinding.getDefaultKey(), keyBinding);
        }
    }
}
