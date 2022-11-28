package com.spiritlight.invgui.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault @SuppressWarnings("unused")
public class PlayerUtils {
    private static double[] lastCoords = new double[3];

    public static boolean isPosDefined() {
        return defined;
    }

    private static boolean defined = false;

    public static double[] getLastTeleportCoordinates() {
        return lastCoords;
    }

    public static BlockPos getPlayerCameraPos() {
        final EntityPlayerSP player = getPlayer();
        return new BlockPos(player.getPosition().getX(), player.getPosition().getY() + player.eyeHeight, player.getPosition().getZ());
    }

    public static EntityPlayerSP getPlayer() {
        return Minecraft.getMinecraft().player;
    }

    public static void teleport(BlockPos pos) {
        EntityPlayerSP player = getPlayer();
        recordLastPos();
        player.setPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     *
     * @throws IllegalArgumentException if pos does not have at least 3 contents.
     */
    public static void teleport(double[] pos) throws IllegalArgumentException {
        if(pos.length < 3) throw new IllegalArgumentException("PlayerUtils$teleport(double[] pos) cannot take array size of less than 3!");
        EntityPlayerSP player = getPlayer();
        recordLastPos();
        player.setPosition(pos[0], pos[1], pos[2]);
    }

    public static void teleportCenter(BlockPos pos) {
        EntityPlayerSP player = getPlayer();
        recordLastPos();
        player.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }

    public static void recordLastPos() {
        lastCoords = BlockPosUtils.toDoubleArray(getPlayer().getPosition());
        defined = true;
    }

    /**
     *
     * @param mod The modification array.
     * @param pure Whether this modification should be pure. Impure modifications are divided by 8,000 according to {@link net.minecraft.client.network.NetHandlerPlayClient#handleEntityVelocity(SPacketEntityVelocity)}.
     */
    public static void setVelocity(double[] mod, boolean pure) {
        if(pure) {
            getPlayer().setVelocity(mod[0], mod[1], mod[2]);
        } else {
            getPlayer().setVelocity(mod[0]/8000.0D, mod[1]/8000.0D, mod[2]/8000.0D);
        }
    }

    public static ItemStack getHeldItem(EnumHand side) {
        return getPlayer().getHeldItem(side);
    }

    public static Vec3d getLastTickPos() {
        return new Vec3d(getPlayer().lastTickPosX, getPlayer().lastTickPosY, getPlayer().lastTickPosZ);
    }
}
