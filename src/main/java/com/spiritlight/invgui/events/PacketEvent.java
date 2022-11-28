package com.spiritlight.invgui.events;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.GenericEvent;

@Cancelable
public class PacketEvent<T extends Packet<?>> extends GenericEvent<T> {
    private T packet;

    @SuppressWarnings("unchecked")
    public PacketEvent(T packet) {
        super((Class<T>) packet.getClass());
        this.packet = packet;
    }

    public T getPacket() {
        return packet;
    }

    public void setPacket(T packet) {
        this.packet = packet;
    }


    /**
     * Event that is fired when this client receives a packet of any kind.
     */
    @Cancelable
    public static class Inbound<T extends Packet<?>> extends PacketEvent<T> {
        /**
         * Constructs an inbound packet event.
         * @param packet The packet that caused this event.
         */
        public Inbound(T packet) {
            super(packet);
        }
    }

    /**
     * Event that is fired when this client sends a packet of any kind.<br>
     * {@link com.spiritlight.invgui.connections.PacketHandler#fire(Packet, boolean)} may bypass this when force = true
     */
    @Cancelable
    public static class Outbound<T extends Packet<?>> extends PacketEvent<T> {
        /**
         * Constructs an outbound packet event.
         * @param packet The pack that caused this event.
         */
        public Outbound(T packet) {
            super(packet);
        }
    }
}
