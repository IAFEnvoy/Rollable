package com.iafenvoy.rollable.forge;

import com.iafenvoy.rollable.EventCallbacksClient;
import net.minecraft.client.MinecraftClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class WhyIsTherePublicTransportationInThisModLoaderClient {
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            EventCallbacksClient.clientTick(MinecraftClient.getInstance());
        }
    }
}
