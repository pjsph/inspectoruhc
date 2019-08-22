package me.pjsph.inspectoruhc.game;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.events.GameStartsEvent;
import me.pjsph.inspectoruhc.kits.Kit;
import me.pjsph.inspectoruhc.teams.Team;
import me.pjsph.inspectoruhc.timer.Timer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class GameManager {
    private InspectorUHC plugin;

    private boolean hasStarted = false;
    private boolean rolesActivated = false;
    private boolean kitsActivated = false;
    private boolean invincible = true;

    private Set<String> players = new HashSet<>();
    private Set<UUID> alivePlayers = new HashSet<>();
    private Set<UUID> spectators = new HashSet<>();
    private Map<UUID, Location> deathLocations = new HashMap<>();

    public GameManager(InspectorUHC plugin) {
        this.plugin = plugin;
    }

    public void initPlayer(final Player player) {
        Location loc = player.getWorld().getSpawnLocation().add(0.5, 0.5, 0.5);
        player.teleport(loc);

        player.setFoodLevel(20);
        player.setSaturation(20f);
        player.setHealth(20d);

        /* TODO scoreboard manager */

        plugin.getSpectatorsManager().setSpectating(player, false);

        player.removeAchievement(Achievement.OPEN_INVENTORY);

        if(player.isOp()) {
            player.setGameMode(GameMode.CREATIVE);
        } else {
            player.setGameMode(GameMode.ADVENTURE);
        }
    }

    public void start(Player player) {
        if(!hasStarted()) {

            /* Initialization of the teams */

            alivePlayers.clear();

            startRandomizeRoles(player);

            /* Initialization of the players */

            Team.getTeams().forEach(
                            team -> team.getPlayers().stream()
                                        .map(OfflinePlayer::getUniqueId)
                                        .filter(id -> !spectators.contains(id))
                                        .forEach(id -> alivePlayers.add(id))
            );

            /* Initialization of the spectator mode */

            Bukkit.getOnlinePlayers().forEach(p -> plugin.getSpectatorsManager().setSpectating(p, spectators.contains(p.getUniqueId())));

            /* Initialization of the timer */

            plugin.getTimerManager().setMinutesLeft(20);
            plugin.getTimerManager().incEpisode();
            plugin.getTimerManager().setMinutesRolesLeft(5);
            plugin.getTimerManager().setMinutesKitsLeft(10);

            setStarted(true);

            /* Start messages */

            plugin.getServer().broadcastMessage(ChatColor.GREEN + "La partie démarre...");

            plugin.getServer().broadcastMessage(ChatColor.GREEN + "3");
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                public void run() {
                    plugin.getServer().broadcastMessage(ChatColor.YELLOW + "2");
                }
            }, 20L);
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                public void run() {
                    plugin.getServer().broadcastMessage(ChatColor.RED + "1");
                }
            }, 40L);
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                public void run() {
                    plugin.getServer().broadcastMessage(ChatColor.DARK_AQUA + "C'est parti !");

                    new Timer(InspectorUHC.get()).runTaskTimer(InspectorUHC.get(), 20L, 20L);

                    for(World w : plugin.getServer().getWorlds()) {
                        w.setDifficulty(Difficulty.HARD);
                    }

                    for(String name : plugin.getGameManager().getPlayers()) {
                        Player player = Bukkit.getPlayer(name);
                        if(player == null || !player.isOnline()) return;

                        player.setGameMode(GameMode.SURVIVAL);
                        player.setExp(0f);
                        player.setLevel(0);
                        player.getInventory().clear();
                        player.getInventory().setArmorContents(new ItemStack[] { new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR) });
                        player.setHealth(20D);
                        player.setExhaustion(20F);
                        player.setFoodLevel(20);
                        player.getActivePotionEffects().clear();

                        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20, 255));
                    }
                }
            }, 60L);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                activateDamages();
                plugin.getServer().broadcastMessage(ChatColor.RED + "Attention ! Vous n'êtes plus invincible !");
            }, 660L);

            Bukkit.getPluginManager().callEvent(new GameStartsEvent());
        }
    }

    public void finish(int cause) {
    }

    private void startRandomizeRoles(Player p) {
        Random random = new Random();

        // Select teams
        int rand = 0;

        ArrayList<Player> playersToChoose = new ArrayList<>(Bukkit.getOnlinePlayers().stream().filter(player -> !spectators.contains(player.getUniqueId())).collect(Collectors.toList()));
        ArrayList<Player> chosenPlayers = new ArrayList<>();
        plugin.getLogger().log(Level.INFO,  "Teams: " + Team.values().length);

        int allPlayers = playersToChoose.size();

        for(int i = 0; i < allPlayers; i++) {
            boolean check = false;

            // Choose a team to put a player in
            while(check == false) {
                int randTeam = random.nextInt(Team.values().length);

                Team team = Team.values()[randTeam];
                plugin.getLogger().log(Level.INFO, "Team : " + team.getName() + ", Players : " + team.getPlayers().size());
                if(!(team.getPlayers().size() >= Math.floorDiv(allPlayers, 2))) {
                    while(check == false) {
                        rand = random.nextInt(playersToChoose.size());

                        if(!chosenPlayers.contains(playersToChoose.get(rand))) {
                            team.addPlayer(Bukkit.getOfflinePlayer(playersToChoose.get(rand).getUniqueId()));

                            plugin.getLogger().log(Level.INFO, "[SPOIL] " + playersToChoose.get(rand).getName() + " rejoint l'equipe " + team.getName());

                            chosenPlayers.add(playersToChoose.get(rand));
                            playersToChoose.remove(rand);

                            check = true;
                        }
                    }
                } else if(Team.INSPECTORS.getPlayers().size() == Team.THIEVES.getPlayers().size()) {
                    // Same amount of players in the two teams but a player is still teamless, we had him in the Inspectors team
                    if(playersToChoose.get(0) != null) {
                        Team.INSPECTORS.addPlayer(Bukkit.getOfflinePlayer(playersToChoose.get(0).getUniqueId()));

                        plugin.getLogger().log(Level.INFO, "[SPOIL] " + playersToChoose.get(0).getName() + " rejoint l'equipe Inspecteurs");

                        chosenPlayers.add(playersToChoose.get(0));
                        playersToChoose.remove(0);

                        p.sendMessage(ChatColor.RED + "Le nombre de joueurs connectés n'est pas pair. Les équipes ne seront pas de la même taille.\n" +
                                "L'équipe des " + ChatColor.DARK_AQUA + "Inspecteurs" + ChatColor.RED + " aura 1 joueur de plus.");

                        check = true;
                    }
                }
            }
        }

        // Select kits
        ArrayList<Kit> kits = new ArrayList<>();

        for(Kit.Kits kitEnum : Kit.Kits.values()) {
            Kit kit = Kit.getFromKits(kitEnum);

            if(kit != null) {
                kits.add(kit);
            }
        }

        for(Player player : Team.INSPECTORS.getOnlinePlayers()) {
            if(player != null) {
                UUID uuid = player.getUniqueId();

                Kit kit = kits.get(random.nextInt(kits.size()));
                if(kit != null) {
                    kit.setOwner(uuid);
                    kits.remove(kit);
                    plugin.getLogger().log(Level.INFO, ChatColor.AQUA + "[SPOIL] " + player.getName() + " obtient le kit " + kit.getName());
                }
            }
        }

    }

    public void updatePlayer(Player newPlayer) {

    }

    public void activateDamages() {
        this.invincible = false;
    }

    public boolean isRolesActivated() {
        return rolesActivated;
    }

    public void activateRoles() {
        this.rolesActivated = true;
    }

    public boolean isKitsActivated() {
        return kitsActivated;
    }

    public void activateKits() {
        this.kitsActivated = true;
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    public void setStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    public Set<String> getPlayers() {
        return players;
    }

    public Set<OfflinePlayer> getAlivePlayers() {
        return alivePlayers.stream()
                .map(id -> plugin.getServer().getOfflinePlayer(id))
                .collect(Collectors.toSet());
    }

    public HashSet<Player> getOnlineAlivePlayers() {
        return alivePlayers.stream()
                .map(id -> plugin.getServer().getPlayer(id))
                .filter(Objects::nonNull)
                .filter(Player::isOnline)
                .collect(Collectors.toCollection(HashSet::new));
    }
}
