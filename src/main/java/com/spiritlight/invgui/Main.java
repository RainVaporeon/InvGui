package com.spiritlight.invgui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main
{
    public static final String MODID = "origasm";
    public static final String NAME = "InvGUI";
    public static final String VERSION = "1.0";
    static boolean enabled = false;
    static GuiScreen savedGui = null;



    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new ConnectionEvent());
        ClientCommandHandler.instance.registerCommand(new Command());
        MinecraftForge.EVENT_BUS.register(new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new KeyBindings());
        KeyBindings.register();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }
}
