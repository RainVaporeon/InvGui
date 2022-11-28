package com.spiritlight.invgui.commands;

import com.spiritlight.invgui.interfaces.annotations.AutoRegister;
import com.spiritlight.invgui.message;
import com.spiritlight.invgui.utils.BlockPosUtils;
import com.spiritlight.invgui.utils.PlayerUtils;
import com.spiritlight.invgui.utils.SpiritCommand;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.ParametersAreNonnullByDefault;

@AutoRegister(permission = 0, requirePrefix = true)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SpiritTeleportCommand extends SpiritCommand {
    @Override
    public String getName() {
        return "stp";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        // 2Or3
        if(!(args.length == 2 || args.length == 3)) {
            message.send("Invalid coordinates.");
            return;
        }
        final EntityPlayerSP player = Minecraft.getMinecraft().player;

        // assert args.length == 2
        double[] parsedArgsD = new double[] {0.5, 0.5};
        double[] playerPosD = new double[] {player.posX, player.posZ};
        if(args.length == 2) {
            // x, z
            for(int i=0; i< args.length; i++) {
                boolean flag = args[i].startsWith("~");
                boolean flag1 = args[i].equals("~");
                if(flag) {
                    // y coordinate is unchanged
                    parsedArgsD[i] += playerPosD[i];
                }
                if(flag1) {
                    args[i] = "0";
                }
                try {
                    parsedArgsD[i] += Double.parseDouble(args[i].replace("~", ""));
                } catch (NumberFormatException e) {
                    message.send("Invalid coordinates.");
                    return;
                }
            }
            BlockPos destination = new BlockPos(parsedArgsD[0], player.posY, parsedArgsD[1]);
            PlayerUtils.teleportCenter(destination);
            message.send("Successfully teleported to " + (destination.getX() + 0.5) + ", " + player.posY + ", " + (destination.getZ() + 0.5));
            return;
        }
        // assert args.length == 3;
        double[] parsedArgs = new double[] {0.5, 0.0, 0.5};
        double[] playerPos = new double[] {player.posX, player.posY, player.posZ};
        for (int i = 0; i < args.length; i++) {
            boolean flag = args[i].startsWith("~");
            boolean flag1 = args[i].equals("~");
            if(flag) {
                parsedArgs[i] += playerPos[i];
            }
            if(flag1) {
                args[i] = "0";
            }
            try {
                parsedArgs[i] += Double.parseDouble(args[i].replace("~", ""));
            } catch (NumberFormatException e) {
                message.send("Invalid coordinates.");
                return;
            }
        }
        BlockPos destination = BlockPosUtils.arrayToPos(parsedArgs);
        PlayerUtils.teleportCenter(destination);
        message.send("Successfully teleported to " + (destination.getX() + 0.5) + ", " + (destination.getY()) + ", " + (destination.getZ() + 0.5));
    }
}
