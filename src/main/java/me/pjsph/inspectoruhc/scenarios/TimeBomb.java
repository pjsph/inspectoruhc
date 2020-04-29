package me.pjsph.inspectoruhc.scenarios;

import me.pjsph.inspectoruhc.InspectorUHC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class TimeBomb extends Scenario implements Listener {

    private static final int COUNT = 30;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        if (Scenarios.TIMEBOMB.isEnabled()) {
            event.getEntity().getLocation().getBlock().setType(Material.CHEST);
            event.getEntity().getLocation().getBlock().getRelative(BlockFace.EAST).setType(Material.CHEST);
            final Chest chest = (Chest)event.getEntity().getLocation().getBlock().getState();
            for (final ItemStack itemStack : event.getDrops()) {
                if (itemStack == null) {
                    continue;
                }
                chest.getInventory().addItem(new ItemStack[] { itemStack });
            }
            chest.update();
            event.getDrops().clear();
            new TimeBombRunnable(chest, TimeBomb.COUNT).runTaskTimer(InspectorUHC.get(), 0L, 20L);
        }
    }


    @Override
    public void configure() {
        this.scenario = Scenarios.TIMEBOMB;
    }

    @Override
    public void activate() {
        Bukkit.getServer().getPluginManager().registerEvents(this, InspectorUHC.get());
    }
}
