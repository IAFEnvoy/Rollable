package com.iafenvoy.rollable.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.iafenvoy.rollable.ModKeybindings;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WhyIsTherePublicTransportationInThisModLoaderClientMod {
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        ModKeybindings.ALL.forEach(event::register);
    }
}
