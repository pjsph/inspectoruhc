package me.pjsph.inspectoruhc.timer;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.kits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Timer extends BukkitRunnable {

    private InspectorUHC plugin;

    public Timer(InspectorUHC plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        int seconds = plugin.getTimerManager().decSecondsLeft(), minutes = 0;

        if(seconds == -1) {
            plugin.getTimerManager().setSecondsLeft(59);
            minutes = plugin.getTimerManager().decMinutesLeft();

            if(minutes == -1) {
                plugin.getTimerManager().setMinutesLeft(19);
                plugin.getTimerManager().setSecondsLeft(59);
                plugin.getTimerManager().incEpisode();

                String txt = "";

                plugin.getServer().broadcastMessage(ChatColor.GOLD + "Episode " + plugin.getTimerManager().getEpisode());

                if(plugin.getTimerManager().getEpisode() == 2) {
                    txt = ChatColor.RED + "La bordure rétrécira à partir de 10min à l'épisode 2, et pendant 1h30.";
                    plugin.getServer().broadcastMessage(txt);
                }
            }

            if(!plugin.getGameManager().isRolesActivated()) {
                minutes = plugin.getTimerManager().decMinutesRolesLeft();

                if(minutes == -1) {
                    plugin.getGameManager().activateRoles();

                    for(Player player : plugin.getTeamManager().getPlayersTeam().keySet()) {
                        switch (plugin.getTeamManager().getPlayersTeam().get(player).getName()) {
                            case "Inspecteurs":
                                player.sendMessage(ChatColor.DARK_AQUA + "-------------------------------------------");
                                player.sendMessage(ChatColor.DARK_AQUA + "Vous êtes un Inspecteur ! Vous devez démasquer et tuer les " + ChatColor.RED + "Criminels");
                                player.sendMessage(ChatColor.DARK_AQUA + "Attention cependant : ceux-ci connaissent votre identité.");
                                player.sendMessage(ChatColor.DARK_AQUA + "-------------------------------------------");
                                break;

                            case "Criminels":
                                player.sendMessage(ChatColor.RED + "-------------------------------------------");
                                player.sendMessage(ChatColor.RED + "Vous êtes un Criminel ! Vous devez tuer les " + ChatColor.DARK_AQUA + "Inspecteurs");
                                player.sendMessage(ChatColor.RED + "Vous reconnaitrez un " + ChatColor.DARK_AQUA + "Inspecteur " + ChatColor.RED + "en le voyant.");
                                player.sendMessage(ChatColor.RED + "Vous pouvez activer votre aura de Serial Killer pour perdre votre effet Weakness et le remplacer par Force I.");
                                player.sendMessage(ChatColor.RED + "Attention cependant : les " + ChatColor.DARK_AQUA + "Inspecteurs " + ChatColor.RED + "pourront alors vous tracer.");
                                player.sendMessage(ChatColor.RED + "/f (comme furie) pour activer/désactiver l'aura.");
                                player.sendMessage(ChatColor.RED + "-------------------------------------------");
                                break;
                        }
                    }

                    for(Player player : plugin.getGameManager().getAllPlayers()) {
                        player.sendMessage(ChatColor.AQUA + "Les équipes ont été annoncées.");
                    }

                    if(plugin.getGameManager().getAllPlayers().size() == 1)
                        plugin.getGameManager().finish(0);
                }
            }

            if(!plugin.getGameManager().isKitsActivated()) {
                minutes = plugin.getTimerManager().decMinutesKitsLeft();

                if(minutes == -1) {
                    plugin.getGameManager().activateKits();

                    for(String sId : Kit.getKitOwners().keySet()) {
                        Player player = Bukkit.getPlayer(UUID.fromString(sId));

                        if(player != null && plugin.getGameManager().getAllPlayers().contains(player)) {
                            player.sendMessage(ChatColor.DARK_AQUA + "-------------------------------------------");
                            player.sendMessage(ChatColor.DARK_AQUA + "Voici votre kit : " + Kit.getFromOwner(UUID.fromString(sId)).getName() + ".");
                            player.sendMessage(ChatColor.DARK_AQUA + "Celui-ci vous donne un objet, un effet ou une capacité spéciale :");
                            player.sendMessage(ChatColor.DARK_AQUA + Kit.getFromOwner(UUID.fromString(sId)).getDescription());
                            player.sendMessage(ChatColor.DARK_AQUA + "-------------------------------------------");
                        }
                    }
                }
            }
        }

        plugin.getScoreboardManager().matchInfo();
    }
}
