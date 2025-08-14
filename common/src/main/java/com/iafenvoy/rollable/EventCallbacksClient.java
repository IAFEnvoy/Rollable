package com.iafenvoy.rollable;

import net.minecraft.client.MinecraftClient;

public class EventCallbacksClient {
    public static void clientTick(MinecraftClient client) {
        ModKeybindings.CONTEXT.tick();
        if (!RollableClient.isFallFlying()) RollableClient.clearValues();
        ModKeybindings.clientTick(client);
    }
}
