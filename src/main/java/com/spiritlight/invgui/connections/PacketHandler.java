package com.spiritlight.invgui.connections;

import com.spiritlight.invgui.Main;
import com.spiritlight.invgui.events.PacketEvent;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;

import java.util.*;


public class PacketHandler extends ChannelDuplexHandler {
    private static final List<String> discardPacketsR = new ArrayList<>();
    private static final List<String> discardPacketsW = new ArrayList<>();
    private static boolean packetReceiving = true;
    private static final LinkedList<Packet<? extends INetHandler>> queuedPackets = new LinkedList<>();
    private static final List<Packet<? extends INetHandler>> forceSend = new LinkedList<>();

    PacketHandler() {}

    public enum Type {
        READ, WRITE
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
        if(obj instanceof Packet) {
            PacketEvent.Inbound<Packet<?>> event = new PacketEvent.Inbound<>((Packet<?>) obj);
            if(MinecraftForge.EVENT_BUS.post(event)) return;
            super.channelRead(ctx, event.getPacket());
            return;
        }
        super.channelRead(ctx, obj);
    }

    @Override // Send packet
    public void write(ChannelHandlerContext ctx, Object obj, ChannelPromise promise) throws Exception {
        if(obj instanceof Packet) {
            if(forceSend.contains(obj)) {
                forceSend.remove(obj);
                super.write(ctx, obj, promise);
                return;
            }
            if(!packetReceiving) {
                queuedPackets.add((Packet<? extends INetHandler>)obj);
                return;
            }
            if(Main.enabled) {
                final List<String> dpw = new ArrayList<>(discardPacketsW);
                for(String s : dpw) {
                    if(obj.getClass().getName().contains(s)) {
                        discardPacketsW.remove(s);
                        return;
                    }
                }
            }
            PacketEvent.Outbound<Packet<?>> event = new PacketEvent.Outbound<>((Packet<?>) obj);
            if(MinecraftForge.EVENT_BUS.post(event)) return;
            super.write(ctx, event.getPacket(), promise);
            return;
        }
        super.write(ctx, obj, promise);
    }

    public static boolean isPacketReceiving() {
        return packetReceiving;
    }

    private static void sendPausedPackets() {
        if(Minecraft.getMinecraft().getConnection() == null) {
            LogManager.getLogger(Main.MODID).warn("Cannot find a connection! Discarding all packets.");
            queuedPackets.clear();
            return;
        }
        for (int size = queuedPackets.size(), i = 0; i < size; ++i) {
            Minecraft.getMinecraft().getConnection().getNetworkManager().sendPacket(queuedPackets.getFirst());
            queuedPackets.removeFirst();
        }
    }

    /**
     * Discards all paused packets.
     */
    protected static void discardPausedPackets() {
        queuedPackets.clear();
    }

    public static void setPacketReceiving(boolean b) {
        if(!packetReceiving && b) {
            packetReceiving = true;
            sendPausedPackets();
            return;
        }
        packetReceiving = b;
    }

    public static boolean discardPacket(String packetName, Type type) {
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
     * @return A {@link Map} of <{@link Type}, {@link List<String>}> of packet that has not been processed yet.
     */
    protected static Map<Type, List<String>> clear() {
        final Map<Type, List<String>> ret = new HashMap<Type, List<String>>() {{
            put(Type.READ, new ArrayList<>(discardPacketsR));
            put(Type.WRITE, new ArrayList<>(discardPacketsW));
        }};
        discardPacketsR.clear();
        discardPacketsW.clear();
        forceSend.clear();
        return ret;
    }

    /**
     * Fires a packet. This will also cause a {@link PacketEvent} to be fired.<br>
     * <br>
     * In order to bypass the packet check, use {@link PacketHandler#fire(Packet, boolean)}.
     * @param packet The packet to be fired
     */
    public static void fire(Packet<? extends INetHandler> packet) {
        if(Minecraft.getMinecraft().getConnection() == null) return;
        Minecraft.getMinecraft().getConnection().sendPacket(packet);
    }

    /**
     * Forcibly fires a packet, ignoring blink status as well as cancellations.
     * @param packet The packet to be fired
     * @param force Always expected to be executed with force = true. Whether this packet should be sent with force.
     */
    public static void fire(Packet<? extends INetHandler> packet, boolean force) {
        if(force)
            forceSend.add(packet);
        fire(packet);
    }
}
