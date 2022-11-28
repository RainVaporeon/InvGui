package com.spiritlight.invgui.commands;

import com.spiritlight.invgui.events.PacketEvent;
import com.spiritlight.invgui.interfaces.annotations.AutoRegister;
import com.spiritlight.invgui.interfaces.annotations.AutoSubscribe;
import com.spiritlight.invgui.message;
import com.spiritlight.invgui.mixins.IMixinCPacketPlayer;
import com.spiritlight.invgui.utils.PlayerUtils;
import com.spiritlight.invgui.utils.SpiritCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;

@AutoSubscribe
@AutoRegister(name = "back", aliases = "sback", permission = 0, requirePrefix = true)
public class BackCommand extends SpiritCommand {
    private static boolean autoRecord = false;

    /**
     * Wait til I'm able to detect whether a teleportation is made... bweh.
     */
    @Override @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 1) {
            switch(args[0].toLowerCase(Locale.ROOT)) {
                case "help":
                    sender.sendMessage(new TextComponentString("/back - Returns to last position."));
                    sender.sendMessage(new TextComponentString("Positions are recorded when /stp is executed."));
                    sender.sendMessage(new TextComponentString("Alternatively, use /back record to record current position."));
                    return;
                case "auto":
                    autoRecord = !autoRecord;
                    sender.sendMessage(new TextComponentString("Toggled auto record! Now: " + autoRecord));
                    return;
                case "record":
                    sender.sendMessage(new TextComponentString("Recorded position."));
                    PlayerUtils.recordLastPos();
                    return;
            }
        }
        sender.sendMessage(new TextComponentString("Sending you back to the previous teleported position."));
        if(!PlayerUtils.isPosDefined()) {
            sender.sendMessage(new TextComponentString("You haven't recorded a position yet."));
            return;
        }
        PlayerUtils.teleport(PlayerUtils.getLastTeleportCoordinates());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST) // Just a listener
    public void onPositionPacket(PacketEvent.Outbound<CPacketPlayer> event) {
        IMixinCPacketPlayer packet = (IMixinCPacketPlayer) event.getPacket();
        if(!packet.isMoving()) return;
        Vec3d lastPos = PlayerUtils.getLastTickPos();
        Vec3d currentPos = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
        double d = currentPos.lengthSquared() - lastPos.lengthSquared();
        if(Math.abs(d) >= 45.0D) {
            message.send("Detected teleportation! New position set.");
            PlayerUtils.recordLastPos();
        }
    }
}
