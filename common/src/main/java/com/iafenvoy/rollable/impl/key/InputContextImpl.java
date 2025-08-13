package com.iafenvoy.rollable.impl.key;

import com.iafenvoy.rollable.api.key.InputContext;
import com.iafenvoy.rollable.util.key.ContextualKeyBinding;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class InputContextImpl implements InputContext {
    private static final List<InputContextImpl> CONTEXTS = new ReferenceArrayList<>();

    public static List<InputContextImpl> getContexts() {
        return CONTEXTS;
    }

    public static boolean contextsContain(KeyBinding binding) {
        for (InputContextImpl context : InputContextImpl.getContexts()) {
            if (context.getKeyBindings().contains(binding)) {
                return true;
            }
        }

        return false;
    }

    private final Identifier id;
    private final Supplier<Boolean> activeCondition;
    private final List<KeyBinding> keyBindings = new ReferenceArrayList<>();
    private final Map<InputUtil.Key, KeyBinding> bindingsByKey = new HashMap<>();
    private boolean active;

    public InputContextImpl(Identifier id, Supplier<Boolean> activeCondition) {
        this.id = id;
        this.activeCondition = activeCondition;
        CONTEXTS.add(this);
    }

    public void tick() {
        boolean active = this.activeCondition.get();
        if (active != this.active) {
            this.active = active;
            KeyBinding.updatePressedStates();
        }
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void addKeyBinding(KeyBinding keyBinding) {
        Objects.requireNonNull(keyBinding);
        this.keyBindings.add(keyBinding);
        ((ContextualKeyBinding) keyBinding).doABarrelRoll$addToContext(this);
    }

    @Override
    public List<KeyBinding> getKeyBindings() {
        return this.keyBindings;
    }

    @Override
    public KeyBinding getKeyBinding(InputUtil.Key key) {
        return this.bindingsByKey.get(key);
    }

    @Override
    public void updateKeysByCode() {
        this.bindingsByKey.clear();
        for (KeyBinding keyBinding : this.keyBindings) {
            //TODO
            this.bindingsByKey.put(keyBinding.getDefaultKey(), keyBinding);
        }
    }
}
