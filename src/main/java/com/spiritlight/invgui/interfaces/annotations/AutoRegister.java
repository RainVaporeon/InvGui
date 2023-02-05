package com.spiritlight.invgui.interfaces.annotations;

import net.minecraft.command.CommandBase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate with this annotation to automatically register commands
 * and event listeners without having to call per-instance registers.<br>
 * <br>
 * Super useful for people like me who forgets it all the time.
 * @apiNote The {@code name} field will default to "example", and the {@code permission} field will default to 4.<br>
 * The {@code aliases} field will default to an empty array, and requirePrefix is false by default.
 * @author RainVaporeon
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoRegister {
    /**
     * The command name.<br>
     * <br>
     * If unassigned, it'll be "example" by default.
     */
    String name() default "example";

    /**
     * The permission number, ranging from 0 to 4.<br>
     * This determines what's returned in getRequiredPermissionLevel if the command implementation
     * is {@link com.spiritlight.invgui.utils.SpiritCommand}, or any underlying implementation
     * that supports this.
     * @see CommandBase#getRequiredPermissionLevel()
     */
    int permission() default 4;

    /**
     * The list of aliases. This will be turned into a List by {@link java.util.Arrays#asList(Object[])} by {@link com.spiritlight.invgui.utils.SpiritCommand}.
     */
    String[] aliases() default {};

    /**
     * Whether this command requires the "/" prefix to be executed. Default false.
     */
    boolean requirePrefix() default false;
}
