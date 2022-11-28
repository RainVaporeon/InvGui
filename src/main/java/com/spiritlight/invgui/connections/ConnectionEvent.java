package com.spiritlight.invgui.connections;

import com.spiritlight.invgui.Main;
import com.spiritlight.invgui.commands.CrashCommand;
import com.spiritlight.invgui.interfaces.annotations.AutoSubscribe;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.NoSuchElementException;

@AutoSubscribe
public class ConnectionEvent {
    @SubscribeEvent
    public void onServerConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        CrashCommand.setAgreed(false);
        event.getManager().channel().pipeline().addBefore("packet_handler", Main.MODID + "_packet_handler", new PacketHandler());
        System.out.println("Added packet handler to channel pipeline.");
        ConnectionHandler.newConnection();
    }

    @SubscribeEvent
    public void onServerDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        PacketHandler.clear();
        PacketHandler.discardPausedPackets();
        ConnectionHandler.disconnect(false);
        try {
            event.getManager().channel().pipeline().remove(Main.MODID + "_packet_handler");
        } catch (NoSuchElementException ignored) {} // Rare cases where one is disconnected mid-join
    }
}
