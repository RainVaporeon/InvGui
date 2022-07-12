package com.spiritlight.invgui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class GuiHandler {
    private static final List<GuiScreen> savedGuiList = new ArrayList<GuiScreen>(4) {{
        add(0, null);
        add(1, null);
        add(2, null);
        add(3, null);
    }};

    @SubscribeEvent
    public void onInitGui(final GuiScreenEvent.InitGuiEvent.Post event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null || mc.ingameGUI == null) {
            return;
        }
        if (Minecraft.getMinecraft().player.inventory == null) {
            return;
        }
        final ScaledResolution sr = new ScaledResolution(mc);
        final int savedGui_x = (sr.getScaledWidth() + 225) / 2;
        final int loadGui_y = (sr.getScaledHeight() - 75) / 2;
        final int saveGui_y = (sr.getScaledHeight() - 25) / 2;
        final int btn3_x = (sr.getScaledWidth() + 225) / 2;
        final int btn3_y = (sr.getScaledHeight() + 25) / 2;
        final int btn4_x = (sr.getScaledWidth() + 225) / 2;
        final int btn4_y = (sr.getScaledHeight() + 75) / 2;
        if (event.getGui() instanceof GuiContainer) {
            event.getButtonList().add(new GuiButton(100, btn3_x, btn3_y, "Close GUI silently"));
            event.getButtonList().add(new GuiButton(103, btn4_x, btn4_y, "Toggle Packets" + (PacketHandler.isPacketReceiving() ? TextFormatting.GREEN + " ON" : TextFormatting.RED + " OFF")));
            event.getButtonList().add(new GuiButton(104, savedGui_x, loadGui_y, 60, 20, "Load GUI 1"));
            event.getButtonList().add(new GuiButton(105, savedGui_x+70, loadGui_y, 60, 20, "Load GUI 2"));
            event.getButtonList().add(new GuiButton(106, savedGui_x+140, loadGui_y, 60, 20, "Load GUI 3"));
            event.getButtonList().add(new GuiButton(107, savedGui_x, saveGui_y, 60, 20, "Save GUI 1"));
            event.getButtonList().add(new GuiButton(108, savedGui_x+70, saveGui_y, 60, 20, "Save GUI 2"));
            event.getButtonList().add(new GuiButton(109, savedGui_x+140, saveGui_y, 60, 20, "Save GUI 3"));
        }
    }

    @SubscribeEvent
    public void onPostActionPerformedGui(final GuiScreenEvent.ActionPerformedEvent.Post event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (event.getGui() instanceof GuiContainer) {
            switch(event.getButton().id) {
                case 100:
                    mc.player.sendMessage(new TextComponentString("Silently closed GUI."));
                    PacketHandler.discardPacket("CPacketCloseWindow", PacketHandler.Enum.WRITE);
                    mc.player.closeScreen();
                    break;
                case 103:
                    PacketHandler.setPacketReceiving(!PacketHandler.isPacketReceiving());
                    message.send("Toggled packet sending to " + PacketHandler.isPacketReceiving());
                    event.getButton().displayString = "Toggle Packets" + (PacketHandler.isPacketReceiving() ? TextFormatting.GREEN + " ON" : TextFormatting.RED + " OFF");
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
            }
        }
    }

    protected static boolean loadGui(int id) {
        if(savedGuiList.get(id) == null) return false;
        Minecraft.getMinecraft().displayGuiScreen(savedGuiList.get(id));
        return true;
    }

    protected static void saveGui(int id, GuiScreen guiScreen) {
        if(id < 0 || id >= 3) return;
        savedGuiList.set(id, guiScreen);
    }
}
