package com.iafenvoy.rollable.fabric;

import com.iafenvoy.rollable.RollableKeybindings;
import com.iafenvoy.rollable.RollableClient;
import com.iafenvoy.rollable.event.ClientEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;

public class RollableFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RollableClient.FLIGHT_ASSISTANT_LOADED = FabricLoader.getInstance().isModLoaded("flightassistant");
        RollableClient.init();
        ClientTickEvents.END_CLIENT_TICK.register(ClientEvents::clientTick);
        RollableKeybindings.ALL.forEach(KeyBindingHelper::registerKeyBinding);
    }
}
