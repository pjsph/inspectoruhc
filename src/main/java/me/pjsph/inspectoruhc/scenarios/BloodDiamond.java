package me.pjsph.inspectoruhc.scenarios;

import me.pjsph.inspectoruhc.InspectorUHC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class BloodDiamond extends Scenario implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent e) {
        if(e.isCancelled()) return;
        if (Scenarios.BLOODDIAMONDS.isEnabled()) {
            final Block block = e.getBlock();
            final Player player = e.getPlayer();
            if (block.getType() == Material.DIAMOND_ORE) {
                this.damage(player, 1.0);
                if(Scenarios.CUTCLEAN.isEnabled() || Scenarios.TRIPLE_ORES.isEnabled()) {
                    e.setCancelled(true);
                    final Location loc = new Location(block.getWorld(), block.getLocation().getBlockX() + 0.5, block.getLocation().getBlockY() + 0.5, block.getLocation().getBlockZ() + 0.5);

                    if(Scenarios.TRIPLE_ORES.isEnabled()) {
                        block.setType(Material.AIR);
                        block.getWorld().dropItem(loc, new ItemStack(Material.DIAMOND, 3));
                        ((ExperienceOrb) block.getWorld().spawn(loc, (Class) ExperienceOrb.class)).setExperience(12);
                    } else if(Scenarios.CUTCLEAN.isEnabled()) {
                        block.setType(Material.AIR);
                        block.getWorld().dropItem(loc, new ItemStack(Material.DIAMOND, 1));
                        ((ExperienceOrb)block.getWorld().spawn(loc, (Class)ExperienceOrb.class)).setExperience(4);
                    }
                }
            }
        }
    }

    protected void damage(Player player, double amount) {
        EntityDamageEvent event = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.CUSTOM, amount);
        Bukkit.getPluginManager().callEvent(event);
        player.damage(event.getDamage());
    }

    @Override
    public void configure() {
        this.scenario = Scenarios.BLOODDIAMONDS;
    }

    @Override
    public void activate() {
        Bukkit.getServer().getPluginManager().registerEvents(this, InspectorUHC.get());
    }
}
