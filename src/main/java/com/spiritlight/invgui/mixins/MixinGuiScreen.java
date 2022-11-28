package com.spiritlight.invgui.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GuiScreen.class)
public class MixinGuiScreen {

    @Shadow
    public Minecraft mc;

    // Current issue: Execution fail -> Send actual command aswell
    // Uses of this mixin: Replaces the last two lines with our own execution
    // Adds a check to make sure it's a command
    // Fully overriding this method, however.
    /**
     * Method that patches sendChatMessage
     * @author RainVaporeon
     * @reason Patches the bug where slash is not required to execute a command
     */
    @Overwrite
    public void sendChatMessage(String msg, boolean addToChat) {
        msg = net.minecraftforge.event.ForgeEventFactory.onClientSendMessage(msg);
        if (msg.isEmpty()) return;
        if (addToChat)
        {
            this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
        }
        if(msg.startsWith("/"))
        {
            if (net.minecraftforge.client.ClientCommandHandler.instance.executeCommand(mc.player, msg) != 0) return;
        }
        this.mc.player.sendChatMessage(msg);
    }
}
