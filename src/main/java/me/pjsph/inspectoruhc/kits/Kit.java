package me.pjsph.inspectoruhc.kits;

import org.bukkit.ChatColor;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Kit {

    private static HashMap<UUID, Kit> kitOwners = new HashMap<>();

    private String name;
    private String description;
    private KIT_TYPES kitType;

    public Kit(KIT_TYPES kitType) {
        this(kitType.name, kitType.description, kitType);
    }

    private Kit(String name, String description, KIT_TYPES kitType) {
        this.name = name;
        this.description = description;
        this.kitType = kitType;
    }

    public void addOwner(UUID owner) {
        if(kitOwners.containsKey(owner))
            kitOwners.remove(owner);

        kitOwners.put(owner, this);
    }

    public void removeOwner() {
        UUID uuid = kitOwners.keySet().stream().filter(id -> kitOwners.get(id) == this).findFirst().orElse(null);

        if(uuid != null) {
            if(kitOwners.containsKey(uuid))
                kitOwners.remove(uuid);
        }
    }

    public UUID getOwner() {
        UUID uuid = kitOwners.entrySet().stream().filter(entry -> entry.getValue() == this).map(Map.Entry::getKey).findFirst().orElse(null);

        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public KIT_TYPES getKitType() {
        return kitType;
    }

    public static List<UUID> getOwners(KIT_TYPES kitType) {
        return kitOwners.entrySet().stream()
                .filter(e -> e.getValue().getKitType() == kitType)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static Kit getKit(UUID owner) {
        if(kitOwners.get(owner) == null) return null;

        return kitOwners.get(owner);
    }

    /**
     * All the kits who can be assigned to an Inspector.
     */
    public enum KIT_TYPES {

        /**
         * Allow the player (Inspector) to know the team of one player.
         */
        SPY_GLASSES("Lunettes d'espion", "Permet d'espionner un joueur afin de connaître son équipe (/spy <player>)."),

        /**
         * Give Speed effect to the player so he can move easier than others.
         */
        AGILITY("Agilité", "Permet d'obtenir un effet de Speed."),

        /**
         * Give to the player a Thief's position.
         */
        UNDERSENSE("Sixième sens", "Permet d'obtenir la position des " + ChatColor.RED + "Criminels §3à moins de cent blocs de vous, à chaque épisode."),

        /**
         * Allow the player to respawn 1 time when he dies.
         */
        ROUGHNECK("Dur à cuire", "Permet de réapparaitre lors de votre première mort.");

        private String name;
        private String description;

        KIT_TYPES(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

}

