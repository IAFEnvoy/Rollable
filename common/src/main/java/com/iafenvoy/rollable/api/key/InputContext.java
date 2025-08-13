package com.iafenvoy.rollable.api.key;

import com.iafenvoy.rollable.impl.key.InputContextImpl;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Supplier;

public interface InputContext {
    static InputContext of(Identifier id, Supplier<Boolean> activeCondition) {
        return new InputContextImpl(id, activeCondition);
    }

    Identifier getId();

    boolean isActive();

    void addKeyBinding(KeyBinding keyBinding);

    List<KeyBinding> getKeyBindings();

    KeyBinding getKeyBinding(InputUtil.Key key);

    void updateKeysByCode();
}
