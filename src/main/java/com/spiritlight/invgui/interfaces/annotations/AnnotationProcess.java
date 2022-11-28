package com.spiritlight.invgui.interfaces.annotations;

import net.minecraft.command.ICommand;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AnnotationProcess {
    private static boolean invoked = false;

    private AnnotationProcess() {
        throw new AssertionError("com.spiritlight.invgui.interfaces.annotations.AnnotationProcess cannot be instantiated!");
    }

    public static void invoke() {
        if(invoked) {
            System.out.println("Already processed annotations!");
            return;
        }
        invoked = true;
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("com.spiritlight.invgui"))
                .setScanners(Scanners.TypesAnnotated, Scanners.SubTypes)
                .setParallel(true));
        Set<Class<? extends ICommand>> classSet = reflections.getSubTypesOf(ICommand.class);
        for(Class<? extends ICommand> clazz : classSet) {
            if(clazz.isAnnotationPresent(AutoRegister.class)) {
                try {
                    ClientCommandHandler.instance.registerCommand(clazz.newInstance());
                    System.out.println("Successfully registered " + clazz.getCanonicalName() + " as a command.");
                } catch (InstantiationException | IllegalAccessException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        Set<Class<?>> busSet = reflections.getTypesAnnotatedWith(AutoSubscribe.class);
        for(Class<?> subscriber : busSet) {
            try {
                MinecraftForge.EVENT_BUS.register(subscriber.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
