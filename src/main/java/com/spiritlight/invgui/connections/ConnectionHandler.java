package com.spiritlight.invgui.connections;

import com.spiritlight.invgui.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.Proxy;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectionHandler {
    private static Socket socket;
    private static String ip;
    private static short port;
    private static final Pattern PORT_PATTERN = Pattern.compile("(:[0-9])\\w+");
    private static String lastIp;
    private static boolean disconnectWarning = true;
    private static final AtomicBoolean isSocketConnected = new AtomicBoolean(false);

    private ConnectionHandler() {
        throw new AssertionError("You cannot. (Instantiation of com.spiritlight.invgui.connections.ConnectionHandler)");
    }
    /**
     * Checks if a valid connection is made (Socket connected)<br>
     * <br>
     * One may safely assume that {@link ConnectionHandler#getIp()} and {@link ConnectionHandler#getPort()} is non-null
     * if this returns {@code true}.
     * @return Whether a connection is valid.
     */
    public static boolean isConnected() {
        return isSocketConnected.get();
    }

    @Nullable
    public static INetHandlerPlayClient getConnection() {
        return Minecraft.getMinecraft().getConnection();
    }

    public static Proxy getProxy() {
        return Minecraft.getMinecraft().getProxy();
    }

    /**
     * Returns an already-connected socket, connected to the current server.
     * @apiNote <b>Do not</b> attempt to create another connection with this socket!
     * @return A {@link Socket} which is connected to the current server, or {@code null} if none connected.
     */
    @Nullable
    public static Socket getSocket() {
        return socket;
    }

    @Nullable
    public static String getIp() {
        return ip;
    }

    public static int getPort() {
        return Short.toUnsignedInt(port);
    }

    protected static void newConnection() {
        ServerData data = Minecraft.getMinecraft().getCurrentServerData();
        if (data == null) {
            System.out.println("newConnection() called with invalid server");
            return;
        }
        String ip = data.serverIP;
        Matcher matcher = PORT_PATTERN.matcher(ip);
        // Detecting the connected port, 25565 (default) if not found
        String sPort = (matcher.find() ? matcher.group(0).substring(1) : "25565"); // :12345
        short port = Short.parseShort(sPort);
        // Detecting whether the player joined in a server with port
        boolean flag = ip.lastIndexOf(':') == -1;
        ip = (flag ? ip : ip.substring(0, ip.lastIndexOf(':'))); // 127.0.0.1
        ConnectionHandler.ip = ip;
        ConnectionHandler.port = port;
        disconnectWarning = !(ip.equals(lastIp));
        System.out.println("INFO: IP: " + ip + ", Port: " + port + ", ServerIP: " + data.serverIP);
        try {
            ConnectionHandler.socket = new Socket(ip, getPort());
            isSocketConnected.set(true);
        } catch (IOException e) {
            System.err.println("An exception has occurred whilst trying to create a new connection!");
            System.err.println("Dump IP: " + ip + ", Port: " + port + ", Error:");
            e.printStackTrace();
            disconnect(true);
        }
    }

    public static void disconnect(INetHandlerPlayClient client, ITextComponent message) {
        if(client == null) {
            LogManager.getLogger(Main.MODID).warn("client passed as null on ConnectionHandler#disconnect!");
            return;
        }
        ITextComponent component;
        if(message == null) {
            component = new TextComponentString(TextFormatting.RED +
                    "InvGui failed to initialize a connection!\n" + TextFormatting.YELLOW +
                    "Re-join to ignore this error.");
        } else {
            component = message;
        }
        System.out.println("Attempting disconnection with message " + component.getUnformattedText());
        // Constructs a packet for self
        SPacketDisconnect packet = new SPacketDisconnect(component);
        client.handleDisconnect(packet);
    }

    /**
     * Internal uses, handles disconnection.
     * @param abnormal Whether this disconnection is abnormal.
     */
    protected static void disconnect(boolean abnormal) {
        isSocketConnected.set(false);
        lastIp = ip;
        ip = null;
        port = '\0';
        socket = null;
        if(disconnectWarning && abnormal) {
            disconnectWarning = false;
            disconnect(getConnection(), null);
        }
    }
}
