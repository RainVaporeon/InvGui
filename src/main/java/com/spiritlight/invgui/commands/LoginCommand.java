package com.spiritlight.invgui.commands;

import com.spiritlight.invgui.Main;
import com.spiritlight.invgui.configs.Configurations;
import com.spiritlight.invgui.connections.ConnectionHandler;
import com.spiritlight.invgui.exceptions.InvalidCredentialException;
import com.spiritlight.invgui.interfaces.annotations.AutoRegister;
import com.spiritlight.invgui.message;
import com.spiritlight.invgui.utils.PlayerUtils;
import com.spiritlight.invgui.utils.SessionUtils;
import com.spiritlight.invgui.utils.SpiritCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Session;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@AutoRegister(name = "spiritauth", aliases = {"slogin", "sauth"}, permission = 0, requirePrefix = true)
public class LoginCommand extends SpiritCommand {
    private final ITextComponent disconnectMessage = new TextComponentString(
            TextFormatting.GOLD + "[" + TextFormatting.YELLOW + "!" + TextFormatting.GOLD + "]\n" +
                    TextFormatting.GREEN + "Your Session has been changed.\n"
            + TextFormatting.GREEN + "Please re-login in order for changes to apply."
    );

    private static String token = "";
    private static String uuid = "";
    private static String name = "";

    @Override @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) {
            getHelp(sender);
            return;
        }
        boolean flag = args.length == 2;
        switch(args[0].toLowerCase(Locale.ROOT)) {
            case "password":
                if(flag) {
                    try {
                        Main.cachedPassword = Long.parseLong(args[1]);
                        sender.sendMessage(new TextComponentString("Password set. You'll need to use this password to retrieve your accounts."));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(new TextComponentString("Password has to be consisted of only numeric characters."));
                    }
                } else {
                    sender.sendMessage(new TextComponentString("You need to supply a password to enter."));
                }
                break;
            case "list":
                try {
                    decode();
                } catch (InvalidCredentialException e) {
                    sender.sendMessage(new TextComponentString("Invalid password."));
                    return;
                }
                for(Map.Entry<String, Session> m : Main.sessionMap.entrySet()) {
                    sender.sendMessage(new TextComponentString("Session Name: " + m.getKey()));
                }
                sender.sendMessage(new TextComponentString("Login via: /" + getName() + " login <username>"));
                break;
            case "remove":
                if (flag) {
                    Session s = Main.sessionMap.get(args[1]);
                    if(s == null) {
                        sender.sendMessage(new TextComponentString("This session is either not found, or not unlocked yet."));
                        return;
                    }
                    if(Main.accountHash.remove(SessionUtils.encrypt(s, Main.cachedPassword))) {
                        sender.sendMessage(new TextComponentString("Successfully removed " + args[1] + "!"));
                    } else {
                        sender.sendMessage(new TextComponentString("The password of this session is incorrect."));
                    }
                    return;
                } else {
                    sender.sendMessage(new TextComponentString("You need to supply a user to delete."));
                }
                return;
            case "save":
                String encrypt = SessionUtils.encrypt(SessionUtils.getSession(), Main.cachedPassword);
                Main.accountHash.add(encrypt);
                try {
                    Configurations.writeConfig();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sender.sendMessage(new TextComponentString("Saved account " + SessionUtils.getSession().getUsername() + "!"));
                sender.sendMessage(new TextComponentString("Please keep in mind that the password may be different from other sessions."));
                break;
            case "token":
                if(flag) {
                    token = args[1];
                    sender.sendMessage(new TextComponentString("Token set."));
                } else {
                    sender.sendMessage(new TextComponentString("Token: " + token));
                }
                break;
            case "uuid":
                if(flag) {
                    uuid = args[1];
                    sender.sendMessage(new TextComponentString("UUID set."));
                } else {
                    sender.sendMessage(new TextComponentString("UUID: " + uuid));
                }
                break;
            case "user":
            case "name":
            case "username":
                if(flag) {
                    name = args[1];
                    sender.sendMessage(new TextComponentString("Name set."));
                } else {
                    sender.sendMessage(new TextComponentString("Name: " + name));
                }
                break;
            case "login":
                if(flag) {
                    Session s = Main.sessionMap.get(args[1]);
                    if(s == null) {
                        sender.sendMessage(new TextComponentString("Supplied name does not exist for this session!"));
                        return;
                    }
                    disconnect();
                    SessionUtils.getMinecraft().setSession(s);
                    return;
                }
                if(isDefined()) {
                    final Session s = new Session(name, uuid, token, "SpiritTree");
                    if(SessionUtils.sessionEquals(s, SessionUtils.getSession())) {
                        sender.sendMessage(new TextComponentString("You may not log into the same session."));
                        return;
                    }
                    disconnect();
                    SessionUtils.getMinecraft().setSession(s);
                    return;
                } else {
                    sender.sendMessage(new TextComponentString("One or more fields are not defined."));
                }
                break;
            case "restore":
                if(SessionUtils.sessionEquals(SessionUtils.DEFAULT, SessionUtils.getSession())) {
                    sender.sendMessage(new TextComponentString("You may not log into the same session."));
                    return;
                }
                disconnect();
                SessionUtils.getMinecraft().setSession(SessionUtils.DEFAULT);
                break;
            default:
                sender.sendMessage(new TextComponentString("Unknown operation."));
        }
    }

    private static void decode() throws InvalidCredentialException {
        List<Session> sret = new ArrayList<>();
        int err = 0;
        for(String s : Main.accountHash) {
            try {
                sret.add(SessionUtils.decrypt(s, Main.cachedPassword));
            } catch (InvalidCredentialException e) {
                err++;
            } catch (Exception e) {
                err++;
                message.send("[InvGui] An exception has occurred during decoding: " + e.getClass().getCanonicalName());
                e.printStackTrace();
            }
        }
        for(Session s : sret) {
            Main.sessionMap.put(s.getUsername(), s);
        }
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString("[InvGui] Decrypted " + sret.size() + " sessions. " + err + " of which uses a different password."));
    }

    /**
     * Check if supplied data is defined.
     */
    private static boolean isDefined() {
        return !token.equals("") && !uuid.equals("") && !name.equals("");
    }

    private void getHelp(ICommandSender sender) {
        sender.sendMessage(new TextComponentString("/" + getName() + " token|username|uuid|list|save|password"));
        sender.sendMessage(new TextComponentString("Then: /" + getName() + " login [username]"));
        sender.sendMessage(new TextComponentString("WARNING: Logging in will result in disconnection."));
        sender.sendMessage(new TextComponentString("To use default login, do /" + getName() + " restore."));
    }

    private void disconnect() {
        if(Minecraft.getMinecraft().isIntegratedServerRunning()) {
            message.send("[InvGui] Your session has been modified. You will not be disconnected as you are in integrated server.");
            return;
        }
        ConnectionHandler.disconnect(ConnectionHandler.getConnection(), disconnectMessage);
    }
}
