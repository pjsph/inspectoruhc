package me.pjsph.inspectoruhc.game;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.events.EpisodeChangedCause;
import me.pjsph.inspectoruhc.events.EpisodeChangedEvent;
import me.pjsph.inspectoruhc.events.GameStartsEvent;
import me.pjsph.inspectoruhc.events.KitChosenEvent;
import me.pjsph.inspectoruhc.kits.Kit;
import me.pjsph.inspectoruhc.listeners.KitsListener;
import me.pjsph.inspectoruhc.misc.chat.IUChat;
import me.pjsph.inspectoruhc.task.FireworksOnWinnersTask;
import me.pjsph.inspectoruhc.teams.Team;
import me.pjsph.inspectoruhc.timer.Timer;
import me.pjsph.inspectoruhc.tools.IUSound;
import me.pjsph.inspectoruhc.tools.Titles;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GameManager {
    private final InspectorUHC plugin;

    private boolean hasStarted = false;
    private boolean rolesActivated = false;
    private boolean kitsActivated = false;
    private boolean invincible = true;
    @Getter private boolean pvpActivated = false;

    @Getter private Set<IUPlayer> players = new HashSet<>();
    private Set<IUPlayer> alivePlayers = new HashSet<>();
    private Set<IUPlayer> spectators = new HashSet<>();
    @Getter private Map<IUPlayer, Location> deathLocations = new HashMap<>();

    private int aliveTeamsCount = 0;

    private Teleporter teleporter = null;

    @Getter private IUChat spectatorChat = new IUChat((sender, message) -> {
        return "§7"+sender.getName()+" §6» §f"+message;
    });
    @Getter private IUChat commonChat = new IUChat(new IUChat.IUChatCallback() {
        @Override
        public String send(IUPlayer sender, String message) {
            if(sender.getPlayer().isOp())
                return "§d"+sender.getName()+" §6» §f"+message;
            return null;
        }
        @Override
        public String receive(IUPlayer sender, String message) {
            return "§e"+sender.getName()+" §6» §f"+message;
        }
    });

    public GameManager(InspectorUHC plugin) {
        this.plugin = plugin;
    }

    public void sendActionBarMessage(String msg) {
        for(IUPlayer iup : getPlayers())
            iup.sendActionBarMessage(msg);
    }

    public void broadcastMessage(String msg) {
        for(IUPlayer iup : getPlayers())
            iup.sendMessage(msg);
    }

    public void join(IUPlayer iup) {
        Player player = iup.getPlayer();

        if(spectators.contains(iup)) {
            iup.joinChat(spectatorChat);
            return;
        }

        if(!hasStarted) {
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

            plugin.getRulesManager().displayRulesTo(player);
        }

        iup.joinChat(commonChat);
    }

    public void leave(IUPlayer iup) {
        // TODO tester si ca marche
        iup.leaveChat();
        if(!alivePlayers.contains(iup))
            IUPlayer.removePlayer(iup.getPlayer().getPlayer());
        iup.remove();
    }

    public void start(CommandSender sender) {
        if(!hasStarted()) {

            /* Initialization of the teams */

            alivePlayers.clear();
            aliveTeamsCount = 0;

            Team.getTeams().forEach(team -> team.clear());

            Bukkit.getOnlinePlayers().stream()
                    .filter(p -> !spectators.contains(p))
                    .forEach(p -> players.add(IUPlayer.thePlayer(p)));

            startRandomizeRoles();

            /* Initialization of the players */

            Team.getTeams().forEach(
                            team -> team.getPlayers().stream()
                                        .filter(iup -> !spectators.contains(iup))
                                        .forEach(iup -> alivePlayers.add(iup))
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

            /* MOTD */
            plugin.getMOTDManager().updateMOTDDuringStart();

            /* Initialization of the spectator mode */
            Bukkit.getOnlinePlayers().forEach(p -> plugin.getSpectatorsManager().setSpectating(p, spectators.contains(IUPlayer.thePlayer(p))));

            /* Start messages */
            broadcastMessage(ChatColor.GREEN + "La partie démarre...");
            new BukkitRunnable() {
                int secondsLeft = 3;
                @Override
                public void run() {
                    if(secondsLeft == 0) {
                        plugin.getServer().broadcastMessage(ChatColor.DARK_AQUA + "C'est parti !");

                        /* Teleportation process */
                        teleporter = new Teleporter();

                        List<Location> spawnPoints = new ArrayList<>(plugin.getSpawnsManager().getSpawnPoints());
                        Collections.shuffle(spawnPoints);

                        Queue<Location> unusedTp = new ArrayDeque<>(spawnPoints);

                        Team.getTeams().stream().filter(team -> !team.isEmpty()).forEach(team -> {
                            team.getPlayers().forEach(player -> {
                                final Location playerSpawn = unusedTp.poll();
                                final Cage cage = new Cage(playerSpawn, true, true);
                                cage.setCustomMaterial(Material.STAINED_GLASS, (byte) 5);
                                cage.setInternalHeight(3);
                                cage.setRadius(5);

                                teleporter.setSpawnForPlayer(player.getUuid(), playerSpawn);
                                if(cage != null) teleporter.setCageForPlayer(player.getUuid(), cage);
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
                                    plugin.getTimerManager().setMinutesRolesLeft(20);
                                    plugin.getTimerManager().setMinutesKitsLeft(25);
                                    plugin.getTimerManager().setMinutesBorderLeft(Math.round(plugin.getBorderManager().getBORDER_SHRINKING_STARTS_AFTER() / 60));
                                    plugin.getTimerManager().setMinutesPvpLeft(30);

                                    setStarted(true);

                                    /* Schedule damages */
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            activateDamages();
                                            broadcastMessage(ChatColor.RED + "Attention ! Vous n'êtes plus invincible !");
                                        }
                                    }.runTaskLater(plugin, 30L * 20L);

                                    /* Finalize start */
                                    finalizeStart();
                                })
                                .startTeleportationProcess();
                        cancel();
                    }
                    else if(secondsLeft == 1) {
                        broadcastMessage(ChatColor.RED + "1");
                        Titles.broadcastTitle(2, 16, 2, "§c1", "");
                    }
                    else if(secondsLeft == 2) {
                        broadcastMessage(ChatColor.YELLOW + "2");
                        Titles.broadcastTitle(2, 16, 2, "§e2", "");
                    }
                    else if(secondsLeft == 3) {
                        broadcastMessage(ChatColor.GREEN + "3");
                        Titles.broadcastTitle(2, 16, 2, "§a3", "");
                    }

                    secondsLeft--;
                }
            }.runTaskTimer(plugin, 0L, 20L);
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
            world.setPVP(false);
        }
    }

    private void finalizeStart() {
        new Timer(InspectorUHC.get()).runTaskTimer(InspectorUHC.get(), 20L, 20L);

        teleporter.cleanup();

        hasStarted = true;

        updateAliveCache();

        plugin.getServer().getOnlinePlayers().stream()
                .filter(player -> alivePlayers.contains(IUPlayer.thePlayer(player)))
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
        player.setExhaustion(0F);
        player.setFoodLevel(20);
        player.setSaturation(20);
    }

    public void finish(Team winnerTeam, boolean forced) {
        if(!forced) {
            if(!hasStarted()) {
                throw new IllegalStateException("Cannot finish the game: the game is not started.");
            }

            if(getAliveTeamsCount() != 1) {
                throw new IllegalStateException("Cannot finish the game: more/less than one team are alive.");
            }
        }

        Set<IUPlayer> listWinners = winnerTeam.getPlayers();

        StringBuilder winners = new StringBuilder();
        int j = 0;

        for(IUPlayer winner : listWinners) {
            if(j != 0) {
                if(j == listWinners.size() - 1) {
                    winners.append(" ").append("et").append(" ");
                } else {
                    winners.append(", ");
                }
            }

            winners.append(Bukkit.getOfflinePlayer(winner.getUuid()).getName());
            j++;
        }

        Bukkit.broadcastMessage("§2Bravo à §e" + winners.toString() + " §2(" + winnerTeam.getColor() + winnerTeam.getName() + "§2) pour leur victoire !");

        /* Broadcast title */
        Titles.broadcastTitle(
                5, 142, 21,
                winnerTeam.getColor() + winnerTeam.getName(),
                "§aCette équipe remporte la partie !"
                );

        new FireworksOnWinnersTask(listWinners).runTaskTimer(plugin, 0L, 15L);
    }

    public void finish(Team winnerTeam) {
        finish(winnerTeam, false);
    }

    public void shiftEpisode(String shifter) {
        plugin.getTimerManager().incEpisode();

        final EpisodeChangedCause cause;
        if(shifter == null || shifter.equals("")) cause = EpisodeChangedCause.FINISHED;
        else cause = EpisodeChangedCause.SHIFTED;

        if(!isRolesActivated() || !isKitsActivated() || !plugin.getBorderManager().isShrinking() || !isPvpActivated()) {
            final int minutesToEndOfEpisode = plugin.getTimerManager().getMinutesLeft() + 1;
            plugin.getTimerManager().setMinutesKitsLeft(Math.max(-1, plugin.getTimerManager().getMinutesKitsLeft() - minutesToEndOfEpisode));
            plugin.getTimerManager().setMinutesRolesLeft(Math.max(-1, plugin.getTimerManager().getMinutesRolesLeft() - minutesToEndOfEpisode));
            plugin.getTimerManager().setMinutesBorderLeft(Math.max(-1, plugin.getTimerManager().getMinutesBorderLeft() - minutesToEndOfEpisode));
            plugin.getTimerManager().setMinutesPvpLeft(Math.max(-1, plugin.getTimerManager().getMinutesPvpLeft() - minutesToEndOfEpisode));

            if (!isRolesActivated() && plugin.getTimerManager().getMinutesRolesLeft() == -1)
                this.activateRoles();
            if (!isKitsActivated() && plugin.getTimerManager().getMinutesKitsLeft() == -1)
                this.activateKits();
            if(!plugin.getBorderManager().isShrinking() && plugin.getTimerManager().getMinutesBorderLeft() == -1)
                plugin.getBorderManager().startBorderReduction();
            if(!isPvpActivated() && plugin.getTimerManager().getMinutesPvpLeft() == -1)
                this.activatePvp();
        }

        plugin.getTimerManager().setMinutesLeft(19);
        plugin.getTimerManager().setSecondsLeft(59);

        plugin.getServer().getPluginManager().callEvent(new EpisodeChangedEvent(plugin.getTimerManager().getEpisode(), cause, shifter));
    }

    public void shiftEpisode() {
        shiftEpisode("");
    }

    private void startRandomizeRoles() {
        Random random = new Random();

        /* Randomize teams */
        ArrayList<IUPlayer> playersToChoose = new ArrayList<>(players);

        ArrayList<Team> teams = new ArrayList<>();
        for(IUPlayer player : playersToChoose) {
            if(teams.size() == 0) teams.addAll(Arrays.asList(Team.values()));
            Team team = teams.remove(0);
            team.addPlayer(player);
            System.out.println("[SPOIL] "+Bukkit.getOfflinePlayer(player.getUuid()).getName()+" rejoint l'équipe des "+team.getName());
        }

        /* Randomize kits */
        List<IUPlayer> inspectors = new ArrayList<>(Team.INSPECTORS.getPlayers());
        List<Kit.KIT_TYPES> listKits = null;
        for(int i = 0; i < inspectors.size(); i++) {
            if(listKits == null || listKits.size() == 0) listKits = new ArrayList<>(Arrays.asList(Kit.KIT_TYPES.values()));
            int r = random.nextInt(listKits.size());
            Kit kit = new Kit(listKits.remove(r));
            kit.addOwner(inspectors.get(i));

            System.out.println("[SPOIL] "+Bukkit.getOfflinePlayer(inspectors.get(i).getUuid()).getName()+" obtient le kit "+kit.getName());
        }
    }

    public void updateAliveCache() {
        Set<Team> aliveTeams = getAliveTeams();

        this.aliveTeamsCount = aliveTeams.size();
    }

    @Getter private Set<String> deadPlayersToResurrect = new HashSet<>();
    public boolean resurrect(String name) {
        Player player = Bukkit.getPlayer(name);
        if(player != null && player.isOnline()) {
            IUPlayer iup = IUPlayer.thePlayer(player);
            if(alivePlayers.contains(iup) || !players.contains(iup))
                return false;
            alivePlayers.add(iup);
            iup.joinChat(commonChat);
            updateAliveCache();
            plugin.getMOTDManager().updateMOTDDuringGame();

            plugin.getSpectatorsManager().setSpectating(player, false);
        } else {
            deadPlayersToResurrect.add(name);
        }

        return true;
    }

    public Set<Team> getAliveTeams() {
        Set<Team> aliveTeams = new HashSet<>();
        for(Team team : Team.values()) {
            for(IUPlayer iup : team.getPlayers()) {
                if(!this.isPlayerDead(iup)) aliveTeams.add(team);
            }
        }

        return aliveTeams;
    }


    public void addDead(IUPlayer player) {
        alivePlayers.remove(player);
        player.joinChat(spectatorChat);
    }

    public boolean isPlayerDead(IUPlayer player) {
        return !alivePlayers.contains(player);
    }

    public void addStartupSpectator(IUPlayer player) {
        if(player.isOnline())
            player.joinChat(spectatorChat);
        spectators.add(player);
    }

    public void removeStartupSpectator(IUPlayer player) {
        spectators.remove(player);
    }

    public HashSet<String> getStartupSpectators() {
        HashSet<String> spectatorNames = new HashSet<>();

        for(IUPlayer iup : spectators) {
            final String playerName = Bukkit.getOfflinePlayer(iup.getUuid()).getName();

            if(playerName != null) {
                spectatorNames.add(playerName);
            } else {
                spectatorNames.add("Unknown player with UUID " + iup.getUuid());
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
        /* Init thieves action */
        for(IUPlayer iup : Team.THIEVES.getPlayers()){
            KitsListener.resetThievesAction(iup);
        }

        /* Messages */
        for(IUPlayer player : Team.INSPECTORS.getOnlinePlayers()) {
            player.sendMessage(ChatColor.DARK_AQUA + "-------------------------------------------");
            player.sendMessage(ChatColor.DARK_AQUA + "Vous êtes un Inspecteur ! Vous devez démasquer et tuer les " + ChatColor.RED + "Criminels");
            player.sendMessage(ChatColor.DARK_AQUA + "Attention cependant : ceux-ci peuvent connaître votre identité.");
            player.sendMessage(ChatColor.DARK_AQUA + "-------------------------------------------");
        }

        for(IUPlayer player : Team.THIEVES.getOnlinePlayers()) {
            player.sendMessage(ChatColor.RED + "-------------------------------------------");
            player.sendMessage(ChatColor.RED + "Vous êtes un Criminel ! Vous devez tuer les " + ChatColor.DARK_AQUA + "Inspecteurs");
            player.sendMessage(ChatColor.RED + "Vous pouvez connaître l'identité d'un joueur avec /spy <joueur>");
            player.sendMessage(ChatColor.RED + "Vous pouvez activer votre aura de Serial Killer pour perdre votre effet Weakness et le remplacer par Force I.");
            player.sendMessage(ChatColor.RED + "Attention cependant : les " + ChatColor.DARK_AQUA + "Inspecteurs " + ChatColor.RED + "pourront alors vous tracer.");
            player.sendMessage(ChatColor.RED + "/f (comme furie) pour activer/désactiver l'aura.");
            player.sendMessage(ChatColor.RED + "-------------------------------------------");
        }

        broadcastMessage("§bLes équipes ont été annoncées.");
        this.rolesActivated = true;
    }

    public boolean isKitsActivated() {
        return kitsActivated;
    }

    public void activateKits() {
        Team.INSPECTORS.getPlayers().forEach(iup ->
                plugin.getServer().getPluginManager().callEvent(new KitChosenEvent(iup)));

        /* Messages */
        for(IUPlayer iup : Team.INSPECTORS.getPlayers()) {
            if(iup.getPlayer() != null && plugin.getGameManager().getOnlineAlivePlayers().contains(iup)) {
                iup.sendMessage(ChatColor.DARK_AQUA + "-------------------------------------------");
                iup.sendMessage(ChatColor.DARK_AQUA + "Voici votre kit : " + Kit.getKit(iup).getName() + ".");
                iup.sendMessage(ChatColor.DARK_AQUA + "Celui-ci vous donne un objet, un effet ou une capacité spéciale :");
                iup.sendMessage(ChatColor.DARK_AQUA + Kit.getKit(iup).getDescription());
                iup.sendMessage(ChatColor.DARK_AQUA + "-------------------------------------------");
            }
        }
        this.kitsActivated = true;
    }

    public void activatePvp() {
        /* Messages */
        broadcastMessage("§c/!\\ Le PVP est maitenant activé !");
        (new IUSound(Sound.BAT_DEATH)).broadcast();
        for(World world : Bukkit.getWorlds())
            world.setPVP(true);
        this.pvpActivated = true;
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    public void setStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    public int getAliveTeamsCount() {
        return aliveTeamsCount;
    }

    public void addDeathLocation(IUPlayer player, Location location) {
        deathLocations.put(player, location);
    }

    public Set<IUPlayer> getAlivePlayers() {
        return alivePlayers;
    }

    public HashSet<IUPlayer> getOnlineAlivePlayers() {
        return alivePlayers.stream()
                .filter(iup -> iup.isOnline())
                .collect(Collectors.toCollection(HashSet::new));
    }

    public boolean isInvincible() {
        return invincible;
    }
}
