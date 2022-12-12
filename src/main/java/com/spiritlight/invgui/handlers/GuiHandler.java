package com.spiritlight.invgui.handlers;

import com.spiritlight.invgui.Main;
import com.spiritlight.invgui.connections.PacketHandler;
import com.spiritlight.invgui.interfaces.annotations.AutoSubscribe;
import com.spiritlight.invgui.message;
import com.spiritlight.invgui.mixins.IMixinGuiScreen;
import com.spiritlight.invgui.utils.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Pattern;

@AutoSubscribe
public class GuiHandler {
    private GuiTextField textField;
    private boolean doPersist = false;
    private String previousInput = "";
    private final Pattern validInput = Pattern.compile("\\w");
    private boolean isTouchscreen = Minecraft.getMinecraft().gameSettings.touchscreen;
    private boolean preventClosing = false;
    private static GuiScreen lastGui;
    public static boolean discardItem = true;
    private static final int[] textSelectionCodes = new int[] {Keyboard.KEY_C, Keyboard.KEY_V, Keyboard.KEY_X};

    private static int mouseX;
    private static int mouseY;

    @SubscribeEvent
    public void onDrawScreen(final GuiScreenEvent.DrawScreenEvent.Post event) {
        if(!(event.getGui() instanceof GuiContainer)) return;
        if(textField == null) return;
        if(!Main.enabled) return;
        mouseX = event.getMouseX();
        mouseY = event.getMouseY();
        textField.drawTextBox();
    }

