package com.spiritlight.invgui;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import static com.spiritlight.invgui.GuiHandler.loadGui;

public class KeyBindings {
    public static final KeyBinding[] keyBindings = new KeyBinding[] {
            new KeyBinding("Toggle mod", Keyboard.KEY_K, "InvGui"),
            new KeyBinding("Toggle Blink", Keyboard.KEY_O, "InvGui"),
            new KeyBinding("Load Gui Slot #1", Keyboard.KEY_P, "InvGui"),
            new KeyBinding("Load Gui Slot #2", Keyboard.KEY_LBRACKET, "InvGui"),
            new KeyBinding("Load Gui Slot #3", Keyboard.KEY_RBRACKET, "InvGui")
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
            if(loadGui(0)) {
                message.send("Loading saved GUI #1");
            }
            return;
        }
        if(keyBindings[3].isPressed()) {
            if(loadGui(1)) {
                message.send("Loading saved GUI #2");
            }
            return;
        }
        if(keyBindings[4].isPressed()) {
            if(loadGui(2)) {
                message.send("Loading saved GUI #3");
            }
            return;
        }
    }
}