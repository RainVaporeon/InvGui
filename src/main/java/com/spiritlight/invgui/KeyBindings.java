package com.spiritlight.invgui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import static com.spiritlight.invgui.GuiHandler.loadGui;

public class KeyBindings {
    public static final KeyBinding kb = new KeyBinding("Toggle mod", Keyboard.KEY_K, "InvGui");
    public static final KeyBinding kb2 = new KeyBinding("Toggle Blink", Keyboard.KEY_O, "InvGui");
    public static final KeyBinding kb3 = new KeyBinding("Load Gui Slot #1", Keyboard.KEY_P, "InvGui");
    public static final KeyBinding kb4 = new KeyBinding("Load Gui Slot #2", Keyboard.KEY_LBRACKET, "InvGui");
    public static final KeyBinding kb5 = new KeyBinding("Load Gui Slot #3", Keyboard.KEY_RBRACKET, "InvGui");
    private static boolean enabled = false;

    public static void register() {
        if (enabled) return;
        ClientRegistry.registerKeyBinding(kb);
        ClientRegistry.registerKeyBinding(kb2);
        ClientRegistry.registerKeyBinding(kb3);
        enabled = true;
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if (kb.isPressed()) {
            Main.enabled = !Main.enabled;
            message.send("Toggled mod!: " + Main.enabled);
            return;
        }
        if (kb2.isPressed()) {
            PacketHandler.setPacketReceiving(!PacketHandler.isPacketReceiving());
            message.send("Toggled packet sending to " + PacketHandler.isPacketReceiving());
            return;
        }
        if(kb3.isPressed()) {
            if(loadGui(0)) {
                message.send("Loading saved GUI #1");
            }
            return;
        }
        if(kb4.isPressed()) {
            if(loadGui(1)) {
                message.send("Loading saved GUI #2");
            }
            return;
        }
        if(kb5.isPressed()) {
            if(loadGui(2)) {
                message.send("Loading saved GUI #3");
            }
            return;
        }
    }
}