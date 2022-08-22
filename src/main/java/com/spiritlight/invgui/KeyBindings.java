package com.spiritlight.invgui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class KeyBindings {
    public static final KeyBinding[] keyBindings = new KeyBinding[] {
            new KeyBinding("Toggle mod", Keyboard.KEY_K, "InvGui"),
            new KeyBinding("Toggle Blink", Keyboard.KEY_O, "InvGui"),
            new KeyBinding("Reopen Silent Closed GUIs", Keyboard.KEY_U, "InvGui")
    };
    private static boolean enabled = false;

    public static void register() {
        if (enabled) return;
        for(KeyBinding k : keyBindings) {
            ClientRegistry.registerKeyBinding(k);
        }
        enabled = true;
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if(!Main.enabled) return; // No activations
        if (keyBindings[0].isPressed()) {
            Main.enabled = !Main.enabled;
            message.send("Toggled mod!: " + Main.enabled);
            return;
        }
        if (keyBindings[1].isPressed()) {
            PacketHandler.setPacketReceiving(!PacketHandler.isPacketReceiving());
            message.send("Toggled packet sending to " + PacketHandler.isPacketReceiving());
            return;
        }
        if(keyBindings[2].isPressed()) {
            if(GuiHandler.getLastGui() == null) {
                message.send("No GUI can be reopened at the moment.");
                return;
            }
            Minecraft.getMinecraft().displayGuiScreen(GuiHandler.getLastGui());
            message.send("Attempting to open the silent closed GUI");
        }
    }
}