package com.spiritlight.invgui.utils;

import com.spiritlight.invgui.interfaces.annotations.AutoRegister;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.IClientCommand;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Utility command implementation with slightly more flexibility.<br>
 * <br>
 * Features of this specific implementation:<br>
 * - Supports {@link AutoRegister#permission()}: Allows the user to set direct permissions easier.<br>
 * - Supports {@link AutoRegister#name()}: Allows the user to easily implement command name. Defaults to "example"<br>
 * - Supports {@link AutoRegister#aliases()}: Allows the user to quickly construct aliases for command.<br>
 * - Supports {@link AutoRegister#requirePrefix()}: By implementing {@link IClientCommand}, this class can decide whether
 * this command needs a "/" prefix to be executed. Default false.
 * - {@link CommandBase#getUsage(ICommandSender)} is implemented to return {@code "/getName()"} by default.<br>
 */
@ParametersAreNonnullByDefault @MethodsReturnNonnullByDefault
public abstract class SpiritCommand extends CommandBase implements IClientCommand {
    // assert annotation != null if flag == true
    private final boolean flag = this.getClass().isAnnotationPresent(AutoRegister.class);
    private final AutoRegister annotation = this.getClass().getAnnotation(AutoRegister.class);
    private final List<String> aliases = Arrays.asList(annotation.aliases());
    private final List<String> EMPTY_LIST = Collections.emptyList();

    @Override
    public List<String> getAliases() {
      if(flag) {
            return aliases;
        }
        return EMPTY_LIST;
    }

    @Override
    public String getName() {
      if(flag) {
            return annotation.name();
        }
        return "example";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + getName();
    }

    @Override
    public int getRequiredPermissionLevel() {
        if (flag) {
            return annotation.permission();
        } else {
            return 4;
        }
    }

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        if(flag) {
            return !annotation.requirePrefix();
        }
        return true;
    }
}
