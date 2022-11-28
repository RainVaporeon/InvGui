package com.spiritlight.invgui.mixins;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.handshake.FMLHandshakeMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

// Hello staffs! This particular class will allow users to hide their mods from the server.
@Mixin(value = FMLHandshakeMessage.ModList.class, remap = false)
public class MixinModList extends FMLHandshakeMessage {
    @Shadow(remap = false)
    private Map<String, String> modTags;

    @Inject(method = "<init>(Ljava/util/List;)V", at = @At("RETURN"), remap = false)
    public void ModList(List<ModContainer> container, CallbackInfo ci) {
        if(!Minecraft.getMinecraft().isIntegratedServerRunning()) {
            final List<String> commonMods = Arrays.asList("fml", "forge", "mcp");
            modTags.entrySet().removeIf(modid -> !commonMods.contains(modid.getKey().toLowerCase(Locale.ROOT)));
        }
    }
}