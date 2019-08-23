package me.pjsph.inspectoruhc.tools;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class IUSound {
    private Sound sound = null;
    private Float volume = 1f;
    private Float pitch = 1f;

    public IUSound(Sound sound) {
        this.sound = sound;
    }

    public IUSound(Sound sound, Float volume, Float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public void play(Player player) {
        play(player, player.getLocation());
    }

    public void play(Player player, Location location) {
        player.playSound(location, sound, volume, pitch);
    }

    public void broadcast() {
        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            play(player);
        }
    }

    public Sound getSound() {
        return sound;
    }

    public Float getVolume() {
        return volume;
    }

    public Float getPitch() {
        return pitch;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
    }

    public void setPitch(Float pitch) {
        this.pitch = pitch;
    }
}
