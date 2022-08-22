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
            message.send("/invgui toggle");
            return;
        }
        if ("toggle".equals(args[0].toLowerCase(Locale.ROOT))) {
            Main.enabled = !Main.enabled;
            message.send("Toggled enable status to " + Main.enabled);
        } else {
            message.send("/invgui toggle");
        }
    }
}
