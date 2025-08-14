package com.iafenvoy.rollable.forge;

import com.iafenvoy.jupiter.render.screen.ClientConfigScreen;
import com.iafenvoy.rollable.EventCallbacksClient;
import com.iafenvoy.rollable.ModKeybindings;
import com.iafenvoy.rollable.config.RollableClientConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RollableForgeClient {
    @SuppressWarnings("removal")
    @SubscribeEvent
    public static void onInit(FMLClientSetupEvent event) {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, parent) -> new ClientConfigScreen(parent, RollableClientConfig.INSTANCE)));
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        ModKeybindings.ALL.forEach(event::register);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void clientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) EventCallbacksClient.clientTick(MinecraftClient.getInstance());
        }
    }
}
