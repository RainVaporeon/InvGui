package com.spiritlight.invgui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;


/**
 * Lazy class for McIn.INSTANCE.player.sendMessage(ITextComponent t)
 */
public class message {
    private message() {}

    public static void send(String s) {
        if(Minecraft.getMinecraft().player == null) return;
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(s));
    }
}
