package com.iafenvoy.rollable.util.key;

import com.iafenvoy.rollable.api.key.InputContext;

import java.util.List;

public interface ContextualKeyBinding {
    void doABarrelRoll$addToContext(InputContext context);

    List<InputContext> doABarrelRoll$getContexts();
}
