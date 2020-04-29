package me.pjsph.inspectoruhc.listeners;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.events.*;
import me.pjsph.inspectoruhc.game.IUPlayer;
import me.pjsph.inspectoruhc.kits.Kit;
import me.pjsph.inspectoruhc.scoreboard.ScoreboardSign;
import me.pjsph.inspectoruhc.teams.Team;
import me.pjsph.inspectoruhc.tools.IUSound;
import me.pjsph.inspectoruhc.tools.Titles;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Random;
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
        IUPlayer victim = IUPlayer.thePlayer(ev.getEntity());

        if(!plugin.getGameManager().hasStarted() || plugin.getGameManager().isPlayerDead(victim))
            return;

        /* If the player has the roughneck kit and it's its first death, his death is treated by the KitsListener class */
        if(Kit.getKit(victim) != null &&
                Kit.getKit(victim).getKitType() == Kit.KIT_TYPES.ROUGHNECK &&
                plugin.getGameManager().isKitsActivated() &&
                victim.getCache().getBoolean("kit_roughneck"))
            return;

        /* Call the PlayerDeathEvent */
        plugin.getServer().getPluginManager().callEvent(new me.pjsph.inspectoruhc.events.PlayerDeathEvent(victim, ev));

        /* Death sound */
        new IUSound(Sound.WITHER_SPAWN).broadcast();

        plugin.getGameManager().addDead(victim);
        plugin.getGameManager().addDeathLocation(victim, victim.getPlayer().getLocation());

        final ItemStack head = new ItemStack(Material.SKULL_ITEM);
        head.setDurability((short)3);
        final SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setOwner(victim.getPlayer().getName());
        skullMeta.setDisplayName("Head");
        head.setItemMeta(skullMeta);
        victim.getPlayer().getWorld().dropItem(victim.getPlayer().getLocation(), head);

        enableSpectatorModeOnRespawn.add(ev.getEntity().getUniqueId());

        /* Give xp to the killer */
        Player killer = ev.getEntity().getKiller();
        if(killer != null)
            killer.giveExpLevels(5);

        /* Check if the team is empty */
        final Team team = Team.getTeamForPlayer(victim);
        if(team != null) {
            boolean isAliveTeam = false;
            for(IUPlayer iup : team.getPlayers()) {
                if(!plugin.getGameManager().isPlayerDead(iup)) {
                    isAliveTeam = true;
                    break;
                }
            }

            if(!isAliveTeam) {
                /* Call the TeamDeathEvent */
                plugin.getServer().getPluginManager().callEvent(new TeamDeathEvent(team));

                /* Display the team death message after the player's death one */
                if(plugin.getGameManager().isRolesActivated()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            plugin.getGameManager().broadcastMessage("§8§l>> §6L'équipe des " + team.getColor() + team.getName() + " §6est éliminée !");
                        }
                    }.runTaskLater(plugin, 1L);
                }
            }

            /* Display a death message in the console */
            plugin.getServer().getConsoleSender().sendMessage("§6-- Mort de " + ev.getEntity().getDisplayName() + " §6(" + ev.getDeathMessage() + "§6) --");

            /* Display the player's death message */
            String deathMsg;
            if(killer != null)
                deathMsg = "§8§l>> §6"+victim.getPlayer().getName()+" est mort.";
            else
                deathMsg = "§8§l>> §6" + ev.getDeathMessage();
            ev.setDeathMessage(deathMsg);

            plugin.getGameManager().updateAliveCache();
            plugin.getMOTDManager().updateMOTDDuringGame();

            if(plugin.getGameManager().hasStarted() && plugin.getGameManager().getAliveTeamsCount() <= 1) {
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent ev) {
        IUPlayer iup = IUPlayer.thePlayer(ev.getPlayer());

        /* Initialization of the scoreboard */
        ScoreboardSign scoreboardSign = new ScoreboardSign(ev.getPlayer(), "§3InspectorUHC");
        scoreboardSign.create();
        scoreboardSign.update();
        ScoreboardSign.getScoreboards().put(ev.getPlayer(), scoreboardSign);

        /* Initialization of the spectator mode */
        if(plugin.getGameManager().hasStarted() && !plugin.getGameManager().getAlivePlayers().contains(iup)) {
            plugin.getSpectatorsManager().setSpectating(ev.getPlayer(), true);
            plugin.getGameManager().addStartupSpectator(IUPlayer.thePlayer(ev.getPlayer()));
        }

        /* Initialization of the player */
        InspectorUHC.get().getGameManager().join(iup);

        /* If he needs to be resurrected */
        if(plugin.getGameManager().getDeadPlayersToResurrect().contains(ev.getPlayer().getName())) {
            plugin.getGameManager().resurrect(ev.getPlayer().getName());
            plugin.getGameManager().getDeadPlayersToResurrect().remove(ev.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent ev) {
        ScoreboardSign scoreboardSign = ScoreboardSign.getScoreboards().get(ev.getPlayer());
        if(scoreboardSign != null) {
            scoreboardSign.destroy();
            ScoreboardSign.getScoreboards().remove(ev.getPlayer());
        }

        /* Wait other events and then unregister the player */
        new BukkitRunnable() {
            @Override
            public void run() {
                InspectorUHC.get().getGameManager().leave(IUPlayer.thePlayer(ev.getPlayer()));
            }
        }.runTaskLater(plugin, 2L);
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
        final Block block = ev.getBlock();
        final Location loc = ev.getBlock().getLocation();
        final Random random = new Random();
        final double r = random.nextDouble();
        if(r <= 2 * 0.01 && block.getType() == Material.LEAVES) {
            block.setType(Material.AIR);
            block.getWorld().dropItemNaturally(loc, new ItemStack(Material.APPLE, 1));
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

        new BukkitRunnable() {
            int seconds = 3;
            @Override
            public void run() {
                if(seconds == 0) {
                    (new IUSound(Sound.FIREWORK_BLAST)).broadcast();
                    (new IUSound(Sound.FIREWORK_LARGE_BLAST)).broadcast();
                    this.cancel();
                } else if(seconds == 1) {
                    Titles.broadcastTitle(
                            0, 32, 8,
                            "§6Episode §7" + (ev.getNewEpisode() - 1) + " §7> §e" + ev.getNewEpisode(),
                            ""
                    );

                    (new IUSound(Sound.FIRE_IGNITE)).broadcast();
                } else if(seconds == 2) {
                    Titles.broadcastTitle(
                            0, 32, 0,
                            "§6Episode §7" + (ev.getNewEpisode() - 1) + " §e> §7" + ev.getNewEpisode(),
                            ""
                    );

                    (new IUSound(Sound.FIRE_IGNITE)).broadcast();
                } else if(seconds == 3) {
                    Titles.broadcastTitle(
                            5, 32, 0,
                            "§6Episode §e" + (ev.getNewEpisode() - 1) + " §7> §7" + ev.getNewEpisode(),
                            ""
                    );

                    (new IUSound(Sound.FIRE_IGNITE)).broadcast();
                }

                seconds--;
            }
        }.runTaskTimer(plugin, 0L, 20L);

        if(plugin.getTimerManager().getEpisode() == 2) {
            String txt = "§cLa bordure rétrécira dans "+plugin.getTimerManager().getMinutesBorderLeft()+" minutes.";
            plugin.getGameManager().broadcastMessage(txt);
        }
    }

    @EventHandler
    public void appleRate(LeavesDecayEvent ev) {
        final Block block = ev.getBlock();
        final Location loc = block.getLocation();
        final Random random = new Random();
        final double r = random.nextDouble();
        if(r <= 2 * 0.01 && block.getType() == Material.LEAVES) {
            block.setType(Material.AIR);
            block.getWorld().dropItemNaturally(loc, new ItemStack(Material.APPLE, 1));
        }
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent ev) {
        if(ev.getItem().hasItemMeta() && ev.getItem().getItemMeta().getDisplayName().contains("Golden Head"))
            ev.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 1));
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

        /* Rules */
        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getRulesManager().broadcastRules(), 15 * 20L);

        /* MOTD */
        plugin.getMOTDManager().updateMOTDDuringGame();
    }

    @EventHandler
    public void onGameEnds(GameEndsEvent ev) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getGameManager().finish(ev.getWinnerTeam(), ev.isForced()), 3 * 20L);
        plugin.getMOTDManager().updateMOTDAfterGame(ev.getWinnerTeam());
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onPing(ServerListPingEvent ev) {
        ev.setMotd(plugin.getMOTDManager().getCurrentMOTD());
    }

}
