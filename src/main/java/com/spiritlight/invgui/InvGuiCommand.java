package com.spiritlight.invgui;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;

@MethodsReturnNonnullByDefault @ParametersAreNonnullByDefault
public class InvGuiCommand extends CommandBase {

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "invgui";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + getName();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) {
            message.send("/invgui toggle|load args..");
            return;
        }
        switch(args[0].toLowerCase(Locale.ROOT)) {
            case "toggle":
                Main.enabled = !Main.enabled;
                message.send("Toggled enable status to " + Main.enabled);
                break;
            case "load":
                if(args.length == 1) {
                    message.send("/invgui load #{1..3}");
                    return;
                }
                try {
                    int i = Integer.parseInt(args[1]);
                    boolean b = GuiHandler.loadGui(i);
                    if(b) {
                        message.send("Loaded Gui");
                    } else {
                        message.send("Failed to load Gui");
                    }
                } catch (NumberFormatException ex) {
                    message.send("Invalid operation");
                    return;
                }
                break;
            default:
                message.send("/invgui toggle|load args..");
        }
    }
}
