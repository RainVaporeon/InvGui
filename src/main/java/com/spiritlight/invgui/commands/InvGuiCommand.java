package com.spiritlight.invgui.commands;

import com.spiritlight.invgui.Main;
import com.spiritlight.invgui.interfaces.annotations.AutoRegister;
import com.spiritlight.invgui.message;
import com.spiritlight.invgui.utils.SpiritCommand;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@AutoRegister(permission = 0, requirePrefix = true)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class InvGuiCommand extends SpiritCommand {
    @Override
    public String getName() {
        return "invgui";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("ig");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + getName();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) {
            message.send("/invgui toggle|hidemods");
            return;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "toggle":
                Main.enabled = !Main.enabled;
                message.send("Toggled enable status to " + Main.enabled);
                break;
            case "hidemods":
                if(args.length == 1) {
                    message.send("/invgui hidemods [on|off|self]");
                    message.send("self: hides invgui from server.");
                    message.send("Note: this preference does not save.");
                    return;
                }
                switch(args[1].toLowerCase(Locale.ROOT)) {
                    case "on":
                        message.send("Changed hide mods to on");
                        Main.hideStatus = Main.Hide.ON;
                        return;
                    case "off":
                        message.send("Changed hide mods to off");
                        Main.hideStatus = Main.Hide.OFF;
                        return;
                    case "self":
                        message.send("Changed hide mods to self");
                        Main.hideStatus = Main.Hide.SELF;
                        return;
                }
                message.send("/invgui hidemods");
                break;
            default:
                message.send("/invgui toggle");
                break;
        }
    }
}
