package me.pjsph.inspectoruhc.events;

import me.pjsph.inspectoruhc.teams.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameEndsEvent extends Event {

    private Team winner;
    private boolean forced;

    public GameEndsEvent(Team winner, boolean forced) {
        this.winner = winner;
        this.forced = forced;
    }

    public GameEndsEvent(Team winner) {
        this.winner = winner;
        forced = false;
    }

    public Team getWinnerTeam() {
        return winner;
    }

    public boolean isForced() { return forced; }

    private static HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
