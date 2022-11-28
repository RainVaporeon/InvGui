package com.spiritlight.invgui.mixins;

import net.minecraft.network.play.client.CPacketPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CPacketPlayer.class)
public interface IMixinCPacketPlayer {
    @Accessor(value = "x")
    void setX(double xIn);

    @Accessor(value = "x")
    double getX();

    @Accessor(value = "y")
    void setY(double yIn);

    @Accessor(value = "y")
    double getY();

    @Accessor(value = "z")
    void setZ(double zIn);

    @Accessor(value = "z")
    double getZ();

    @Accessor
    void setYaw(float yawIn);

    @Accessor
    void setPitch(float pitchIn);

    @Accessor
    void setOnGround(boolean onGroundIn);

    @Accessor
    void setRotating(boolean rotatingIn);

    @Accessor
    void setMoving(boolean movingIn);

    @Accessor
    boolean isMoving();
}
