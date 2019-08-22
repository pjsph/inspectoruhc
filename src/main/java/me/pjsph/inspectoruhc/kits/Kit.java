package me.pjsph.inspectoruhc.kits;

import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Kit {

    private static HashMap<String, Kit> kitOwners = new HashMap<>();
    private static HashMap<Kits, Kit> fromKits = new HashMap<>();

    private String name;
    private String description;
    private Kits kit;
    private UUID owner;
    private boolean active = false;

    public Kit(String name, String description, Kits kit) {
        this.name = name;
        this.description = description;
        this.kit = kit;
        this.owner = null;

        if(kitOwners.containsValue(this))
            removeFromKitOwners();

        fromKits.put(this.kit, this);
    }

    private void addToKitOwners() {
        String sId;

        if(this.owner == null) {
            sId = "null_";
        } else {
            sId = owner.toString();
        }

        if(kitOwners.containsKey(sId))
            kitOwners.remove(sId);

        kitOwners.put(sId, this);
    }

    private void removeFromKitOwners() {
        String uuid = kitOwners.entrySet().stream().filter(e -> e.getValue() == this).map(Map.Entry::getKey).findFirst().get();

        if(kitOwners.containsKey(uuid))
            kitOwners.remove(uuid);
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        active = true;

        addToKitOwners();
    }

    public UUID getOwner() {
        return owner;
    }

    public boolean isActive() {
        return active;
    }

    public static Kit getFromOwner(UUID uuid) {
        if(kitOwners.containsKey(uuid.toString()))
            return kitOwners.get(uuid.toString());

        return null;
    }

    public static Kit getFromKits(Kits kit) {
        if(fromKits.containsKey(kit))
            return fromKits.get(kit);

        return null;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static HashMap<String, Kit> getKitOwners() {
        return kitOwners;
    }

    /**
     * All the kits who can be assigned to an Inspector.
     */
    public enum Kits {

        /**
         * Allow the player (Inspector) to know the team of one player.
         */
        KIT_SPY_GLASSES("Lunettes d'espion", "Permet d'espionner un joueur afin de connaître son équipe."),

        /**
         * Give Speed effect to the player so he can move easier than others.
         */
        KIT_AGILITY("Agilité", "Permet d'obtenir un effet de Speed."),

        /**
         * Give to the player a Thief's position.
         */
        KIT_UNDERSENSE("Sixième sens", "Permet d'obtenir la position d'un " + ChatColor.RED + "Criminel."),

        /**
         * Allow the player to respawn 1 time when he dies.
         */
        KIT_ROUGHNECK("Dur à cuire", "Permet de réapparaitre lors de votre première mort.");

        private String name;
        private String description;

        Kits(String name, String description) {
            this.name = name;
            this.description = description;
        }

        static {
            for(Kits kit : Kits.values()) {
                Kit.fromKits.put(kit, new Kit(kit.name, kit.description, kit));
            }
        }
    }

}

