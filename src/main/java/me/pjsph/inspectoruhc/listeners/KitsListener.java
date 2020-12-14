package me.pjsph.inspectoruhc.listeners;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.events.*;
import me.pjsph.inspectoruhc.game.Cage;
import me.pjsph.inspectoruhc.game.IUPlayer;
import me.pjsph.inspectoruhc.game.Teleporter;
import me.pjsph.inspectoruhc.kits.Kit;
import me.pjsph.inspectoruhc.teams.Team;
import me.pjsph.inspectoruhc.tools.IUSound;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class KitsListener implements Listener {

    private InspectorUHC plugin;

    public KitsListener(InspectorUHC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onKitChosen(KitChosenEvent ev) {
        IUPlayer iup = ev.getPlayer();
        Kit kit = Kit.getKit(iup);

        /* AGILITY: Set speed effect */
        if(kit.getKitType() == Kit.KIT_TYPES.AGILITY) {
            if(iup.isOnline())
                iup.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
        /* SPY_GLASSES: Add to canSpy */
        } else if(kit.getKitType() == Kit.KIT_TYPES.SPY_GLASSES) {
            ArrayList<IUPlayer> chosen = new ArrayList<>(InspectorUHC.get().getGameManager().getAlivePlayers());
            if(chosen.contains(iup)) chosen.remove(iup);
            Random rand = new Random();
            while(chosen.size() > 2) // Size of chosen must be < 2
                chosen.remove(rand.nextInt(chosen.size()));
            iup.getCache().set("can_see", chosen);
            iup.sendTitle("§6(⌐■_■)", "", 20*5);
            iup.sendMessage("§6(⌐■_■) Vous connaissez l'identité de :");
            for(IUPlayer chosenOne : (ArrayList<IUPlayer>)iup.getCache().get("can_see")) {
                iup.seeRoleOf(chosenOne);
                Team chosenTeam = Team.getTeamForPlayer(chosenOne);
                iup.sendMessage("§7 - §l"+chosenOne.getName()+" est "+chosenTeam.getColor()+chosenTeam.getName().substring(0, chosenTeam.getName().length() - 1));
            }
            for(IUPlayer chosenOne : (ArrayList<IUPlayer>)iup.getCache().get("can_see"))
                iup.seeRoleOf(chosenOne);
        /* ROUGHNECK: Add to canRespawn */
        } else if(kit.getKitType() == Kit.KIT_TYPES.ROUGHNECK) {
            iup.getCache().set("kit_roughneck", true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent ev) {
        IUPlayer iup = IUPlayer.thePlayer(ev.getPlayer());

        if(plugin.getGameManager().isKitsActivated()) {
            Kit kit = Kit.getKit(iup);
            if(kit != null) {
                /* AGILITY: Set speed effect */
                if(kit.getKitType() == Kit.KIT_TYPES.AGILITY) {
                    if(iup.getPlayer().hasPotionEffect(PotionEffectType.SPEED))
                        iup.getPlayer().removePotionEffect(PotionEffectType.SPEED);
                    iup.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
                }
            }
        }

        if(iup.getPlayer().hasPotionEffect(PotionEffectType.WEAKNESS))
            iup.getPlayer().removePotionEffect(PotionEffectType.WEAKNESS);

        if(iup.getPlayer().hasPotionEffect(PotionEffectType.ABSORPTION))
            iup.getPlayer().removePotionEffect(PotionEffectType.ABSORPTION);

        if(plugin.getGameManager().isRolesActivated()) {
            if(Team.getTeamForPlayer(iup) == Team.THIEVES)
                iup.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0, false, false));
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent ev) {
        IUPlayer iup = IUPlayer.thePlayer(ev.getPlayer());

        plugin.getServer().getPluginManager().callEvent(new DesactivateAuraEvent(iup));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent ev) {
        if(!plugin.getGameManager().isRolesActivated()) return;

        IUPlayer iup = ev.getPlayer();
        if(Team.getTeamForPlayer(iup) == Team.THIEVES)
            plugin.getServer().getPluginManager().callEvent(new DesactivateAuraEvent(iup));

//        Player killer = iup.getPlayer().getKiller();
//        if(killer != null)
//            if(Team.getTeamForPlayer(IUPlayer.thePlayer(killer)) == Team.THIEVES)
//                plugin.getServer().getPluginManager().callEvent(new DesactivateAuraEvent(IUPlayer.thePlayer(killer)));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent ev) {
        if(!(ev.getEntity() instanceof Player)) return;

        IUPlayer victim = IUPlayer.thePlayer((Player) ev.getEntity());

        if((victim.getPlayer().getHealth() <= ev.getFinalDamage()) && !plugin.getGameManager().isInvincible() && plugin.getGameManager().isKitsActivated()) {
            Kit kit = Kit.getKit(victim);
            if(kit != null) {
                /* ROUGHNECK: If it's its first death, the player doesn't die */
                if(kit.getKitType() != Kit.KIT_TYPES.ROUGHNECK || !victim.getCache().getBoolean("kit_roughneck")) {
                    return;
                } else {
                    /* Teleport away the player */
                    plugin.getSpawnsManager().reset();
                    try {
                        plugin.getSpawnsManager().generateSpawnPoints(
                                plugin.getServer().getWorlds().get(0),
                                1,
                                plugin.getBorderManager().getCurrentBorderDiameter() - 25,
                                0,
                                plugin.getServer().getWorlds().get(0).getSpawnLocation().getX(),
                                plugin.getServer().getWorlds().get(0).getSpawnLocation().getZ()
                        );

                        Location spawnPoint = plugin.getSpawnsManager().getSpawnPoints().stream().findFirst().get();
                        Teleporter teleporter = new Teleporter();

                        final Cage cage = new Cage(spawnPoint, true, true);
                        teleporter.setSpawnForPlayer(victim.getUuid(), spawnPoint);
                        if(cage != null) teleporter.setCageForPlayer(victim.getUuid(), cage);

                        teleporter.teleportPlayer(victim.getUuid());

                        /* Heal the player as well */
                        victim.getPlayer().setHealth(20d);
                        victim.getPlayer().setFoodLevel(20);
                        victim.getPlayer().setSaturation(20f);

                        /* Cancel death */
                        ev.setCancelled(true);

                        /* Give Resistance effect to the player */
                        victim.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30 * 20, 4, false, false));

                        victim.getCache().set("kit_roughneck", false);
                    } catch(Exception e) {
                        victim.sendMessage("§cUne erreur s'est produite : impossible de vous ressusciter.");
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEpisodeChange(EpisodeChangedEvent ev) {
        if(!this.plugin.getGameManager().isRolesActivated()) return;

        /* Broadcast undersense message */
        if(plugin.getGameManager().isKitsActivated()) {
            for (IUPlayer iup : Kit.getOwners(Kit.KIT_TYPES.UNDERSENSE)) {
                if (!iup.isOnline()) continue;

                List<String> near = new ArrayList<>();
                for (IUPlayer thief : Team.THIEVES.getOnlinePlayers())
                    if(!plugin.getGameManager().isPlayerDead(thief))
                        if (Math.round(thief.getPlayer().getLocation().distance(iup.getPlayer().getLocation())) <= 100.0d)
                            near.add(thief.getPlayer().getName());
                iup.sendMessage("");
                iup.sendMessage("§3" + near.size() + " §cCriminel(s) §3est/sont à proximité (< 100 blocs).");
                iup.sendMessage("");
            }
        }
    }

    @EventHandler
    public void onActivateAura(ActivateAuraEvent ev) {
        IUPlayer iup = ev.getPlayer();

        if(iup.isOnline()) {
            iup.getCache().set("thief_aura", true);

            if(iup.getPlayer().hasPotionEffect(PotionEffectType.WEAKNESS))
                iup.getPlayer().removePotionEffect(PotionEffectType.WEAKNESS);

            iup.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 0, false, false));

            iup.getPlayer().sendMessage("§aVous activez votre Aura de serial killer. Vous gagnez Absorption I mais vos coordonnées sont divulguées.");
            plugin.getGameManager().broadcastMessage("§cUn Criminel a activé son Aura de serial killer.");
            (new IUSound(Sound.WITHER_IDLE)).broadcast();

            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    if(iup.isOnline())
                        Bukkit.broadcastMessage("§4[§cA§6U§2R§aA§3] §cUn Criminel se trouve aux coordonnées suivantes : §rX: " + iup.getPlayer().getLocation().getBlockX() + " Z: " + iup.getPlayer().getLocation().getBlockZ());
                }
            }.runTaskTimer(plugin, 0L, 10 * 20L);

            iup.getCache().set("thief_aura_task", task);
        }
    }

    @EventHandler
    public void onDesactivateAura(DesactivateAuraEvent ev) {
        IUPlayer player = ev.getPlayer();

        player.getCache().set("thief_aura", false);

        if(player.getCache().get("thief_aura_task") != null)
            ((BukkitTask) player.getCache().remove("thief_aura_task")).cancel();

        if(player.isOnline()) {
            if(player.getPlayer().hasPotionEffect(PotionEffectType.ABSORPTION))
                player.getPlayer().removePotionEffect(PotionEffectType.ABSORPTION);

            if(player.getPlayer().hasPotionEffect(PotionEffectType.WEAKNESS))
                player.getPlayer().removePotionEffect(PotionEffectType.WEAKNESS);

            player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0, false, false));

            player.sendMessage("§aVotre Aura a été désactivée, vos coordonnées ne seront plus divulguées.");
        }
    }

    public static void resetThievesAction(IUPlayer iup) {
        iup.getCache().set("thief_aura", false);

        ArrayList<IUPlayer> chosen = new ArrayList<>(InspectorUHC.get().getGameManager().getAlivePlayers());
        if(chosen.contains(iup)) chosen.remove(iup);
        Random rand = new Random();
        while(chosen.size() > 2) // Size of chosen must be < 2
            chosen.remove(rand.nextInt(chosen.size()));
        iup.getCache().set("can_see", chosen);

        if(iup.isOnline()) {
            iup.getPlayer().removePotionEffect(PotionEffectType.ABSORPTION);
            iup.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0, false, false));
            iup.sendTitle("§6(⌐■_■)", "", 20 * 5);
            iup.sendMessage("§6(⌐■_■) Vous connaissez l'identité de :");
            for (IUPlayer chosenOne : (ArrayList<IUPlayer>) iup.getCache().get("can_see")) {
                iup.seeRoleOf(chosenOne);
                Team chosenTeam = Team.getTeamForPlayer(chosenOne);
                iup.sendMessage("§7 - §l" + chosenOne.getName() + " est " + chosenTeam.getColor() + chosenTeam.getName().substring(0, chosenTeam.getName().length() - 1));
            }
        }
    }
}
