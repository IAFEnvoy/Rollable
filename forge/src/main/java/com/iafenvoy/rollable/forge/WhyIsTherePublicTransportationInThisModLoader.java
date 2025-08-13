package com.iafenvoy.rollable.forge;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionNode;

@Mod.EventBusSubscriber
public class WhyIsTherePublicTransportationInThisModLoader {
    @SubscribeEvent
    public static void gatherPermissions(PermissionGatherEvent.Nodes event) {
        event.addNodes(ModPermissions.NODES.toArray(new PermissionNode[0]));
    }
}
