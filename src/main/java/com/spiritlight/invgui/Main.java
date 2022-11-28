package com.spiritlight.invgui;

import com.spiritlight.invgui.cat.Cat;
import com.spiritlight.invgui.configs.Configurations;
import com.spiritlight.invgui.exceptions.ProcessException;
import com.spiritlight.invgui.interfaces.annotations.AnnotationProcess;
import net.minecraft.util.Session;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main
{
    public static final String MODID = "origasm";
    public static final String NAME = "InvGUI";
    public static final String VERSION = "1.4";
    public static boolean enabled = false;
    public static Hide hideStatus = Hide.ON;
    /**
     * a must-have luxury
     */
    public static Cat cat;
    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    public static final Set<String> accountHash = new HashSet<>();
    public static final Map<String, Session> sessionMap = new HashMap<>();
    public static long cachedPassword = 0;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) throws ProcessException {
        AnnotationProcess.invoke();
        KeyBindings.register();
        // Not going to bother catching this as proper error handling should already have been handled.
        Configurations.getConfig();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // me
        cat = new Cat("Rain", "FishCat", 200);
        executor.scheduleAtFixedRate(action, 60, 60, TimeUnit.SECONDS);
    }

    public enum Hide {
        ON, OFF, SELF
    }

    private final Runnable action = () -> {
        if(!Objects.equals(cat.getName(), "Rain") || !Objects.equals(cat.getBreed(), "FishCat")) {
            throw new RuntimeException("Rain check failed.");
        }
        cat.randomAction();
    };
}
