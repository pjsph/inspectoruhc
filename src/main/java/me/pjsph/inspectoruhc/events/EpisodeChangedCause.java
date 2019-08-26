package me.pjsph.inspectoruhc.events;

public enum EpisodeChangedCause {

    /**
     * The episode changed because the timer reach the end of the episode
     */
    FINISHED,

    /**
     * The episode changed because the previous episode was shifted by someone using
     * the {@code /iu shift} command.
     */
    SHIFTED

}
