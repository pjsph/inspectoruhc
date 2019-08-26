package me.pjsph.inspectoruhc.listeners;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.events.EpisodeChangedEvent;
import me.pjsph.inspectoruhc.events.KitChosenEvent;
import me.pjsph.inspectoruhc.events.SpyEvent;
import me.pjsph.inspectoruhc.game.Cage;
import me.pjsph.inspectoruhc.game.Teleporter;
import me.pjsph.inspectoruhc.kits.Kit;
import me.pjsph.inspectoruhc.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitsListener implements Listener {

    private InspectorUHC plugin;

    private static HashMap<UUID, Boolean> canAction = new HashMap<>();

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
                pl.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false));
            }
        /* SPY_GLASSES: Add to canSpy */
        } else if(kit.getKitType() == Kit.KIT_TYPES.SPY_GLASSES) {
            canAction.put(ev.getPlayerUUID(), true);
        /* ROUGHNECK: Add to canRespawn */
        } else if(kit.getKitType() == Kit.KIT_TYPES.ROUGHNECK) {
            canAction.put(ev.getPlayerUUID(), true);
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

                    pl.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false));
                }
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
                if(kit.getKitType() != Kit.KIT_TYPES.ROUGHNECK || !canAction.get(victim.getUniqueId())) {
                    return;
                } else {

                    /* Give a new inventory */
                    victim.getInventory().clear();
                    victim.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.IRON_HELMET), new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_BOOTS)});
                    victim.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
                    victim.getInventory().setItem(1, new ItemStack(Material.COOKED_BEEF, 32));
                    victim.getInventory().setItem(2, new ItemStack(Material.IRON_PICKAXE));
                    victim.getInventory().setItem(17, new ItemStack(Material.GOLD_INGOT, 24));

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

                        canAction.put(victim.getUniqueId(), false);
                    } catch(Exception e) {
                        victim.sendMessage("§cUne erreur s'est produite : impossible de vous ressusciter.");
                        return;
                    }

                    /* Cancel death */
                    ev.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onSpy(SpyEvent ev) {
        OfflinePlayer spied = Bukkit.getOfflinePlayer(ev.getSpied());
        Player spy = Bukkit.getPlayer(ev.getSpy());

        if(canAction.containsKey(ev.getSpy()) && canAction.get(ev.getSpy())) {

            Team team = Team.getTeamForPlayer(spied);

            if(team != null) {
                spy.sendMessage("§aLe joueur espionné est un " + team.getColor() + team.getName().substring(0, team.getName().length() - 1) + "§a.");
            } else {
                spy.sendMessage("§cUne erreur est survenue, l'équipe du joueur est introuvable.");
            }

            canAction.put(spy.getUniqueId(), false);
        } else {
            spy.sendMessage("§cVous avez déjà espionné quelqu'un cet épisode.");
        }
    }

    @EventHandler
    public void onEpisodeChange(EpisodeChangedEvent ev) {

        /* Reset SPY action */
        canAction.entrySet().stream()
                .filter(e -> Kit.getKit(e.getKey()).getKitType() == Kit.KIT_TYPES.SPY_GLASSES)
                .forEach(e -> canAction.put(e.getKey(), true));

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

    public static boolean canAction(UUID uuid) {
        return canAction.get(uuid);
    }
}
