package com.spiritlight.invgui.mixins;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nonnull;
import java.util.List;

@Mixin(GuiScreen.class)
public interface IMixinGuiScreen {
    @Accessor
    @Nonnull
    List<GuiButton> getButtonList();
}
