package me.pjsph.inspectoruhc.game;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.kits.Kit;
import me.pjsph.inspectoruhc.teams.Team;
import me.pjsph.inspectoruhc.timer.Timer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

public class GameManager {
    private InspectorUHC plugin;

    private ArrayList<Player> players = new ArrayList<>();

    private boolean hasStarted = false;
    private boolean rolesActivated = false;
    private boolean kitsActivated = false;
    private boolean invincible = true;

    public GameManager(InspectorUHC plugin) {
        this.plugin = plugin;
    }

    public void start(Player player) {
        if(!hasStarted()) {
            startRandomizeRoles(player);

            plugin.getTimerManager().setMinutesLeft(20);
            plugin.getTimerManager().incEpisode();
            setStarted(true);
            plugin.getTimerManager().setMinutesRolesLeft(5);
            plugin.getTimerManager().setMinutesKitsLeft(10);

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

                    for(Entry<String, Team> entry : plugin.getTeamManager().getTeams().entrySet()) {
                        Team team = entry.getValue();

                        if(team.countPlayer() != 0) {
                            team.getScoreboardTeam().setAllowFriendlyFire(true);
                        }
                    }

                    for(Player player : plugin.getGameManager().getAllPlayers()) {
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

                    plugin.getBorderManager().scheduleBorderReduction();
                }
            }, 60L);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                activateDamages();
                plugin.getServer().broadcastMessage(ChatColor.RED + "Attention ! Vous n'êtes plus invincible !");
            }, 660L);
        }
    }

    public void finish(int cause) {
        if(cause == 0)
            plugin.getServer().broadcastMessage(ChatColor.GREEN + "Partie terminée ! Bravo à l'équipe des " + plugin.getTeamManager().getPlayersTeam().get(players.get(0)).getName());
    }

    private void startRandomizeRoles(Player p) {
        Random random = new Random();

        // Select teams
        int rand = 0;

        ArrayList<Player> players = new ArrayList<>(plugin.getGameManager().getAllPlayers());
        plugin.getLogger().log(Level.INFO, "Joueurs : " + players.size());
        ArrayList<Player> chosenPlayers = new ArrayList<>();
        ArrayList<String> teamsName = new ArrayList<>();
        Iterator<String> it = plugin.getTeamManager().getTeams().keySet().iterator();

        while(it.hasNext()) {
            teamsName.add(it.next());
        }

        for(int i = 0; i < plugin.getGameManager().getAllPlayers().size(); i++) {
            boolean check = false;

            // Choose a team to put a player in
            while(check == false) {
                int randTeam = random.nextInt(plugin.getTeamManager().getTeams().size());

                Team team = plugin.getTeamManager().getTeams().get(teamsName.get(randTeam));
                plugin.getLogger().log(Level.INFO, "Team : " + team.getName() + ", Players : " + team.getPlayers().size());
                if(!(team.getPlayers().size() >= Math.floorDiv(getAllPlayers().size(), 2))) {
                    while(check == false) {
                        rand = random.nextInt(players.size());

                        if(!chosenPlayers.contains(players.get(rand))) {
                            team.addPlayer(players.get(rand));

                            plugin.getLogger().log(Level.INFO, ChatColor.AQUA + "[SPOIL] " + players.get(rand).getName() + " rejoint l'equipe " + team.getName());

                            chosenPlayers.add(players.get(rand));
                            players.remove(rand);

                            check = true;
                        }
                    }
                } else if(plugin.getTeamManager().getTeams().get(teamsName.get(0)).getPlayers().size() == plugin.getTeamManager().getTeams().get(teamsName.get(1)).getPlayers().size()) {
                    // Same amount of players in the two teams but a player is still teamless, we had him in the Inspectors team
                    if(players.get(0) != null) {
                        plugin.getTeamManager().getTeams().get("Inspecteurs").addPlayer(players.get(0));

                        plugin.getLogger().log(Level.INFO, ChatColor.AQUA + "[SPOIL] " + players.get(0).getName() + " rejoint l'equipe Inspecteurs");

                        chosenPlayers.add(players.get(0));
                        players.remove(0);

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

        for(Player player : plugin.getTeamManager().getTeams().get("Inspecteurs").getPlayers()) {
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
        Player oldPlayer = getPlayerInTeamsByName(newPlayer.getName());

        if(oldPlayer != null) {
            Inventory inv = oldPlayer.getInventory();
            newPlayer.getInventory().setContents(inv.getContents());

            Team team = plugin.getTeamManager().getTeamOfPlayer(oldPlayer);

            if(team != null) {
                if(team.getPlayers().contains(oldPlayer)) {
                    team.removePlayer(oldPlayer);
                }

                team.addPlayer(newPlayer);
            }

            if(plugin.getTeamManager().getPlayersTeam().containsKey(oldPlayer)) {
                plugin.getTeamManager().getPlayersTeam().remove(oldPlayer);
            }

            plugin.getTeamManager().getPlayersTeam().put(newPlayer, team);

            getAllPlayers().remove(oldPlayer);
        }

        players.add(newPlayer);
    }

    public Player getPlayerInTeamsByName(String name) {
        Player player = null;

        Iterator<Player> it = getAllPlayers().iterator();

        while(it.hasNext() && player == null) {
            Player tmpPlayer = it.next();

            if(tmpPlayer.getName().equalsIgnoreCase(name)) {
                player = tmpPlayer;
            }
        }

        return player;
    }

    public int countAllPlayers() {
        int count = 0;

        Iterator<Team> it = plugin.getTeamManager().getTeams().values().iterator();
        while(it.hasNext()) {
            count += it.next().countPlayer();
        }

        return count;
    }

    public void activateDamages() {
        this.invincible = false;
    }

    public ArrayList<Player> getAllPlayers() {
        return players;
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
}
