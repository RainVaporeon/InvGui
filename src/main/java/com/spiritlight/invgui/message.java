package com.spiritlight.invgui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

public class message {
    static void send(String s) {
        if(Minecraft.getMinecraft().player == null) return;
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(s));
    }
}
