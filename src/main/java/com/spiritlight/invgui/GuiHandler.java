package com.spiritlight.invgui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GuiHandler {
    private static final List<GuiScreen> savedGuiList = new ArrayList<GuiScreen>(4) {{
        add(0, null);
        add(1, null);
        add(2, null);
        add(3, null);
    }};
    private GuiTextField textField;
    private boolean doPersist = false;
    private String previousInput = "";
    private double mouseX = 0;
    private double mouseY = 0;
    private final Pattern validInput = Pattern.compile("\\w");
    private boolean isTouchscreen = Minecraft.getMinecraft().gameSettings.touchscreen;
    private boolean preventClosing = false;

    @SubscribeEvent
    public void onDrawScreen(final GuiScreenEvent.DrawScreenEvent.Post event) {
        if(!(event.getGui() instanceof GuiContainer)) return;
        if(textField == null) return;
        if(!Main.enabled) return;
        textField.drawTextBox();
        mouseX = event.getMouseX();
        mouseY = event.getMouseY();
    }

    @SubscribeEvent (priority = EventPriority.HIGHEST) // We want the highest priority on input to override other hotkeys for field inputs
    public void onKeyInput(final GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if(!Main.enabled) return;
        if(!(event.getGui() instanceof GuiContainer)) return;
        if(textField == null) return;
        if(textField.isFocused()) {
            textField.textboxKeyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
            previousInput = textField.getText();
            // Cancel any other operations after a valid textField input has been accepted: RegExr(\w) AND backspace for deletions
            event.setCanceled(validInput.matcher((Character.toString(Keyboard.getEventCharacter()))).find() || Keyboard.getEventKey() == Keyboard.KEY_BACK);
        }
    }

    @SubscribeEvent
    public void onMouseInput(GuiScreenEvent.MouseInputEvent.Post event) {
        if(!(event.getGui() instanceof GuiContainer)) return;
        if(textField == null) return;
        if(!Main.enabled) {
            textField.setFocused(false);
        }
        if(Mouse.getEventButton() != 0) return;
        double tfXStart = textField.x;
        double tfXEnd = textField.x + textField.width;
        double tfYStart = textField.y;
        double tfYEnd = textField.y + textField.height;
        textField.setFocused(inRange(mouseX, tfXStart, tfXEnd) && inRange(mouseY, tfYStart, tfYEnd));
    }

    @SubscribeEvent
    public void onInitGui(final GuiScreenEvent.InitGuiEvent.Post event) {
        if(!Main.enabled) return;
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null || mc.ingameGUI == null) {
            return;
        }
        if (Minecraft.getMinecraft().player.inventory == null) {
            return;
        }
        isTouchscreen = mc.gameSettings.touchscreen;
        final ScaledResolution sr = new ScaledResolution(mc);
        final FontRenderer fontRenderer = mc.fontRenderer;
        final int savedGui_x = (sr.getScaledWidth() + 225) / 2;
        final int cmd_x = (sr.getScaledWidth() + 225) / 2;
        final int cmdBtn_y = (sr.getScaledHeight() - 75) / 2;
        final int cmdField_y = (sr.getScaledHeight() - 125) / 2;
        final int loadGui_y = (sr.getScaledHeight() - 25) / 2;
        final int saveGui_y = (sr.getScaledHeight() + 25) / 2;
        final int btn3_x = (sr.getScaledWidth() + 225) / 2;
        final int btn3_y = (sr.getScaledHeight() + 75) / 2;
        final int btn4_x = (sr.getScaledWidth() + 225) / 2;
        final int btn4_y = (sr.getScaledHeight() + 125) / 2;
        if (event.getGui() instanceof GuiContainer) {
            event.getButtonList().add(new GuiButton(100, btn3_x, btn3_y, "Close GUI silently"));
            event.getButtonList().add(new GuiButton(102, btn4_x+105, btn4_y, 95, 20, "TouchScreen" + (isTouchscreen ? TextFormatting.GREEN + " ON" : TextFormatting.RED + " OFF")));
            event.getButtonList().add(new GuiButton(103, btn4_x, btn4_y, 95, 20, "Packets" + (PacketHandler.isPacketReceiving() ? TextFormatting.GREEN + " ON" : TextFormatting.RED + " OFF")));
            event.getButtonList().add(new GuiButton(104, savedGui_x, loadGui_y, 60, 20, "Load GUI 1"));
            event.getButtonList().add(new GuiButton(105, savedGui_x+70, loadGui_y, 60, 20, "Load GUI 2"));
            event.getButtonList().add(new GuiButton(106, savedGui_x+140, loadGui_y, 60, 20, "Load GUI 3"));
            event.getButtonList().add(new GuiButton(107, savedGui_x, saveGui_y, 60, 20, "Save GUI 1"));
            event.getButtonList().add(new GuiButton(108, savedGui_x+70, saveGui_y, 60, 20, "Save GUI 2"));
            event.getButtonList().add(new GuiButton(109, savedGui_x+140, saveGui_y, 60, 20, "Save GUI 3"));
            event.getButtonList().add(new GuiButton(110, cmd_x, cmdBtn_y, 95, 20, "Send Message"));
            event.getButtonList().add(new GuiButton(111, cmd_x+105, cmdBtn_y, 95, 20, "Invoke Client Command"));
            event.getButtonList().add(new GuiButton(1002, cmd_x+170, cmdField_y, 30, 20, doPersist ? "O" : "X"));
            textField = new GuiTextField(1001, fontRenderer, cmd_x, cmdField_y, 160, 20);
            if(doPersist)
                textField.setText(previousInput);
            textField.setMaxStringLength(255);
            textField.setFocused(false);
        }
    }

    @SubscribeEvent
    public void onGuiOpen(final GuiOpenEvent event) {
        if(!Main.enabled) return;
        if(preventClosing && event.getGui() == null) {
            preventClosing = false;
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPostActionPerformedGui(final GuiScreenEvent.ActionPerformedEvent.Post event) {
        if(!Main.enabled) return;
        final Minecraft mc = Minecraft.getMinecraft();
        if (event.getGui() instanceof GuiContainer) {
            if(isTouchscreen) preventClosing = true;
            switch(event.getButton().id) {
                case 100:
                    preventClosing = false;
                    mc.player.sendMessage(new TextComponentString("Silently closed GUI."));
                    PacketHandler.discardPacket("CPacketCloseWindow", PacketHandler.Enum.WRITE);
                    mc.player.closeScreen();
                    break;
                case 102:
                    preventClosing = !isTouchscreen;
                    mc.gameSettings.touchscreen = !mc.gameSettings.touchscreen;
                    isTouchscreen = mc.gameSettings.touchscreen;
                    event.getButton().displayString = "Touchscreen" + (isTouchscreen ? TextFormatting.GREEN + " ON" : TextFormatting.RED + " OFF");
                    mc.player.sendMessage(new TextComponentString("Touchscreen mode is now" + (isTouchscreen ? TextFormatting.GREEN + " ON" : TextFormatting.RED + " OFF") + TextFormatting.RESET + "\nYou may go to Options -> Controls to turn it off."));
                    return;
                case 103:
                    PacketHandler.setPacketReceiving(!PacketHandler.isPacketReceiving());
                    message.send("Toggled packet sending to " + PacketHandler.isPacketReceiving());
                    event.getButton().displayString = "Packets" + (PacketHandler.isPacketReceiving() ? TextFormatting.GREEN + " ON" : TextFormatting.RED + " OFF");
                    break;
                case 104:
                    if(loadGui(0)) {
                        message.send("Loading saved GUI #1");
                    }
                    break;
                case 105:
                    if(loadGui(1)) {
                        message.send("Loading saved GUI #2");
                    }
                    break;
                case 106:
                    if(loadGui(2)) {
                        message.send("Loading saved GUI #3");
                    }
                    break;
                case 107:
                    message.send("Saved GUI at slot #1");
                    saveGui(0, event.getGui());
                    break;
                case 108:
                    message.send("Saved GUI at slot #2");
                    saveGui(1, event.getGui());
                    break;
                case 109:
                    message.send("Saved GUI at slot #3");
                    saveGui(2, event.getGui());
                    break;
                case 110:
                    if(!textField.getText().equals("")) {
                        Minecraft.getMinecraft().player.sendChatMessage(textField.getText());
                        if(!doPersist) {
                            textField.setText("");
                        }
                    } else {
                        message.send("Content must not be empty.");
                    }
                    break;
                case 111:
                    if(!textField.getText().equals("")) {
                        ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().player, textField.getText());
                        if(!doPersist) {
                            textField.setText("");
                        }
                    } else {
                        message.send("Content must not be empty.");
                    }
                    break;
                case 1002:
                    doPersist = !doPersist;
                    event.getButton().displayString = doPersist ? "O" : "X";
                    break;
            }
            if(!isTouchscreen)
                preventClosing = false;
        }
    }

    protected static boolean loadGui(int id) {
        if(id < 0 || id >= 3) return false;
        if(savedGuiList.get(id) == null) return false;
        Minecraft.getMinecraft().displayGuiScreen(savedGuiList.get(id));
        return true;
    }

    protected static void saveGui(int id, GuiScreen guiScreen) {
        if(id < 0 || id >= 3) return;
        savedGuiList.set(id, guiScreen);
    }

    protected static boolean inRange(double toCompare, double min, double max) {
        if(min > max) {
            return min >= toCompare && toCompare >= max;
        }
        return max >= toCompare && toCompare >= min;
    }
}
