package me.pjsph.inspectoruhc.listeners;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.events.GameEndsEvent;
import me.pjsph.inspectoruhc.events.GameStartsEvent;
import me.pjsph.inspectoruhc.events.TeamDeathEvent;
import me.pjsph.inspectoruhc.scoreboard.ScoreboardSign;
import me.pjsph.inspectoruhc.teams.Team;
import me.pjsph.inspectoruhc.tools.IUSound;
import me.pjsph.inspectoruhc.tools.Titles;
import org.bukkit.Bukkit;
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
import java.util.logging.Level;

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

    /* TODO Episode change event */

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
