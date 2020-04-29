package me.pjsph.inspectoruhc.timer;

public class TimerManager {

    private int minutesLeft = 0;
    private int secondsLeft = 0;
    private int minutesRolesLeft = 0;
    private int minutesKitsLeft = 0;

    private int episode = 0;

    private int minutesBorderLeft = 0;
    private int minutesPvpLeft = 0;

    public int getMinutesLeft() {
        return minutesLeft;
    }

    public void setMinutesLeft(int minutesLeft) {
        this.minutesLeft = minutesLeft;
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public void setSecondsLeft(int secondsLeft) {
        this.secondsLeft = secondsLeft;
    }

    public int getMinutesRolesLeft() {
        return minutesRolesLeft;
    }

    public void setMinutesRolesLeft(int minutesRolesLeft) {
        this.minutesRolesLeft = minutesRolesLeft;
    }

    public int getMinutesKitsLeft() {
        return minutesKitsLeft;
    }

    public void setMinutesKitsLeft(int minutesKitsLeft) {
        this.minutesKitsLeft = minutesKitsLeft;
    }

    public int incMinutesLeft() {
        return ++this.minutesLeft;
    }

    public int decMinutesLeft() {
        return --this.minutesLeft;
    }

    public int incSecondsLeft() {
        return ++this.secondsLeft;
    }

    public int decSecondsLeft() {
        return --this.secondsLeft;
    }

    public int incMinutesRolesLeft() {
        return ++this.minutesRolesLeft;
    }

    public int decMinutesRolesLeft() {
        return --this.minutesRolesLeft;
    }

    public int incMinutesKitsLeft() {
        return ++this.minutesKitsLeft;
    }

    public int decMinutesKitsLeft() {
        return --this.minutesKitsLeft;
    }

    public int getEpisode() {
        return episode;
    }

    public int incEpisode() {
        return ++this.episode;
    }

    public int getMinutesBorderLeft() {
        return minutesBorderLeft;
    }

    public void setMinutesBorderLeft(int minutesBorderLeft) {
        this.minutesBorderLeft = minutesBorderLeft;
    }

    public int decMinutesBorderLeft() {
        return --this.minutesBorderLeft;
    }

    public int getMinutesPvpLeft() {
        return minutesPvpLeft;
    }

    public void setMinutesPvpLeft(int minutesPvpLeft) {
        this.minutesPvpLeft = minutesPvpLeft;
    }

    public int decMinutesPvpLeft() {
        return --this.minutesPvpLeft;
    }
}
