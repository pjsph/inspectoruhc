package me.pjsph.inspectoruhc.game;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.events.GameStartsEvent;
import me.pjsph.inspectoruhc.kits.Kit;
import me.pjsph.inspectoruhc.teams.Team;
import me.pjsph.inspectoruhc.timer.Timer;
import me.pjsph.inspectoruhc.tools.Titles;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

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

    private int aliveTeamsCount = 0;

    private Teleporter teleporter = null;

    public GameManager(InspectorUHC plugin) {
        this.plugin = plugin;
    }

    public void initPlayer(final Player player) {
        Location loc = player.getWorld().getSpawnLocation().add(0.5, 0.5, 0.5);
        player.teleport(loc);

        player.setFoodLevel(20);
        player.setSaturation(20f);
        player.setHealth(20d);

        plugin.getSpectatorsManager().setSpectating(player, false);

        player.removeAchievement(Achievement.OPEN_INVENTORY);

        if(player.isOp()) {
            player.setGameMode(GameMode.CREATIVE);
        } else {
            player.setGameMode(GameMode.ADVENTURE);
        }
    }

    public void start(CommandSender sender) {
        if(!hasStarted()) {

            /* Initialization of the teams */

            alivePlayers.clear();
            aliveTeamsCount = 0;

            Team.getTeams().forEach(team -> team.clear());

            startRandomizeRoles(sender);

            /* Initialization of the players */

            Team.getTeams().forEach(
                            team -> team.getPlayers().stream()
                                        .map(OfflinePlayer::getUniqueId)
                                        .filter(id -> !spectators.contains(id))
                                        .forEach(id -> alivePlayers.add(id))
            );

            updateAliveCache();

            /* Spawns checks */
            int spawnNeeded = getAlivePlayers().size();

            if(plugin.getSpawnsManager().getSpawnPoints().size() < spawnNeeded) {
                if(sender instanceof Player) sender.sendMessage("");
                sender.sendMessage("§cImpossible de démarrer le jeu: pas assez de points de spawn.");
                sender.sendMessage("§cUtilisez §o/iu spawns generate §cpour générer les points manquants.");

                aliveTeamsCount = 0;
                return;
            }

            /* Initialization of the spectator mode */

            Bukkit.getOnlinePlayers().forEach(p -> plugin.getSpectatorsManager().setSpectating(p, spectators.contains(p.getUniqueId())));

            /* Start messages */
            plugin.getServer().broadcastMessage(ChatColor.GREEN + "La partie démarre...");

            plugin.getServer().broadcastMessage(ChatColor.GREEN + "3");

            Titles.broadcastTitle(2, 16, 2, "§a3", "");

            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                public void run() {
                    plugin.getServer().broadcastMessage(ChatColor.YELLOW + "2");

                    Titles.broadcastTitle(2, 16, 2, "§e2", "");
                }
            }, 20L);
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                public void run() {
                    plugin.getServer().broadcastMessage(ChatColor.RED + "1");

                    Titles.broadcastTitle(2, 16, 2, "§c1", "");
                }
            }, 40L);
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                public void run() {
                    plugin.getServer().broadcastMessage(ChatColor.DARK_AQUA + "C'est parti !");

                    /* TODO Teleportation */
                    teleporter = new Teleporter();

                    List<Location> spawnPoints = new ArrayList<>(plugin.getSpawnsManager().getSpawnPoints());
                    Collections.shuffle(spawnPoints);

                    Queue<Location> unusedTp = new ArrayDeque<>(spawnPoints);

                    Team.getTeams().stream().filter(team -> !team.isEmpty()).forEach(team -> {
                        team.getPlayersUUID().forEach(player -> {
                            final Location playerSpawn = unusedTp.poll();
                            final Cage cage = new Cage(playerSpawn, true, true);
                            cage.setCustomMaterial(Material.STAINED_GLASS, (byte) 5);
                            cage.setInternalHeight(3);
                            cage.setRadius(5);

                            teleporter.setSpawnForPlayer(player, playerSpawn);
                            if(cage != null) teleporter.setCageForPlayer(player, cage);
                        });
                    });

                    teleporter
                            .whenTeleportationSuccesses(uuid -> {
                                final Player player = Bukkit.getPlayer(uuid);

                                player.setGameMode(GameMode.SURVIVAL);

                                /* Reset player */
                                resetPlayer(player);

                                player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
                            })
                            .whenTeleportationFails(uuid -> sender.sendMessage("§cLe joueur " + Bukkit.getPlayer(uuid).getName() + " ne peut pas être téléporté !"))
                            .whenTeleportationEnds(uuids -> {
                                /* Initialization of the environment */
                                startEnvironment();

                                /* Initialization of the timer */
                                plugin.getTimerManager().setMinutesLeft(20);
                                plugin.getTimerManager().incEpisode();
                                plugin.getTimerManager().setMinutesRolesLeft(5);
                                plugin.getTimerManager().setMinutesKitsLeft(10);

                                setStarted(true);

                                /* Schedule damages */
                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    activateDamages();
                                    plugin.getServer().broadcastMessage(ChatColor.RED + "Attention ! Vous n'êtes plus invincible !");
                                }, 660L);

                                /* Finalize start */
                                finalizeStart();
                            })
                            .startTeleportationProcess();
                }
            }, 60L);
        }
    }

    private void startEnvironment() {
        World overworld = Bukkit.getWorlds().stream()
                .filter(world -> world.getEnvironment() != World.Environment.NETHER && world.getEnvironment() != World.Environment.THE_END)
                .findFirst().orElse(null);

        if(overworld != null) {
            overworld.setGameRuleValue("doDaylightCycle", "true");
            overworld.setTime(6000L);
            overworld.setStorm(false);
        }

        for(World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("keepInventory", "false");
            world.setGameRuleValue("naturalRegeneration", "false");
            world.setDifficulty(Difficulty.HARD);
        }
    }

    private void finalizeStart() {
        new Timer(InspectorUHC.get()).runTaskTimer(InspectorUHC.get(), 20L, 20L);

        teleporter.cleanup();

        hasStarted = true;

        updateAliveCache();

        plugin.getServer().getOnlinePlayers().stream()
                .filter(player -> alivePlayers.contains(player.getUniqueId()))
                .forEach(player -> {
                    player.setGameMode(GameMode.SURVIVAL);
                    resetPlayer(player);
        });

        plugin.getServer().getPluginManager().callEvent(new GameStartsEvent());
    }

    private void resetPlayer(Player player) {
        player.setExp(0f);
        player.setLevel(0);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setHealth(20D);
        player.setExhaustion(20F);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.getActivePotionEffects().clear();
    }

    public void finish() {
        if(!hasStarted()) {
            throw new IllegalStateException("Cannot finish the game: the game is not started.");
        }

        if(getAliveTeamsCount() != 1) {
            throw new IllegalStateException("Cannot finish the game: more/less than one team are alive.");
        }

        Team winnerTeam = getAliveTeams().size() == 0 ? Team.INSPECTORS : getAliveTeams().iterator().next();
        Set<OfflinePlayer> listWinners = winnerTeam.getPlayers();

        StringBuilder winners = new StringBuilder();
        int j = 0;

        for(OfflinePlayer winner : listWinners) {
            if(j != 0) {
                if(j == listWinners.size() - 1) {
                    winners.append(" ").append("and").append(" ");
                } else {
                    winners.append(", ");
                }
            }

            winners.append(winner.getName());
            j++;
        }

        Bukkit.broadcastMessage("§2Bravo à §e" + winners.toString() + " §2(" + winnerTeam.getColor() + winnerTeam.getName() + "§2) pour leur victoire !");

        /* Broadcast title */
        Titles.broadcastTitle(
                5, 142, 21,
                winnerTeam.getColor() + winnerTeam.getName(),
                "§aCette équipe remporte la partie !"
                );
    }

    private void startRandomizeRoles(CommandSender p) {
        Random random = new Random();

        // Select teams
        int rand = 0;

        ArrayList<Player> playersToChoose = new ArrayList<>(Bukkit.getOnlinePlayers().stream().filter(player -> !spectators.contains(player.getUniqueId())).collect(Collectors.toList()));
        ArrayList<Player> chosenPlayers = new ArrayList<>();

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

        /* Randomize kits */
        List<UUID> inspectors = new ArrayList<>(Team.INSPECTORS.getPlayersUUID());
        List<Kit.KIT_TYPES> listKits = null;
        for(int i = 0; i < inspectors.size(); i++) {
            if(listKits == null || listKits.size() == 0) listKits = new ArrayList<>(Arrays.asList(Kit.KIT_TYPES.values()));

            int randomIndex = random.nextInt(listKits.size());
            Kit kit = new Kit(listKits.get(randomIndex));
            kit.addOwner(inspectors.get(i));
            listKits.remove(randomIndex);

            plugin.getServer().getConsoleSender().sendMessage("§b" + Bukkit.getOfflinePlayer(inspectors.get(i)).getName() + " obtient le kit " + kit.getName() + ".");
        }
    }

    public void updateAliveCache() {
        Set<Team> aliveTeams = new HashSet<>();
        for(Team team : Team.values()) {
            for(UUID pid : team.getPlayersUUID()) {
                if(!this.isPlayerDead(pid)) aliveTeams.add(team);
            }
        }

        this.aliveTeamsCount = aliveTeams.size();
    }

    public Set<Team> getAliveTeams() {
        Set<Team> aliveTeams = new HashSet<>();
        for(Team team : Team.values()) {
            for(UUID pid : team.getPlayersUUID()) {
                if(!this.isPlayerDead(pid)) aliveTeams.add(team);
            }
        }

        return aliveTeams;
    }


    public void addDead(Player player) {
        alivePlayers.remove(player.getUniqueId());
    }

    public void addDead(UUID player) {
        alivePlayers.remove(player);
    }

    public boolean isPlayerDead(Player player) {
        return !alivePlayers.contains(player.getUniqueId());
    }

    public boolean isPlayerDead(UUID player) {
        return !alivePlayers.contains(player);
    }

    public void addStartupSpectator(OfflinePlayer player) {
        spectators.add(player.getUniqueId());
    }

    public void removeStartupSpectator(OfflinePlayer player) {
        spectators.remove(player.getUniqueId());
    }

    public HashSet<String> getStartupSpectators() {
        HashSet<String> spectatorNames = new HashSet<>();

        for(UUID id : spectators) {
            final OfflinePlayer player = Bukkit.getOfflinePlayer(id);
            final String playerName = player.getName();

            if(playerName != null) {
                spectatorNames.add(playerName);
            } else {
                spectatorNames.add("Unknown player with UUID " + player.getUniqueId());
            }
        }

        return spectatorNames;
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

    public int getAliveTeamsCount() {
        return aliveTeamsCount;
    }

    public void addDeathLocation(Player player, Location location) {
        deathLocations.put(player.getUniqueId(), location);
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

    public boolean isInvincible() {
        return invincible;
    }
}
