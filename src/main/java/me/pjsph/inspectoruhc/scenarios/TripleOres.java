package me.pjsph.inspectoruhc.scenarios;

import me.pjsph.inspectoruhc.InspectorUHC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class TripleOres extends Scenario implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(e.isCancelled()) return;
        if (Scenarios.TRIPLE_ORES.isEnabled()) {
            final Block Block = e.getBlock();
            final Location loc = new Location(Block.getWorld(), Block.getLocation().getBlockX() + 0.5, Block.getLocation().getBlockY() + 0.5, Block.getLocation().getBlockZ() + 0.5);
            if (Block.getType() == Material.IRON_ORE) {
                Block.setType(Material.AIR);
                Block.getWorld().dropItem(loc, new ItemStack(Material.IRON_INGOT, 3));
                ((ExperienceOrb)Block.getWorld().spawn(loc, (Class)ExperienceOrb.class)).setExperience(6);
            }
            if (Block.getType() == Material.COAL_ORE) {
                Block.setType(Material.AIR);
                Block.getWorld().dropItem(loc, new ItemStack(Material.COAL, 3));
                ((ExperienceOrb)Block.getWorld().spawn(loc, (Class)ExperienceOrb.class)).setExperience(3);
            }
            if (Block.getType() == Material.GOLD_ORE) {
                Block.setType(Material.AIR);
                Block.getWorld().dropItem(loc, new ItemStack(Material.GOLD_INGOT, 3));
                ((ExperienceOrb)Block.getWorld().spawn(loc, (Class)ExperienceOrb.class)).setExperience(9);
            }
            if (Block.getType() == Material.DIAMOND_ORE) {
                Block.setType(Material.AIR);
                Block.getWorld().dropItem(loc, new ItemStack(Material.DIAMOND, 3));
                ((ExperienceOrb)Block.getWorld().spawn(loc, (Class)ExperienceOrb.class)).setExperience(12);
            }
            if (Block.getType() == Material.QUARTZ_ORE) {
                Block.setType(Material.AIR);
                Block.getWorld().dropItem(loc, new ItemStack(Material.QUARTZ, 3));
                ((ExperienceOrb)Block.getWorld().spawn(loc, (Class)ExperienceOrb.class)).setExperience(12);
            }
            if (Block.getType() == Material.EMERALD_ORE) {
                Block.setType(Material.AIR);
                Block.getWorld().dropItem(loc, new ItemStack(Material.EMERALD, 3));
                ((ExperienceOrb)Block.getWorld().spawn(loc, (Class)ExperienceOrb.class)).setExperience(15);
            }
        }
    }

    @Override
    public void configure() {
        this.scenario = Scenarios.TRIPLE_ORES;
    }

    @Override
    public void activate() {
        Bukkit.getServer().getPluginManager().registerEvents(this, InspectorUHC.get());
    }
}
