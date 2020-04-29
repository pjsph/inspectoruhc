package me.pjsph.inspectoruhc.events;

import me.pjsph.inspectoruhc.game.IUPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

import lombok.Getter;

public class ActivateAuraEvent extends Event {

    @Getter private IUPlayer player;

    public ActivateAuraEvent(IUPlayer player) {
        this.player = player;
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
