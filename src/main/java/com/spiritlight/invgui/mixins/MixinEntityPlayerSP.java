package com.spiritlight.invgui.mixins;

import com.spiritlight.invgui.events.UpdateEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP {
    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    public void onUpdate(CallbackInfo callbackInfo) {
        MinecraftForge.EVENT_BUS.post(new UpdateEvent((EntityPlayerSP) (Object) this));
    }
}
