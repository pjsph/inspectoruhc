package me.pjsph.inspectoruhc.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EpisodeChangedEvent extends Event {

    private int newEpisode;
    private EpisodeChangedCause cause;
    private String shifter;

    public EpisodeChangedEvent(int newEpisode, EpisodeChangedCause cause, String shifter) {
        this.newEpisode = newEpisode;
        this.cause = cause;
        this.shifter = shifter;
    }

    public int getNewEpisode() {
        return newEpisode;
    }

    public EpisodeChangedCause getCause() {
        return cause;
    }

    public String getShifter() {
        return shifter;
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
