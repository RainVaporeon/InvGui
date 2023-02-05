package com.spiritlight.invgui.commands;

import com.spiritlight.invgui.exceptions.PositionDesyncException;
import com.spiritlight.invgui.interfaces.annotations.AutoRegister;
import com.spiritlight.invgui.message;
import com.spiritlight.invgui.utils.BlockPosUtils;
import com.spiritlight.invgui.utils.PlayerUtils;
import com.spiritlight.invgui.utils.SpiritCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@AutoRegister(name = "sstp", requirePrefix = true, permission = 0)
public class SafeSpiritTeleportCommand extends SpiritCommand {
    // Copy-pasted from stp command with a lil modification
    @Override @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        // 2Or3
        if (!(args.length == 2 || args.length == 3)) {
            message.send("Invalid coordinates.");
            return;
        }
        final EntityPlayerSP player = Minecraft.getMinecraft().player;

        // assert args.length == 2
        double[] parsedArgsD = new double[]{0.5, 0.5};
        double[] playerPosD = new double[]{player.posX, player.posZ};
        if (args.length == 2) {
            // x, z
            for (int i = 0; i < args.length; i++) {
                boolean flag = args[i].startsWith("~");
                boolean flag1 = args[i].equals("~");
                if (flag) {
                    // y coordinate is unchanged
                    parsedArgsD[i] += playerPosD[i];
                }
                if (flag1) {
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
            try {
                teleportSplit(Minecraft.getMinecraft().player.getPosition(), destination);
                message.send("Successfully teleported to " + (destination.getX() + 0.5) + ", " + player.posY + ", " + (destination.getZ() + 0.5));
            } catch (PositionDesyncException pde) {
                message.send(pde.getMessage());
            }
            return;
        }
        // assert args.length == 3;
        double[] parsedArgs = new double[]{0.5, 0.0, 0.5};
        double[] playerPos = new double[]{player.posX, player.posY, player.posZ};
        for (int i = 0; i < args.length; i++) {
            boolean flag = args[i].startsWith("~");
            boolean flag1 = args[i].equals("~");
            if (flag) {
                parsedArgs[i] += playerPos[i];
            }
            if (flag1) {
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
        try {
            teleportSplit(Minecraft.getMinecraft().player.getPosition(), destination);
            message.send("Successfully teleported to " + (destination.getX() + 0.5) + ", " + (destination.getY()) + ", " + (destination.getZ() + 0.5));
        } catch (PositionDesyncException e) {
            message.send(e.getMessage());
        }
    }

    private void teleportSplit(BlockPos start, BlockPos destination) throws PositionDesyncException {
        CompletableFuture.runAsync(() -> {
            BlockPos difference = destination.subtract(start);
            Vec3d tmp = new Vec3d(difference.getX(), difference.getY(), difference.getZ());
            Vec3d vector = tmp.normalize().scale(75.0d); // Traverse 75 blocks per iteration
            // Calculate the expected teleports
            double expectedTravelDistance = start.getDistance(destination.getX(), destination.getY(), destination.getZ());
            // Base case: Already low enough
            if(expectedTravelDistance <= 75.0) {
                PlayerUtils.teleportCenter(destination);
                return;
            }
            int multiplier = 1;
            while (Minecraft.getMinecraft().player.getPosition().getDistance(destination.getX(), destination.getY(), destination.getZ()) > 75.0) {
                BlockPos expectedDestination = new BlockPos(start.getX() + vector.x * multiplier,
                        start.getY() + vector.y * multiplier,
                        start.getZ() + vector.z * multiplier);
                // message.send("Teleporting to " + Arrays.toString(BlockPosUtils.toStringArray(expectedDestination)));
                try {
                    Thread.sleep(100);
                    PlayerUtils.teleportCenter(expectedDestination).delayCheck(350).assertArrival(12.5);
                    multiplier++;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (PositionDesyncException e) {
                    // We have another attempt in teleportation
                    try {
                        PlayerUtils.teleportCenter(expectedDestination).delayCheck(500).assertArrival(15);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            // message.send("Teleporting to " + Arrays.toString(BlockPosUtils.toStringArray(destination)));
            PlayerUtils.teleportCenter(destination);
        });
    }
}
