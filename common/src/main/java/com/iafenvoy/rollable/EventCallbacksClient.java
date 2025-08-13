package com.iafenvoy.rollable;

import com.iafenvoy.rollable.impl.key.InputContextImpl;
import net.minecraft.client.MinecraftClient;

public class EventCallbacksClient {
    public static void clientTick(MinecraftClient client) {
        InputContextImpl.getContexts().forEach(InputContextImpl::tick);
        if (!RollableClient.isFallFlying()) RollableClient.clearValues();
        ModKeybindings.clientTick(client);
    }
}
