package me.pjsph.inspectoruhc.events;

import me.pjsph.inspectoruhc.teams.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameEndsEvent extends Event {

    private Team winner;

    public GameEndsEvent(Team winner) {
        this.winner = winner;
    }

    public Team getWinnerTeam() {
        return winner;
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
