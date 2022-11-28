package com.spiritlight.invgui.utils;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault @SuppressWarnings("unused")
public class BlockPosUtils {
    public static BlockPos edit(BlockPos pos, double x, double y, double z) {
        return new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
    }

    public static BlockPos edit(BlockPos pos, Vec3i vector) {
        return new BlockPos(pos.add(vector));
    }

    public static BlockPos edit(BlockPos pos, Vec3d vector) {
        return new BlockPos(pos.getX() + vector.x, pos.getY() + vector.y, pos.getZ() + vector.z);
    }

    public static Vec3i getVec3i(BlockPos pos) {
        return new Vec3i(pos.getX(), pos.getY(), pos.getZ());
    }

    public static Vec3d getVec3d(BlockPos pos) {
        return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public static List<BlockPos> getSurroundingBlocks(BlockPos pos) {
        List<BlockPos> result = new ArrayList<>();
        result.add(pos);
        result.add(edit(pos, 1, 0, 0));
        result.add(edit(pos, 0, 1, 0));
        result.add(edit(pos, 0, 0, 1));
        result.add(edit(pos, -1, 0, 0));
        result.add(edit(pos, 0, -1, 0));
        result.add(edit(pos, 0, 0, -1));
        return result;
    }

    public static String[] toStringArray(BlockPos pos) {
        return new String[] {String.valueOf(pos.getX()), String.valueOf(pos.getY()), String.valueOf(pos.getZ())};
    }

    public static int[] toIntArray(BlockPos pos) {
        return new int[] {pos.getX(), pos.getY(), pos.getZ()};
    }

    public static double[] toDoubleArray(BlockPos pos) {
        return new double[] {pos.getX(), pos.getY(), pos.getZ()};
    }

    public static BlockPos arrayToPos(double[] d) {
        if(d.length != 3) throw new IllegalArgumentException("Array size must be 3, found " + d.length);
        return new BlockPos(d[0], d[1], d[2]);
    }

    public static BlockPos arrayToPos(int[] d) {
        if(d.length != 3) throw new IllegalArgumentException("Array size must be 3, found " + d.length);
        return new BlockPos(d[0], d[1], d[2]);
    }

    public static Vec3d toVec3d(double[] val) {
        return new Vec3d(val[0], val[1], val[2]);
    }

    public static BlockPos getValidTeleportBlock(BlockPos pos) {
        if(BlockUtils.getBlockAt(pos).equals(Material.AIR))
            return pos;
        int direction = (Objects.equals(RayTraceBlock.getBlock(), Material.AIR) ? -1 : MathHelper.floor(((PlayerUtils.getPlayer().rotationYaw * 4F) / 360F) + 0.5D) & 3);
        int modX = (direction == 3 ? -1 : (direction == 1 ? 1 : 0));
        int modY = (direction != -1 ? 1 : 0);
        int modZ = (direction == 0 ? -1 : (direction == 2 ? 1 : 0));
        return edit(pos, modX, modY, modZ);
    }

    public static boolean equalsFloor(BlockPos match, BlockPos floor) {
        return match.getX() == floor.getX() && match.getY()-1 == floor.getY() && match.getZ() == floor.getZ();
    }

    public static boolean posEquals(BlockPos match, BlockPos floor) {
        return match.getX() == floor.getX() && match.getY() == floor.getY() && match.getZ() == floor.getZ();
    }
}