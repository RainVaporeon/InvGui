package com.spiritlight.invgui.handlers;

import com.spiritlight.invgui.KeyBindings;
import com.spiritlight.invgui.Main;
import com.spiritlight.invgui.interfaces.annotations.AutoSubscribe;
import com.spiritlight.invgui.utils.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;

// Handles container interactions
@AutoSubscribe
public class ContainerHandler {
    private final Slot[] slots = new Slot[2];


    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onKeyInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if(!Main.enabled) return;
        if(!(event.getGui() instanceof GuiContainer)) return;
        EntityPlayerSP player = PlayerUtils.getPlayer();
        GuiContainer gui = (GuiContainer) event.getGui();
        int slotOne = KeyBindings.guiKeyBinds[0].getKeyCode();
        int slotTwo = KeyBindings.guiKeyBinds[1].getKeyCode();
        int repeatSlot = KeyBindings.guiKeyBinds[2].getKeyCode();
        int key = Keyboard.getEventKey();
        // 0: Slot 1; 1: Slot 2; 2: Slot Click
        if(slotOne == key) {
            player.sendMessage(new TextComponentString("Saved Slot 1"));
            slots[0] = getCurrentSlot(gui);
            return;
        }
        if(slotTwo == key) {
            player.sendMessage(new TextComponentString("Saved Slot 2"));
            slots[1] = getCurrentSlot(gui);
            return;
        }
        if(repeatSlot == key) {
            // Simulate interacting back and forth
            for(int repeat = 0; repeat < 100; repeat++) {
                for(int i=0; i<2; i++) {
                    Minecraft.getMinecraft().playerController.windowClick(gui.inventorySlots.windowId, slots[i].slotNumber, 0, ClickType.PICKUP, player);
                }
            }
        }
    }

    @Nullable
    private Slot getCurrentSlot(GuiContainer gui) {
        return gui.getSlotUnderMouse();
    }
}
