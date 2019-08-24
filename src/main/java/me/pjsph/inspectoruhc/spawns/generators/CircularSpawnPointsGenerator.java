package me.pjsph.inspectoruhc.spawns.generators;

import me.pjsph.inspectoruhc.InspectorUHC;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CircularSpawnPointsGenerator {
    private final InspectorUHC p = InspectorUHC.get();

    public Set<Location> generate(final World world, final int spawnCount, final int regionDiameter, final int minimalDistanceBetweenToPoints, final double xCenter, final double zCenter) throws Exception {
        final int usedRegionDiameter = regionDiameter - 1;

        int countGeneratedPoints = 0;
        final Set<Location> generatedPoints = new HashSet<>();

        int currentCircleDiameter = usedRegionDiameter;

        generationLoop:
        while(currentCircleDiameter >= minimalDistanceBetweenToPoints) {
            final double denseCircleAngle = 2 * Math.sin(((double) minimalDistanceBetweenToPoints / 2) / ((double) currentCircleDiameter /2));
            final int pointsPerDenseCircles = (int) Math.floor(2 * Math.PI / denseCircleAngle);

            final double angleBetweenToPoints;

            if(pointsPerDenseCircles < spawnCount - countGeneratedPoints) {
                angleBetweenToPoints = 2 * Math.PI / ((double) pointsPerDenseCircles);
            } else {
                angleBetweenToPoints = 2 * Math.PI / ((double) spawnCount - countGeneratedPoints);
            }

            final double startAngle = (new Random()).nextDouble() * 2 * Math.PI;
            double currentAngle = startAngle;

            while(currentAngle <= 2 * Math.PI - angleBetweenToPoints + startAngle) {
                Location point = new Location(
                        world,
                        (currentCircleDiameter / 2) * Math.cos(currentAngle) + xCenter,
                        0,
                        (currentCircleDiameter / 2) * Math.sin(currentAngle) + zCenter
                );

                currentAngle += angleBetweenToPoints;

                if(!p.getBorderManager().isInsideBorder(point, regionDiameter)) {
                    continue;
                }

                final Block surfaceAirBlock = world.getHighestBlockAt(point);
                final Block surfaceBlock = surfaceAirBlock.getRelative(BlockFace.DOWN);

                generatedPoints.add(point);
                countGeneratedPoints++;

                if(countGeneratedPoints >= spawnCount) {
                    break generationLoop;
                }
            }

            currentCircleDiameter -= 2 * minimalDistanceBetweenToPoints;
        }

        if(generatedPoints.size() < spawnCount) {
            throw new Exception("Cannot generate the spawn point in circles: not enough space.");
        } else {
            return generatedPoints;
        }
    }

}
