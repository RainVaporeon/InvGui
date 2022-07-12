package com.spiritlight.invgui;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class ConnectionEvent {
    @SubscribeEvent
    public void onServerConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        event.getManager().channel().pipeline().addBefore("packet_handler", "spirit_packet_handler", new PacketHandler());
        System.out.println("Added packet handler to channel pipeline.");
    }

    @SubscribeEvent
    public void onServerDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        PacketHandler.clear();
        PacketHandler.discardPausedPackets();
        event.getManager().channel().pipeline().remove("spirit_packet_handler");
    }
}
