package com.spiritlight.invgui;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

import java.util.*;

import static com.spiritlight.invgui.PacketHandler.Enum.READ;
import static com.spiritlight.invgui.PacketHandler.Enum.WRITE;

public class PacketHandler extends ChannelDuplexHandler {
    private static final List<String> discardPacketsR = new ArrayList<>();
    private static final List<String> discardPacketsW = new ArrayList<>();
    private static boolean packetReceiving = true;
    private static final LinkedList<Packet<? extends INetHandler>> queuedPackets = new LinkedList<>();

    public enum Enum {
        READ, WRITE;
    }

    @Override // Receive packet
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
        if(obj instanceof Packet && Main.enabled) {
            final List<String> dpr = new ArrayList<>(discardPacketsR);
            for(String s : dpr) {
                if(obj.getClass().getName().contains(s)) {
                    discardPacketsR.remove(s);
                    return;
                }
            }
        }
        super.channelRead(ctx, obj);
    }

    @Override // Send packet
    public void write(ChannelHandlerContext ctx, Object obj, ChannelPromise promise) throws Exception {
        if(!packetReceiving && obj instanceof Packet) {
            queuedPackets.add((Packet<? extends INetHandler>)obj);
            return;
        }
        if(obj instanceof Packet && Main.enabled) {
            final List<String> dpw = new ArrayList<>(discardPacketsW);
                for(String s : dpw) {
                    if(obj.getClass().getName().contains(s)) {
                        discardPacketsW.remove(s);
                        return;
                    }
                }
            }
        super.write(ctx, obj, promise);
    }

    public static boolean isPacketReceiving() {
        return packetReceiving;
    }

    private static void sendPausedPackets() {
        for (int size = queuedPackets.size(), i = 0; i < size; ++i) {
            Minecraft.getMinecraft().getConnection().getNetworkManager().sendPacket(queuedPackets.getFirst());
            queuedPackets.removeFirst();
        }
    }

    /**
     * Discards all paused packets.
     * @return A List of packets that have been discarded.
     */
    protected static List<Packet<? extends INetHandler>> discardPausedPackets() {
        List<Packet<? extends INetHandler>> ret = new LinkedList<>(queuedPackets);
        queuedPackets.clear();
        return ret;
    }

    public static void setPacketReceiving(boolean b) {
        if(!packetReceiving && b) {
            packetReceiving = true;
            sendPausedPackets();
            return;
        }
        packetReceiving = b;
    }

    public static boolean discardPacket(String packetName, Enum type) {
        // true = operation success; otherwise fail
        if(!packetName.contains("Packet")) return false;
        try {
            switch (type) {
                case READ:
                    discardPacketsR.add(packetName);
                    break;
                case WRITE:
                    discardPacketsW.add(packetName);
                    break;
                default:
                    return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Clears all handling packets
     * @return A {@link Map} of <{@link Enum}, {@link List<String>}> of packet that has not been processed yet.
     */
    protected static Map<Enum, List<String>> clear() {
        final Map<Enum, List<String>> ret = new HashMap<Enum, List<String>>() {{
            put(READ, new ArrayList<>(discardPacketsR));
            put(WRITE, new ArrayList<>(discardPacketsW));
        }};
        discardPacketsR.clear();
        discardPacketsW.clear();
        return ret;
    }
}
