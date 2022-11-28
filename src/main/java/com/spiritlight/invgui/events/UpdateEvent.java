package com.spiritlight.invgui.events;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * A generic UpdateEvent that hooks to {@link EntityPlayerSP#onLivingUpdate()}.
 */
public class UpdateEvent extends Event {
    private final EntityPlayerSP player;

    public EntityPlayerSP getPlayer() {
        return this.player;
    }

    public UpdateEvent(EntityPlayerSP player) {
        this.player = player;
    }
}
