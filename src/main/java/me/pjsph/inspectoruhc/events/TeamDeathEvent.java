package me.pjsph.inspectoruhc.events;


import me.pjsph.inspectoruhc.teams.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamDeathEvent extends Event {

    private Team team;

    public TeamDeathEvent(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
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
