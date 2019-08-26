package me.pjsph.inspectoruhc.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class DesactivateAuraEvent extends Event {

    private UUID playerUUID;

    public DesactivateAuraEvent(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    private static HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
