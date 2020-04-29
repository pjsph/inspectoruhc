package me.pjsph.inspectoruhc.scenarios;

import me.pjsph.inspectoruhc.InspectorUHC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class CutClean extends Scenario implements Listener {

    @EventHandler
    public void onBreakBlock(BlockBreakEvent e) {
        if(e.isCancelled()) return;
        if (Scenarios.CUTCLEAN.isEnabled() && Scenarios.TRIPLE_ORES.isEnabled()) {
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
        if (Scenarios.CUTCLEAN.isEnabled() && !Scenarios.TRIPLE_ORES.isEnabled()) {
            final Block Block = e.getBlock();
            final Location loc = new Location(Block.getWorld(), Block.getLocation().getBlockX() + 0.5, Block.getLocation().getBlockY() + 0.5, Block.getLocation().getBlockZ() + 0.5);
            if (Block.getType() == Material.IRON_ORE) {
                Block.setType(Material.AIR);
                Block.getWorld().dropItem(loc, new ItemStack(Material.IRON_INGOT, 1));
                ((ExperienceOrb)Block.getWorld().spawn(loc, (Class)ExperienceOrb.class)).setExperience(2);
            }
            if (Block.getType() == Material.COAL_ORE) {
                Block.setType(Material.AIR);
                Block.getWorld().dropItem(loc, new ItemStack(Material.COAL, 1));
                ((ExperienceOrb)Block.getWorld().spawn(loc, (Class)ExperienceOrb.class)).setExperience(2);
            }
            if (Block.getType() == Material.GOLD_ORE) {
                Block.setType(Material.AIR);
                Block.getWorld().dropItem(loc, new ItemStack(Material.GOLD_INGOT, 1));
                ((ExperienceOrb)Block.getWorld().spawn(loc, (Class)ExperienceOrb.class)).setExperience(3);
            }
            if (Block.getType() == Material.DIAMOND_ORE) {
                Block.setType(Material.AIR);
                Block.getWorld().dropItem(loc, new ItemStack(Material.DIAMOND, 1));
                ((ExperienceOrb)Block.getWorld().spawn(loc, (Class)ExperienceOrb.class)).setExperience(4);
            }
            if (Block.getType() == Material.QUARTZ_ORE) {
                Block.setType(Material.AIR);
                Block.getWorld().dropItem(loc, new ItemStack(Material.QUARTZ, 3));
                ((ExperienceOrb)Block.getWorld().spawn(loc, (Class)ExperienceOrb.class)).setExperience(6);
            }
            if (Block.getType() == Material.EMERALD_ORE) {
                Block.setType(Material.AIR);
                Block.getWorld().dropItem(loc, new ItemStack(Material.EMERALD, 3));
                ((ExperienceOrb)Block.getWorld().spawn(loc, (Class)ExperienceOrb.class)).setExperience(10);
            }
        }
    }

    @EventHandler
    public void onKill(EntityDeathEvent e) {
        if (Scenarios.CUTCLEAN.isEnabled()) {
            if (e.getEntityType() == EntityType.PIG) {
                e.getDrops().clear();
                e.getDrops().add(new ItemStack(Material.GRILLED_PORK, 2));
            }
            if (e.getEntityType() == EntityType.COW) {
                e.getDrops().clear();
                e.getDrops().add(new ItemStack(Material.COOKED_BEEF, 2));
                e.getDrops().add(new ItemStack(Material.LEATHER));
            }
            if (e.getEntityType() == EntityType.CHICKEN) {
                e.getDrops().clear();
                e.getDrops().add(new ItemStack(Material.COOKED_CHICKEN, 2));
                e.getDrops().add(new ItemStack(Material.FEATHER, 2));
            }
            if (e.getEntityType() == EntityType.SHEEP) {
                e.getDrops().clear();
                e.getDrops().add(new ItemStack(Material.COOKED_MUTTON, 2));
            }
            if (e.getEntityType() == EntityType.RABBIT) {
                e.getDrops().clear();
                e.getDrops().add(new ItemStack(Material.COOKED_RABBIT, 1));
                final int random = new Random().nextInt(100);
                if (random < 5) {
                    e.getDrops().add(new ItemStack(Material.RABBIT_FOOT));
                }
            }
            if (e.getEntityType() == EntityType.SPIDER) {
                e.getDrops().clear();
                e.getDrops().add(new ItemStack(Material.SPIDER_EYE, 1));
                e.getDrops().add(new ItemStack(Material.STRING, 2));
            }
            if (e.getEntityType() == EntityType.SKELETON) {
                e.getDrops().clear();
                e.getDrops().add(new ItemStack(Material.BONE, 2));
                e.getDrops().add(new ItemStack(Material.ARROW, 3));
            }
        }
    }

    @Override
    public void configure() {
        this.scenario = Scenarios.CUTCLEAN;
    }

    @Override
    public void activate() {
        Bukkit.getServer().getPluginManager().registerEvents(this, InspectorUHC.get());
    }
}
