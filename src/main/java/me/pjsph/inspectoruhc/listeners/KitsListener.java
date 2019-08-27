package me.pjsph.inspectoruhc.listeners;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.events.*;
import me.pjsph.inspectoruhc.game.Cage;
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

    private static HashMap<UUID, Boolean> inspectsAction = new HashMap<>();
    private static HashMap<UUID, Integer> thievesAction = new HashMap<>();
    private static HashMap<UUID, Boolean> thievesAura = new HashMap<>();
    private static HashMap<UUID, BukkitTask> auraTasks = new HashMap<>();

    public KitsListener(InspectorUHC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onKitChosen(KitChosenEvent ev) {
        Kit kit = Kit.getKit(ev.getPlayerUUID());

        Player pl = Bukkit.getPlayer(ev.getPlayerUUID());

        /* AGILITY: Set speed effect */
        if(kit.getKitType() == Kit.KIT_TYPES.AGILITY) {
            if(pl != null && pl.isOnline()) {
                pl.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
            }
        /* SPY_GLASSES: Add to canSpy */
        } else if(kit.getKitType() == Kit.KIT_TYPES.SPY_GLASSES) {
            inspectsAction.put(ev.getPlayerUUID(), true);
        /* ROUGHNECK: Add to canRespawn */
        } else if(kit.getKitType() == Kit.KIT_TYPES.ROUGHNECK) {
            inspectsAction.put(ev.getPlayerUUID(), true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent ev) {
        if(plugin.getGameManager().isKitsActivated()) {
            Player pl = ev.getPlayer();

            Kit kit = Kit.getKit(pl.getUniqueId());
            if(kit != null) {

                /* AGILITY: Set speed effect */
                if(kit.getKitType() == Kit.KIT_TYPES.AGILITY) {
                    if(pl.hasPotionEffect(PotionEffectType.SPEED))
                        pl.removePotionEffect(PotionEffectType.SPEED);

                    pl.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
                }
            }
        }

        if(plugin.getGameManager().isRolesActivated()) {
            Player pl = ev.getPlayer();

            if(Team.getTeamForPlayer(pl) == Team.THIEVES) {
                if(pl.hasPotionEffect(PotionEffectType.WEAKNESS))
                    pl.removePotionEffect(PotionEffectType.WEAKNESS);

                if(pl.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
                    pl.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);

                pl.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0, false, false));
            }
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent ev) {
        Player pl = ev.getPlayer();

        if(pl != null && Team.getTeamForPlayer(pl) == Team.THIEVES && thievesAura.get(pl.getUniqueId())) {
            thievesAura.put(pl.getUniqueId(), false);

            if(auraTasks.containsKey(pl.getUniqueId())) {
                auraTasks.get(pl.getUniqueId()).cancel();
                auraTasks.remove(pl.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent ev) {
        if(!(ev.getEntity() instanceof Player)) return;

        Player victim = (Player) ev.getEntity();

        if((victim.getHealth() <= ev.getFinalDamage()) && !plugin.getGameManager().isInvincible() && plugin.getGameManager().isKitsActivated()) {
            Kit kit = Kit.getKit(victim.getUniqueId());
            if(kit != null) {

                /* ROUGHNECK: If it's its first death, the player doesn't die */
                if(kit.getKitType() != Kit.KIT_TYPES.ROUGHNECK || !inspectsAction.get(victim.getUniqueId())) {
                    return;
                } else {

                    /* Cancel death */
                    ev.setCancelled(true);

                    /* Give a new inventory */
                    victim.getInventory().clear();
                    victim.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.IRON_HELMET), new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_BOOTS)});
                    victim.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
                    victim.getInventory().setItem(1, new ItemStack(Material.COOKED_BEEF, 32));
                    victim.getInventory().setItem(2, new ItemStack(Material.IRON_PICKAXE));
                    victim.getInventory().setItem(17, new ItemStack(Material.GOLD_INGOT, 24));

                    /* Give Resistance effect to the player */
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30 * 20, 4, false, false));

                    /* Teleport away the player */
                    plugin.getSpawnsManager().reset();
                    try {
                        plugin.getSpawnsManager().generateSpawnPoints(
                                plugin.getServer().getWorlds().get(0),
                                1,
                                plugin.getBorderManager().getCurrentBorderDiameter() - 25,
                                250,
                                plugin.getServer().getWorlds().get(0).getSpawnLocation().getX(),
                                plugin.getServer().getWorlds().get(0).getSpawnLocation().getZ()
                        );

                        Location spawnPoint = plugin.getSpawnsManager().getSpawnPoints().stream().findFirst().get();
                        Teleporter teleporter = new Teleporter();

                        final Cage cage = new Cage(spawnPoint, true, true);
                        teleporter.setSpawnForPlayer(victim.getUniqueId(), spawnPoint);
                        if(cage != null) teleporter.setCageForPlayer(victim.getUniqueId(), cage);

                        teleporter.teleportPlayer(victim.getUniqueId());

                        /* Used to prevent PlayerDeathEvent to be called because inspectsAction is set to false here, before PlayerDeathEvent's firing */
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            inspectsAction.put(victim.getUniqueId(), false);

                            /* Heal the player as well 'cause he keeps damages (why?) */
                            victim.setHealth(20d);
                            victim.setFoodLevel(20);
                            victim.setSaturation(20f);
                        }, 5L);
                    } catch(Exception e) {
                        victim.sendMessage("§cUne erreur s'est produite : impossible de vous ressusciter.");
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSpy(SpyEvent ev) {
        OfflinePlayer spied = Bukkit.getOfflinePlayer(ev.getSpied());
        Player spy = Bukkit.getPlayer(ev.getSpy());

        if((inspectsAction.containsKey(ev.getSpy()) && inspectsAction.get(ev.getSpy())) ||
                (thievesAction.containsKey(ev.getSpy()) && thievesAction.get(ev.getSpy()) > 0)) {

            Team team = Team.getTeamForPlayer(spied);

            if(team != null) {
                spy.sendMessage("§aLe joueur espionné est un " + team.getColor() + team.getName().substring(0, team.getName().length() - 1) + "§a.");
            } else {
                spy.sendMessage("§cUne erreur est survenue, l'équipe du joueur est introuvable.");
            }

            if(inspectsAction.containsKey(ev.getSpy()))
                inspectsAction.put(spy.getUniqueId(), false);
            else if(thievesAction.containsKey(ev.getSpy())) {
                thievesAction.put(spy.getUniqueId(), thievesAction.get(spy.getUniqueId()) - 1);
                spy.sendMessage("§7Il vous reste (" + thievesAction.get(spy.getUniqueId()) + "/2) possibilités d'espionner pendant cet épisode.");
            }
        } else {
            if(inspectsAction.containsKey(ev.getSpy()))
                spy.sendMessage("§cVous avez épuisé votre quota d'espionnage (0/1), recharge au prochain épisode.");

            else
                spy.sendMessage("§cVous avez épuisé votre quota d'espionnage (0/2), recharge au prochain épisode.");
        }
    }

    @EventHandler
    public void onEpisodeChange(EpisodeChangedEvent ev) {

        /* Reset SPY action */
        inspectsAction.entrySet().stream()
                .filter(e -> Kit.getKit(e.getKey()).getKitType() == Kit.KIT_TYPES.SPY_GLASSES)
                .forEach(e -> inspectsAction.put(e.getKey(), true));

        /* Add one THIEF action */
        thievesAction.entrySet().stream()
                .filter(e -> e.getValue() < 2)
                .forEach(e -> thievesAction.put(e.getKey(), e.getValue() + 1));

        for(UUID id : Kit.getOwners(Kit.KIT_TYPES.UNDERSENSE)) {
            Player player = Bukkit.getPlayer(id);

            if(player == null || !player.isOnline()) return;

            List<String> near = new ArrayList<>();
            for(Player thief : Team.THIEVES.getOnlinePlayers()) {
                if(Math.round(thief.getLocation().distance(player.getLocation())) <= 100.0d) {
                    near.add(thief.getName());
                }
            }
            player.sendMessage("");
            player.sendMessage("§3" + near.size() + " §cCriminel(s) §3est/sont à proximité (< 100 blocs).");
            player.sendMessage("");
        }
    }

    @EventHandler
    public void onActivateAura(ActivateAuraEvent ev) {
        Player player = Bukkit.getPlayer(ev.getPlayerUUID());

        if(player != null && player.isOnline()) {
            thievesAura.put(ev.getPlayerUUID(), true);

            if(player.hasPotionEffect(PotionEffectType.WEAKNESS))
                player.removePotionEffect(PotionEffectType.WEAKNESS);

            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));

            player.sendMessage("§aVous activez votre Aura de serial killer. Vous gagnez Strength I mais vos coordonnées sont divulguées.");
            Bukkit.broadcastMessage("§cUn Criminel a activé son Aura de serial killer.");
            (new IUSound(Sound.WITHER_SPAWN)).broadcast();

            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    if(player != null)
                        Bukkit.broadcastMessage("§4[§cA§6U§2R§aA§3] §cUn Criminel se trouve aux coordonnées suivantes : §rX: " + player.getLocation().getBlockX() + " Z: " + player.getLocation().getBlockZ());
                }
            }.runTaskTimer(plugin, 0L, 10 * 20L);

            auraTasks.put(player.getUniqueId(), task);
        }
    }

    @EventHandler
    public void onDesactivateAura(DesactivateAuraEvent ev) {
        Player player = Bukkit.getPlayer(ev.getPlayerUUID());

        if(player != null && player.isOnline()) {
            thievesAura.put(ev.getPlayerUUID(), false);

            if(player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);

            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0, false, false));

            player.sendMessage("§aVous désactivez votre Aura, vos coordonnées ne seront plus divulguées.");

            if(auraTasks.get(player.getUniqueId()) != null) {
                auraTasks.get(player.getUniqueId()).cancel();
                auraTasks.remove(player.getUniqueId());
            }
        }
    }

    public static boolean canInspectsAction(UUID uuid) {
        return inspectsAction.get(uuid);
    }

    public static void resetThievesAction(UUID uuid) {
        Player pl = Bukkit.getPlayer(uuid);

        if(pl != null && pl.isOnline()) pl.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0, false, false));
        thievesAction.put(uuid, 2);
        thievesAura.put(uuid, false);
    }

    public static boolean isAuraActivated(UUID playerUUID) {
        return thievesAura.get(playerUUID);
    }
}
