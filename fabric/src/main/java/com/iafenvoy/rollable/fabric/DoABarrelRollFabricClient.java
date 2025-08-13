package com.iafenvoy.rollable.fabric;

import com.iafenvoy.rollable.DoABarrelRollClient;
import com.iafenvoy.rollable.EventCallbacksClient;
import com.iafenvoy.rollable.ModKeybindings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class DoABarrelRollFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DoABarrelRollClient.init();

        ClientTickEvents.END_CLIENT_TICK.register(EventCallbacksClient::clientTick);

        // Register keybindings on fabric
        ModKeybindings.ALL.forEach(KeyBindingHelper::registerKeyBinding);
    }
}
