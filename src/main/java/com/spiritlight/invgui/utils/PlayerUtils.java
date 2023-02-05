package com.spiritlight.invgui.utils;

import com.spiritlight.invgui.exceptions.PositionDesyncException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

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

    public static Result teleport(BlockPos pos) {
        EntityPlayerSP player = getPlayer();
        recordLastPos();
        player.setPosition(pos.getX(), pos.getY(), pos.getZ());
        return new Result(pos, 5.0);
    }

    /**
     *
     * @throws IllegalArgumentException if pos does not have at least 3 contents.
     */
    public static Result teleport(double[] pos) throws IllegalArgumentException {
        if(pos.length < 3) throw new IllegalArgumentException("PlayerUtils$teleport(double[] pos) cannot take array size of less than 3!");
        EntityPlayerSP player = getPlayer();
        recordLastPos();
        player.setPosition(pos[0], pos[1], pos[2]);
        return new Result(BlockPosUtils.arrayToPos(pos), 5.0);
    }

    public static Result teleportCenter(BlockPos pos) {
        EntityPlayerSP player = getPlayer();
        recordLastPos();
        player.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        return new Result(pos, 5.0);
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

    /**
     * Checks whether a player position is on the margin,
     * if failed, throw PositionDesyncException
     */
    public static void checkPos(BlockPos location, double margin) throws PositionDesyncException {
        new Result(location, margin).assertArrival0();
    }

    /**
     * Chained response from various teleportation commands,
     * mildly useful to check whether a teleportation succeeded.
     * <p></p>
     * Note that often times this is not needed, but useful if
     * you are doing chained teleportation, or something that depends
     * on relatively precise location would need it.
     */
    public static class Result {
        private final BlockPos position;
        private final double margin;
        private long pauseMills;

        private Result(BlockPos position, double margin) {
            this.position = position;
            this.margin = margin;
            this.pauseMills = 500;
        }

        /**
         * Delays the checking time, useful for latency
         * @param pauseMills The duration (in milliseconds) to pause
         */
        public Result delayCheck(long pauseMills) {
            this.pauseMills = pauseMills;
            return this;
        }

        public void assertArrival() throws PositionDesyncException, InterruptedException {
            if (pauseMills != 0) {
                Thread.sleep(pauseMills);
            }
            assertArrival(margin);
        }

        public void assertArrival(double margin) throws PositionDesyncException, InterruptedException {
            if (pauseMills != 0) {
                Thread.sleep(pauseMills);
            }
            this.assertArrival0();
        }

        private void assertArrival0() {
            final BlockPos playerPos = Minecraft.getMinecraft().player.getPosition();
            if(playerPos.getDistance(position.getX(), position.getY(), position.getZ()) >= margin) {
                throw new PositionDesyncException(position);
            }
        }
    }
}
