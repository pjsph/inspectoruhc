package me.pjsph.inspectoruhc.events;

import me.pjsph.inspectoruhc.game.IUPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

public class DesactivateAuraEvent extends Event {

    @Getter private IUPlayer player;

    public DesactivateAuraEvent(IUPlayer player) {
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
