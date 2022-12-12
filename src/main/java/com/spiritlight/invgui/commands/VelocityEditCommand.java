package com.spiritlight.invgui.commands;

import com.spiritlight.invgui.Main;
import com.spiritlight.invgui.events.PacketEvent;
import com.spiritlight.invgui.exceptions.ProcessException;
import com.spiritlight.invgui.interfaces.annotations.AutoRegister;
import com.spiritlight.invgui.message;
import com.spiritlight.invgui.mixins.IMixinSPacketEntityVelocity;
import com.spiritlight.invgui.utils.ArrayUtils;
import com.spiritlight.invgui.utils.PlayerUtils;
import com.spiritlight.invgui.utils.SpiritCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@AutoRegister(name = "vledit", aliases = "velocityedit", permission = 0, requirePrefix = true)
public class VelocityEditCommand extends SpiritCommand {
    private static final Style textStyle = new Style().setHoverEvent(
            new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(TextFormatting.GOLD + "Click here to hide!"))
    ).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vledit shutup"));
    protected double[] modify = new double[] {1.0, 1.0, 1.0};
    private boolean shouldMute = false;

    @Override @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) {
            sendHelp(sender);
            return;
        }
        if(args.length == 1 && args[0].equals("reset")) {
            modify = new double[] {1.0, 1.0, 1.0};
            return;
        }
        if(args.length == 1 && args[0].equals("shutup")) {
            shouldMute = true;
        }
        if(args.length == 3) {
            double[] dArr = new double[3];
            int i = 0;
            try {
                for(; i < args.length; i++) {
                    dArr[i] = Double.parseDouble(args[i]);
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(new TextComponentString("Invalid input: For input " + args[i]));
                return;
            }
            modify = Arrays.copyOf(dArr, dArr.length);
            if(!shouldMute)
                sender.sendMessage(new TextComponentString("Successfully set velocity multiplier to " + modify[0] + ", " + modify[1] + ", " + modify[2]).setStyle(textStyle));
            return;
        }
        sendHelp(sender);
    }

    private void sendHelp(ICommandSender sender) {
        sender.sendMessage(new TextComponentString("/" + getName() + " reset|<xMod, yMod, zMod>"));
    }

    // Dev note: When processing packet data, it is divided by 8000D before applying.
    @SubscribeEvent
    public void onSpeedPacket(PacketEvent.Inbound<SPacketEntityVelocity> velocityPacket) {
        if(!Main.enabled) return;
        IMixinSPacketEntityVelocity packet = (IMixinSPacketEntityVelocity) velocityPacket.getPacket();
        Double[] velocity = new Double[] {(double) packet.getMotionX(), (double) packet.getMotionY(), (double) packet.getMotionZ()};
        try {
            double[] newVl = ArrayUtils.multiply(ArrayUtils.toPrimitive(velocity), modify);
            PlayerUtils.setVelocity(newVl, false);
            message.send("[InvGui] Modified Velocity Packet (from: " + ArrayUtils.getString(velocity) + ", to: " + ArrayUtils.getString(ArrayUtils.fromPrimitive(newVl)) + ")");
        } catch (ProcessException e) {
            // shouldn't happen as modify.length == velocity.length
            // still print just in case
            e.printStackTrace();
        }
        velocityPacket.setCanceled(true);
    }
}
