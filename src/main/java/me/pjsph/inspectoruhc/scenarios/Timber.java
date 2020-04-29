package me.pjsph.inspectoruhc.scenarios;

import me.pjsph.inspectoruhc.InspectorUHC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class Timber extends Scenario implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (Scenarios.TIMBER.isEnabled()) {
            if (e.getBlock().getType() == Material.LOG) {
                e.setCancelled(true);
                boolean inWood = false;
                int count = 0;
                for (int y = -13; y <= 13; ++y) {
                    for (int z = -3; z <= 3; ++z) {
                        for (int x = -3; x <= 3; ++x) {
                            final Location loc = e.getBlock().getLocation().add((double)x, (double)y, (double)z);
                            if (loc.getBlock().getType() == Material.LOG || loc.getBlock().getType() == Material.LOG_2) {
                                ++count;
                                if (!inWood) {
                                    inWood = true;
                                }
                                loc.getBlock().breakNaturally();
                            }
                        }
                    }
                }
            }
            if (e.getBlock().getType() == Material.LOG_2) {
                e.setCancelled(true);
                boolean inWood = false;
                int count = 0;
                for (int y = -13; y <= 13; ++y) {
                    for (int z = -3; z <= 3; ++z) {
                        for (int x = -3; x <= 3; ++x) {
                            final Location loc = e.getBlock().getLocation().add((double)x, (double)y, (double)z);
                            if (loc.getBlock().getType() == Material.LOG || loc.getBlock().getType() == Material.LOG_2) {
                                ++count;
                                if (!inWood) {
                                    inWood = true;
                                }
                                loc.getBlock().breakNaturally();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void configure() {
        this.scenario = Scenarios.TIMBER;
    }

    @Override
    public void activate() {
        Bukkit.getServer().getPluginManager().registerEvents(this, InspectorUHC.get());
    }
}
