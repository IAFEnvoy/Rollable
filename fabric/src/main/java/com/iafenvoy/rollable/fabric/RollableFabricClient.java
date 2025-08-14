package com.iafenvoy.rollable.fabric;

import com.iafenvoy.rollable.RollableKeybindings;
import com.iafenvoy.rollable.RollableClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class RollableFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RollableClient.init();
        ClientTickEvents.END_CLIENT_TICK.register(RollableClient::clientTick);
        RollableKeybindings.ALL.forEach(KeyBindingHelper::registerKeyBinding);
    }
}
