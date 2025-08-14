package com.iafenvoy.rollable.util.key;

import com.iafenvoy.rollable.util.InputContext;

import java.util.List;

public interface ContextualKeyBinding {
    void rollable$addToContext(InputContext context);

    List<InputContext> rollable$getContexts();
}
