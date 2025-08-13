package com.iafenvoy.rollable.forge;

import com.iafenvoy.rollable.DoABarrelRoll;
import com.iafenvoy.rollable.DoABarrelRollClient;
import net.minecraft.util.Identifier;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;

@Mod(DoABarrelRoll.MOD_ID)
public class DoABarrelRollForge {
    public static final HashMap<Identifier, SimpleChannel> NETWORK_CHANNELS = new HashMap<>();

//    public static final EventNetworkChannel HANDSHAKE_CHANNEL = NetworkRegistry.newEventChannel(DoABarrelRoll.HANDSHAKE_CHANNEL, () -> "", s -> true, s -> true);
//    public static final EventNetworkChannel ROLL_CHANNEL = NetworkRegistry.newEventChannel(DoABarrelRoll.ROLL_CHANNEL, () -> "", s -> true, s -> true);

    public DoABarrelRollForge() {
        DoABarrelRoll.init();
//        HANDSHAKE_CHANNEL.addListener(this::onHandshakeServerPacket);
//        ROLL_CHANNEL.addListener(this::onRollServerPacket);

        if (FMLLoader.getDist().isClient()) {
            DoABarrelRollClient.init();
//            HANDSHAKE_CHANNEL.addListener(this::onHandshakeClientPacket);
//            ROLL_CHANNEL.addListener(this::onRollClientPacket);
        }

        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (minecraft, parent) -> ModConfigScreen.create(parent)
                )
        );
    }

//    private void onHandshakeServerPacket(NetworkEvent.ServerCustomPayloadEvent event) {
//        var reply = DoABarrelRoll.HANDSHAKE_SERVER.clientReplied(event.getSource().get().getSender().networkHandler, buf);
//        if (reply == HandshakeServer.HandshakeState.ACCEPTED) {
//            RollSyncServer.startListening(handler1);
//            ServerConfigUpdaterRegister.startListening(handler1);
//        } else if (reply == HandshakeServer.HandshakeState.RESEND) {
//            // Resending can happen when the client has a different protocol version than expected.
//            sendHandshake(player);
//        }
//    }
//
//    private void onHandshakeClientPacket(NetworkEvent.ClientCustomPayloadEvent event) {
//
//    }
//
//    private void onRollServerPacket(NetworkEvent.ServerCustomPayloadEvent event) {
//        event.getPayload()
//    }
//
//    private void onRollClientPacket(NetworkEvent.ClientCustomPayloadEvent event) {
//
//    }
}
