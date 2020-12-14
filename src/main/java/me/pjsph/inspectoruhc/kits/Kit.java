package me.pjsph.inspectoruhc.kits;

import me.pjsph.inspectoruhc.game.IUPlayer;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Kit {

    private static HashMap<IUPlayer, Kit> kitOwners = new HashMap<>();

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

    public void addOwner(IUPlayer owner) {
        if(kitOwners.containsKey(owner))
            kitOwners.remove(owner);

        kitOwners.put(owner, this);
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

    public static List<IUPlayer> getOwners(KIT_TYPES kitType) {
        return kitOwners.entrySet().stream()
                .filter(e -> e.getValue().getKitType() == kitType)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static Kit getKit(IUPlayer owner) {
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
        SPY_GLASSES("(⌐■_■) Lunettes d'espion", "§3Vous pouvez espionner un joueur pour connaître son identité (/spy <player>)."),

        /**
         * Give Speed effect to the player so he can move easier than others.
         */
        AGILITY(">>---> Agilité", "§3Vous obtenez un effet de Speed."),

        /**
         * Give to the player a Thief's position.
         */
        UNDERSENSE("(■) Sixième sens", "§3Vous obtenez la position des §cCriminels §3à moins de cent blocs de vous, à chaque épisode."),

        /**
         * Allow the player to respawn 1 time when he dies.
         */
        ROUGHNECK("|▼| Dur à cuire", "§3Vous réapparaitrez lors de votre première mort.");

        private String name;
        private String description;

        KIT_TYPES(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

}

