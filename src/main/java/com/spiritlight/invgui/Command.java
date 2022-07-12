package com.spiritlight.invgui;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault @MethodsReturnNonnullByDefault
public class Command extends CommandBase {
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "packet";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + getName();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            send("/packet s/r <name>");
            return;
        }
        switch (args[0]) {
            case "s":
                if (args.length == 1) {
                    send("Input required.");
                    break;
                }
                try {
                    PacketHandler.discardPacket(args[1], PacketHandler.Enum.WRITE);
                    send("Will discard " + args[1] + " in the next upcoming packet.");
                } catch (IllegalArgumentException e) {
                    send(e.getMessage());
                }
                break;
            case "r":
                if (args.length == 1) {
                    send("Input required.");
                    break;
                }
                try {
                    PacketHandler.discardPacket(args[1], PacketHandler.Enum.READ);
                    send("Will discard " + args[1] + " in the next upcoming packet.");
                } catch (IllegalArgumentException e) {
                    send(e.getMessage());
                }
                break;
        }
    }
    
    static void send(String s) {
        if(Minecraft.getMinecraft().player == null) return;
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(s));
    }
}
