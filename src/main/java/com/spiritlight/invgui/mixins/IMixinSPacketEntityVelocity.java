package com.spiritlight.invgui.mixins;

import net.minecraft.network.play.server.SPacketEntityVelocity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SPacketEntityVelocity.class)
public interface IMixinSPacketEntityVelocity {
    @Accessor
    void setMotionX(int x);

    @Accessor
    void setMotionY(int y);

    @Accessor
    void setMotionZ(int z);

    @Accessor
    int getMotionX();

    @Accessor
    int getMotionY();

    @Accessor
    int getMotionZ();
}
