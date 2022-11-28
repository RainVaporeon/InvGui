package com.spiritlight.invgui.commands;

import com.spiritlight.invgui.interfaces.annotations.AutoRegister;
import com.spiritlight.invgui.message;
import com.spiritlight.invgui.utils.PlayerUtils;
import com.spiritlight.invgui.utils.SpiritCommand;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@AutoRegister(permission = 0, requirePrefix = true)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SpamCommand extends SpiritCommand {
    @Override
    public String getName() {
        return "spam";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) {
            getHelp();
            return;
        }
        int spamCnt;
        try {
            spamCnt = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            message.send("Cannot parse number " + args[0]);
            return;
        }
        String s = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        for(int i = 0; i < spamCnt; i++) {
            PlayerUtils.getPlayer().sendChatMessage(s);
        }
    }

    public void getHelp() {
        message.send("/" + getName() + " <spamCount> <message|command>");
    }
}
