package me.pjsph.inspectoruhc.events;

import lombok.*;
import me.pjsph.inspectoruhc.game.IUPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UpdatePrefixEvent extends Event {

    @Getter private final IUPlayer player, to;

    @Getter @Setter private String prefix;

    public UpdatePrefixEvent(IUPlayer player, IUPlayer to, String prefix) {
        this.player = player;
        this.to = to;
        this.prefix = prefix;
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
