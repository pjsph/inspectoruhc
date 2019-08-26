package me.pjsph.inspectoruhc.listeners;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.events.*;
import me.pjsph.inspectoruhc.kits.Kit;
import me.pjsph.inspectoruhc.scoreboard.ScoreboardSign;
import me.pjsph.inspectoruhc.teams.Team;
import me.pjsph.inspectoruhc.tools.IUSound;
import me.pjsph.inspectoruhc.tools.Titles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GameListener implements Listener {
    private InspectorUHC plugin;

    private Set<UUID> enableSpectatorModeOnRespawn = new HashSet<>();

    public GameListener(InspectorUHC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent ev) {
        if(!plugin.getGameManager().hasStarted() || plugin.getGameManager().isPlayerDead(ev.getEntity())) {
            return;
        }

        /* If the player has the roughneck kit and it's its first death, his death is treated by the KitsListener class */
        if(Kit.getKit(ev.getEntity().getUniqueId()) != null &&
                Kit.getKit(ev.getEntity().getUniqueId()).getKitType() == Kit.KIT_TYPES.ROUGHNECK &&
                plugin.getGameManager().isKitsActivated() &&
                KitsListener.canAction(ev.getEntity().getUniqueId())) {
            return;
        }

        /* Call the PlayerDeathEvent */
        plugin.getServer().getPluginManager().callEvent(new me.pjsph.inspectoruhc.events.PlayerDeathEvent(ev.getEntity(), ev));

        /* Death sound */
        new IUSound(Sound.WITHER_SPAWN).broadcast();

        plugin.getGameManager().addDead(ev.getEntity());

        enableSpectatorModeOnRespawn.add(ev.getEntity().getUniqueId());

        /* Give xp to the killer */
        Player killer = ev.getEntity().getKiller();
        if(killer != null) {
            boolean inSameTeam = Team.inSameTeam(ev.getEntity(), killer);

            if(!inSameTeam) {
                killer.giveExpLevels(5);
            }
        }

        /* Check if the team is empty */
        final Team team = Team.getTeamForPlayer(ev.getEntity());
        if(team != null) {
            boolean isAliveTeam = false;

            for(UUID id : team.getPlayersUUID()) {
                if(!plugin.getGameManager().isPlayerDead(id)) {
                    isAliveTeam = true;
                    break;
                }
            }

            if(!isAliveTeam) {
                /* Call the TeamDeathEvent */
                plugin.getServer().getPluginManager().callEvent(new TeamDeathEvent(team));

                /* Display the team death message after the player's death one */
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Bukkit.broadcastMessage("§8§l>> §6L'équipe des " + team.getColor() + team.getName() + " §6est éliminée !");
                }, 1L);
            }

            /* Display a death message in the console */
            plugin.getServer().getConsoleSender().sendMessage("§6-- Mort de " + ev.getEntity().getDisplayName() + " §6(" + ev.getDeathMessage() + "§6) --");

            /* Display the player's death message */
            String deathMsg = "§8§l>> §6" + ev.getDeathMessage();
            ev.setDeathMessage(deathMsg);

            if(plugin.getGameManager().hasStarted() && plugin.getGameManager().getAliveTeamsCount() == 1) {
                /* Call the GameEndsEvent */
                plugin.getServer().getPluginManager().callEvent(new GameEndsEvent(plugin.getGameManager().getAliveTeams().size() == 0 ? Team.INSPECTORS : plugin.getGameManager().getAliveTeams().iterator().next()));
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent ev) {
        if(enableSpectatorModeOnRespawn.remove(ev.getPlayer().getUniqueId())) {
            Bukkit.getScheduler().runTask(plugin, () -> plugin.getSpectatorsManager().setSpectating(ev.getPlayer(), true));
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent ev) {
        if(ev.getEntity() instanceof Player) {
            if(!plugin.getGameManager().hasStarted() || (plugin.getGameManager().hasStarted() && plugin.getGameManager().isInvincible())) {
                ev.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFoodUpdate(FoodLevelChangeEvent ev) {
        if(!plugin.getGameManager().hasStarted()) {
            if(ev.getEntity() instanceof Player) {
                ((Player) ev.getEntity()).setFoodLevel(20);
                ((Player) ev.getEntity()).setSaturation(20f);
            }

            ev.setCancelled(true);
        }
    }

    /* TODO update MOTD manager */

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent ev) {
        if(!plugin.getGameManager().hasStarted()) {
            plugin.getGameManager().initPlayer(ev.getPlayer());

            plugin.getRulesManager().displayRulesTo(ev.getPlayer());
        }

        /* Initialization of the scoreboard */
        ScoreboardSign scoreboardSign = new ScoreboardSign(ev.getPlayer(), "§3InspectorUHC");
        scoreboardSign.create();
        scoreboardSign.update();
        ScoreboardSign.getScoreboards().put(ev.getPlayer(), scoreboardSign);

        /* Initialization of the spectator mode */
        if(plugin.getGameManager().hasStarted() && !plugin.getGameManager().getAlivePlayers().contains(ev.getPlayer())) {
            plugin.getSpectatorsManager().setSpectating(ev.getPlayer(), true);
            plugin.getGameManager().addStartupSpectator(ev.getPlayer());
        }

        /* Update gamemode */
        if(!plugin.getGameManager().hasStarted()) {
            ev.getPlayer().getInventory().clear();

            ev.getPlayer().setGameMode(ev.getPlayer().isOp() ? GameMode.CREATIVE : GameMode.ADVENTURE);
            ev.getPlayer().teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation().add(0, 1, 0));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent ev) {
        ScoreboardSign scoreboardSign = ScoreboardSign.getScoreboards().get(ev.getPlayer());
        if(scoreboardSign != null) {
            scoreboardSign.destroy();
            ScoreboardSign.getScoreboards().remove(ev.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent ev) {
        if(!plugin.getGameManager().hasStarted()) {
            ev.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent ev) {
        if(!plugin.getGameManager().hasStarted() && !ev.getPlayer().isOp()) {
            ev.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent ev) {
        if(!plugin.getGameManager().hasStarted() && !ev.getPlayer().isOp()) {
            ev.setCancelled(true);
        }
    }

    @EventHandler
    public void onEpisodeChange(EpisodeChangedEvent ev) {
        if(ev.getCause() == EpisodeChangedCause.SHIFTED) {
            plugin.getServer().broadcastMessage("§6Fin de l'épisode " + String.valueOf(ev.getNewEpisode() - 1) + " (forcée par " + ev.getShifter() + ").");
        } else {
            plugin.getServer().broadcastMessage("§6Fin de l'épisode " + String.valueOf(ev.getNewEpisode() - 1) + ".");
        }

        Titles.broadcastTitle(
                5, 32, 0,
                "§6Episode §e" + (ev.getNewEpisode() - 1) + " §7> §7" + ev.getNewEpisode(),
                ""
        );

        (new IUSound(Sound.FIRE_IGNITE)).broadcast();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Titles.broadcastTitle(
                    0, 32, 0,
                    "§6Episode §7" + (ev.getNewEpisode() - 1) + " §e> §7" + ev.getNewEpisode(),
                    ""
            );

            (new IUSound(Sound.FIRE_IGNITE)).broadcast();
        }, 20L);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Titles.broadcastTitle(
                    0, 32, 8,
                    "§6Episode §7" + (ev.getNewEpisode() - 1) + " §7> §e" + ev.getNewEpisode(),
                    ""
            );

            (new IUSound(Sound.FIRE_IGNITE)).broadcast();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                (new IUSound(Sound.FIREWORK_BLAST)).broadcast();
                (new IUSound(Sound.FIREWORK_LARGE_BLAST)).broadcast();
            }, 7L);

        }, 2 * 20L);

        if(plugin.getTimerManager().getEpisode() == 2) {
            String txt = ChatColor.RED + "La bordure rétrécira à partir de 10min à l'épisode 2, et pendant 1h30.";
            plugin.getServer().broadcastMessage(txt);
        }

        if(!plugin.getGameManager().isRolesActivated()) {
            plugin.getGameManager().activateRoles();

            for(Player player : Team.INSPECTORS.getOnlinePlayers()) {
                player.sendMessage(ChatColor.DARK_AQUA + "-------------------------------------------");
                player.sendMessage(ChatColor.DARK_AQUA + "Vous êtes un Inspecteur ! Vous devez démasquer et tuer les " + ChatColor.RED + "Criminels");
                player.sendMessage(ChatColor.DARK_AQUA + "Attention cependant : ceux-ci connaissent votre identité.");
                player.sendMessage(ChatColor.DARK_AQUA + "-------------------------------------------");
            }

            for(Player player : Team.THIEVES.getOnlinePlayers()) {
                player.sendMessage(ChatColor.RED + "-------------------------------------------");
                player.sendMessage(ChatColor.RED + "Vous êtes un Criminel ! Vous devez tuer les " + ChatColor.DARK_AQUA + "Inspecteurs");
                player.sendMessage(ChatColor.RED + "Vous reconnaitrez un " + ChatColor.DARK_AQUA + "Inspecteur " + ChatColor.RED + "en le voyant.");
                player.sendMessage(ChatColor.RED + "Vous pouvez activer votre aura de Serial Killer pour perdre votre effet Weakness et le remplacer par Force I.");
                player.sendMessage(ChatColor.RED + "Attention cependant : les " + ChatColor.DARK_AQUA + "Inspecteurs " + ChatColor.RED + "pourront alors vous tracer.");
                player.sendMessage(ChatColor.RED + "/f (comme furie) pour activer/désactiver l'aura.");
                player.sendMessage(ChatColor.RED + "-------------------------------------------");
            }

            for(String name : plugin.getGameManager().getPlayers()) {
                Player player = Bukkit.getPlayer(name);

                if(player != null && player.isOnline())
                    player.sendMessage(ChatColor.AQUA + "Les équipes ont été annoncées.");
            }
        }

        if(!plugin.getGameManager().isKitsActivated()) {
            plugin.getGameManager().activateKits();

            for(UUID id : Team.INSPECTORS.getPlayersUUID()) {
                Player player = Bukkit.getPlayer(id);

                if(player != null && plugin.getGameManager().getOnlineAlivePlayers().contains(player)) {
                    player.sendMessage(ChatColor.DARK_AQUA + "-------------------------------------------");
                    player.sendMessage(ChatColor.DARK_AQUA + "Voici votre kit : " + Kit.getKit(id).getName() + ".");
                    player.sendMessage(ChatColor.DARK_AQUA + "Celui-ci vous donne un objet, un effet ou une capacité spéciale :");
                    player.sendMessage(ChatColor.DARK_AQUA + Kit.getKit(id).getDescription());
                    player.sendMessage(ChatColor.DARK_AQUA + "-------------------------------------------");
                }
            }
        }
    }

    @EventHandler
    public void onGameStarts(GameStartsEvent ev) {
        /* Broadcast start sound */
        new IUSound(Sound.LEVEL_UP).broadcast();

        Titles.broadcastTitle(
                5, 40, 8,
                "§2GO!",
                "§aBonne chance"
        );

        /* Border */
        plugin.getBorderManager().scheduleBorderReduction();

        /* Rules */
        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getRulesManager().broadcastRules(), 15 * 20L);
    }

    @EventHandler
    public void onGameEnds(GameEndsEvent ev) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getGameManager().finish(), 10 * 20L);
    }

}
