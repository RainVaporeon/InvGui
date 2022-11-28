package com.spiritlight.invgui.utils;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import javax.annotation.ParametersAreNullableByDefault;

@ParametersAreNullableByDefault
public class RayTraceBlock {

    private static final EntityPlayerSP player = Minecraft.getMinecraft().player;
    public static BlockPos getPos() {
        RayTraceResult block = player.rayTrace(36.0d, 0.0f);
        return block != null ? block.getBlockPos() : null;
    }

    public static IBlockState getBlockState() {
        RayTraceResult block = player.rayTrace(36.0d, 0.0f);
        return block != null ? player.world.getBlockState(block.getBlockPos()) : null;
    }

    public static Material getBlock() {
        RayTraceResult block = player.rayTrace(36.0d, 0.0f);
        return block != null ? player.world.getBlockState(block.getBlockPos()).getMaterial() : null;
    }

    public static RayTraceResult getResult() {
        return player.rayTrace(36.0d, 0.0f);
    }
}