    @SubscribeEvent (priority = EventPriority.HIGHEST) // We want the highest priority on input to override other hotkeys for field inputs
    public void onKeyInput(final GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if(!Main.enabled) return;
        if(!(event.getGui() instanceof GuiContainer)) return;
        if(textField == null) return;
        if(!Keyboard.getEventKeyState()) return;
        if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            PlayerUtils.getPlayer().closeScreen();
            event.setCanceled(true);
            return;
        }
        if(textField.isFocused()) {
            textField.textboxKeyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
            previousInput = textField.getText();
            // Cancel any other operations after a valid textField input has been accepted: RegExr(\w) AND backspace for deletions
            event.setCanceled(validInput.matcher((Character.toString(Keyboard.getEventCharacter()))).find() || ctrlKeyCombo(textSelectionCodes) || Keyboard.isKeyDown(Keyboard.KEY_BACK));
        }
    }

    /**
     * Checks if any of the ctrl key combos are present.
     * @param keyCodes An array of keycodes to check.
     * @return Whether a ctrl key is combined with any of these keycodes supplied.
     */
    public static boolean ctrlKeyCombo(int[] keyCodes) {
        boolean flag = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
        boolean flag1 = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
        boolean flag2 = Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU);
        if(flag1 || flag2) return false;
        if(flag) {
            for(int i : keyCodes) {
                if(Keyboard.isKeyDown(i)) return true;
            }
        }
        return false;
    }

    private boolean _flag_ = true;

    @SubscribeEvent
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre event) {
        GuiScreen gui = event.getGui();
        if(gui == null) return;
        if(!Main.enabled) return;
        if(Minecraft.getMinecraft().player == null) return;
        if(Minecraft.getMinecraft().player.inventory.getItemStack().isEmpty()) return;
        if(Mouse.getEventButton() != 0) return;
        if(_flag_) {
            _flag_ = false;
            if(!Mouse.isButtonDown(0)) {
                event.setCanceled(true);
                return;
            }
        }
        List<GuiButton> buttonList = ((IMixinGuiScreen) gui).getButtonList();
        GuiButton btn = isMouseOverBtn(buttonList);
        if(btn == null) return;
        if(gui instanceof GuiContainer) {
                MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.ActionPerformedEvent.Post(gui, btn, buttonList));
                event.setCanceled(true);
                _flag_ = true;
                return; // prevent further operations
        }
        if(isTextFieldHovered(textField)) {
            textField.setFocused(true);
            event.setCanceled(true);
            _flag_ = true;
        }
    }

    /**
     *
     * @param buttonList List of GuiButtons for check
     * @return GuiButton if mouse over, otherwise none.
     */
    @Nullable
    private GuiButton isMouseOverBtn(List<GuiButton> buttonList) {
        for(GuiButton btn : buttonList) {
            if(btn.isMouseOver())
            {
                return btn;
            }
        }
        return null;
    }

    public static boolean isTextFieldHovered(GuiTextField tf) {
        if(tf == null) return false;
        return mouseX >= tf.x && mouseY >= tf.y && mouseX < tf.x + tf.width && mouseY < tf.y + tf.height;
    }

    @SubscribeEvent
    public void onMouseInput(GuiScreenEvent.MouseInputEvent.Post event) {
        if(!(event.getGui() instanceof GuiContainer)) return;
        if(textField == null) return;
        if(!Main.enabled) {
            textField.setFocused(false);
        }
        if(Mouse.getEventButton() != 0) return;
        textField.setFocused(isTextFieldHovered(textField));
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

        if (event.getGui() instanceof GuiContainer) {
            final FontRenderer fontRenderer = mc.fontRenderer;
            final ScaledResolution sr = new ScaledResolution(mc);
            final int cmd_x = (sr.getScaledWidth() + 225) / 2;
            final int cmdField_y = (sr.getScaledHeight() - 125) / 2;
            final int cmdBtn_y = (sr.getScaledHeight() - 75) / 2;
            final int btn3_x = (sr.getScaledWidth() + 225) / 2;
            final int btn3_y = (sr.getScaledHeight() + 75) / 2;
            final int btn4_x = (sr.getScaledWidth() + 225) / 2;
            final int btn4_y = (sr.getScaledHeight() + 125) / 2;
            final int debug_x = (sr.getScaledWidth() + 225) / 2;
            final int debug_y = (sr.getScaledHeight() + 25) / 2;
            event.getButtonList().add(new GuiButton(100, btn3_x, btn3_y, "Close GUI silently"));
            event.getButtonList().add(new GuiButton(102, btn4_x+105, btn4_y, 95, 20, "TouchScreen" + (isTouchscreen ? TextFormatting.GREEN + " ON" : TextFormatting.RED + " OFF")));
            event.getButtonList().add(new GuiButton(103, btn4_x, btn4_y, 95, 20, "Packets" + (PacketHandler.isPacketReceiving() ? TextFormatting.GREEN + " ON" : TextFormatting.RED + " OFF")));
            event.getButtonList().add(new GuiButton(110, cmd_x, cmdBtn_y, 95, 20, "Send Message"));
            event.getButtonList().add(new GuiButton(111, cmd_x+105, cmdBtn_y, 95, 20, "Invoke Client Command"));
            event.getButtonList().add(new GuiButton(1002, cmd_x+170, cmdField_y, 30, 20, doPersist ? "O" : "X"));
            event.getButtonList().add(new GuiButton(999, debug_x, debug_y, "Debug Button"));
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
        if(event.getGui() instanceof GuiChat) return; // chat
        // Prevents touchscreen misinput exit via mouse
        if(preventClosing && event.getGui() == null) {
            preventClosing = false;
            event.setCanceled(true);
        }
        // If lastGui exists and a non-null GUI pops up, discards saved GUI as it's no longer valid
        if(event.getGui() != null && lastGui != null) {
            message.send("Your last silently closed GUI result have been automatically discarded.");
            lastGui = null;
        }
    }

    @SubscribeEvent
    public void onPostActionPerformedGui(final GuiScreenEvent.ActionPerformedEvent.Post event) {
        if(!Main.enabled) return;
        discardItem = false;
        final Minecraft mc = Minecraft.getMinecraft();
        if (event.getGui() instanceof GuiContainer) {
            if(isTouchscreen) preventClosing = true;
            switch(event.getButton().id) {
                case 100:
                    preventClosing = false;
                    lastGui = event.getGui();
                    mc.player.sendMessage(new TextComponentString("Silently closed GUI. Press U (Default) to reopen it."));
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
                case 999:
                    ItemStack itemStack = mc.player.inventory.getItemStack();
                    if(!itemStack.isEmpty()) {
                        message.send("Holding item " + itemStack.getDisplayName() + " x" + itemStack.getCount());
                    }
                    break;
            }
            if(!isTouchscreen)
                preventClosing = false;
        }
    }

    public static @Nullable GuiScreen getLastGui() {
        return lastGui;
    }
}
