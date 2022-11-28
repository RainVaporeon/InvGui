package com.spiritlight.invgui.mixins;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiChat.class)
public class MixinGuiChat {
    @Shadow
    protected GuiTextField inputField;

    @Inject(method = "initGui", at = @At("RETURN"))
    public void onInit(CallbackInfo ci) {
        inputField.setMaxStringLength(Integer.MAX_VALUE);
    }
}
