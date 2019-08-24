package me.pjsph.inspectoruhc.spawns;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.spawns.generators.CircularSpawnPointsGenerator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SpawnsManager {
    private InspectorUHC p;
    private LinkedList<Location> spawnPoints = new LinkedList<>();

    public SpawnsManager(InspectorUHC p) {
        this.p = p;
    }

    public void addSpawnPoint(final Vector vec) {
        addSpawnPoint(p.getServer().getWorlds().get(0), vec.getX(), vec.getZ());
    }

    public void addSpawnPoint(final double x, final double z) {
        addSpawnPoint(p.getServer().getWorlds().get(0), x, z);
    }

    public void addSpawnPoint(final World world, final double x, final double z) {
        addSpawnPoint(new Location(world, x, 0, z));
    }

    public void addSpawnPoint(final Location location) {
        Location spawnPoint = location.clone();

        if(!(spawnPoint.getWorld().getEnvironment() == World.Environment.NETHER)) {
            spawnPoint.setY(location.getWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ()) + 120);
        } else {
            throw new RuntimeException("Cannot define a spawn point in the nether.");
        }

        if(!p.getBorderManager().isInsideBorder(spawnPoint)) {
            throw new IllegalArgumentException("The given spawn location is outside the border.");
        }

        spawnPoints.add(spawnPoint);
    }

    public List<Location> getSpawnPoints() {
        return spawnPoints;
    }

    public boolean removeSpawnPoint(Location location, boolean precise) {
        final List<Location> toRemove = getSpawnPoints().stream()
                .filter(spawn -> location.getWorld().equals(spawn.getWorld()))
                .filter(spawn -> precise
                    && location.getX() == spawn.getX()
                    && location.getZ() == spawn.getZ() || !precise
                    && location.getBlockX() == spawn.getBlockX()
                    && location.getBlockZ() == spawn.getBlockZ())
                .collect(Collectors.toCollection(LinkedList::new));

        for(Location spawnToRemove : toRemove) {
            while(spawnPoints.remove(spawnToRemove))
                ;
        }

        return toRemove.size() != 0;
    }

    public void reset() {
        spawnPoints.clear();
    }

    public void generateSpawnPoints(World world, int spawnCount, int regionDiameter, int minimalDistanceBetweenTwoPoints, double xCenter, double zCenter) throws Exception {
        CircularSpawnPointsGenerator generator = new CircularSpawnPointsGenerator();

        final Set<Location> spawnPoints = generator.generate(world, spawnCount, regionDiameter, minimalDistanceBetweenTwoPoints, xCenter, zCenter);
        spawnPoints.forEach(this::addSpawnPoint);
    }
}
