package me.pjsph.inspectoruhc.listeners;

import me.pjsph.inspectoruhc.game.IUPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        if(!e.isCancelled()) {
            IUPlayer player = IUPlayer.thePlayer(e.getPlayer());
            player.onChat(e.getMessage());
            e.setCancelled(true);
        }
    }

}
