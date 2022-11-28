package com.spiritlight.invgui;

import com.spiritlight.invgui.connections.PacketHandler;
import com.spiritlight.invgui.handlers.ContainerHandler;
import com.spiritlight.invgui.interfaces.annotations.AutoSubscribe;
import com.spiritlight.invgui.handlers.GuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

@AutoSubscribe
public class KeyBindings {
    public static final String MOD_ID = "InvGui";
    public static final String IN_GUI = "In-GUI Keybinds";
    public static final KeyBinding[] keyBindings = new KeyBinding[]{
            new KeyBinding("Toggle mod", Keyboard.KEY_K, MOD_ID),
            new KeyBinding("Toggle Blink", Keyboard.KEY_O, MOD_ID),
            new KeyBinding("Reopen Silent Closed GUIs", Keyboard.KEY_U, MOD_ID)
    };

    /**
     * These KeyBindings are not meant to be handled here. Instead, {@link ContainerHandler}
     * should be handling these inputs.
     */
    public static final KeyBinding[] guiKeyBinds = new KeyBinding[]{
            new KeyBinding("Record Slot 1", Keyboard.KEY_N, IN_GUI),
            new KeyBinding("Record Slot 2", Keyboard.KEY_M, IN_GUI),
            new KeyBinding("Repeat Slot 1, 2", Keyboard.KEY_COMMA, IN_GUI)
    };

    private static boolean enabled = false;

    public static void register() {
        if (enabled) return;
        for (KeyBinding k : keyBindings) {
            ClientRegistry.registerKeyBinding(k);
        }
        // Does not work here, should be handled
        for (KeyBinding k2 : guiKeyBinds) {
            ClientRegistry.registerKeyBinding(k2);
        }
        enabled = true;
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if (!Main.enabled) return; // No activations
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
        if (keyBindings[2].isPressed()) {
            if (GuiHandler.getLastGui() == null) {
                message.send("No GUI can be reopened at the moment.");
                return;
            }
            Minecraft.getMinecraft().displayGuiScreen(GuiHandler.getLastGui());
            message.send("Attempting to open the silent closed GUI");
        }
    }
}