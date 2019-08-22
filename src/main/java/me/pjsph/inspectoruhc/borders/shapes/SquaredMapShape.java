package me.pjsph.inspectoruhc.borders.shapes;

import org.bukkit.Location;
import org.bukkit.World;

public class SquaredMapShape extends MapShapeDescriptor {

    @Override
    public boolean isInsideBorder(Location location, Double diameter, Location center) {
        final Integer halfMapSize = (int) Math.floor(diameter / 2);
        final Integer x = location.getBlockX();
        final Integer z = location.getBlockZ();

        final Integer limitXInf = center.clone().add(-halfMapSize, 0, 0).getBlockX();
        final Integer limitXSup = center.clone().add(halfMapSize, 0, 0).getBlockX();
        final Integer limitZInf = center.clone().add(0, 0, -halfMapSize).getBlockZ();
        final Integer limitZSup = center.clone().add(0, 0, halfMapSize).getBlockZ();

        return !(x < limitXInf || x > limitXSup || z < limitZInf || z > limitZSup);
    }

    @Override
    public double getDistanceToBorder(Location location, Double diameter, Location center) {
        if(!location.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
            return -1;
        }

        if(isInsideBorder(location, diameter, center)) {
            return -1;
        }

        final Integer halfMapSize = (int) Math.floor(diameter / 2);
        final Integer x = location.getBlockX();
        final Integer z = location.getBlockZ();

        final Integer limitXInf = center.clone().add(-halfMapSize, 0, 0).getBlockX();
        final Integer limitXSup = center.clone().add(halfMapSize, 0, 0).getBlockX();
        final Integer limitZInf = center.clone().add(0, 0, -halfMapSize).getBlockZ();
        final Integer limitZSup = center.clone().add(0, 0, halfMapSize).getBlockZ();

        if(x > limitXSup && z < limitZSup && z > limitZInf) // East
            return Math.abs(x - limitXSup);

        else if(x < limitXInf && z < limitZSup && z > limitZInf) // West
            return Math.abs(x - limitXInf);

        else if(z > limitZSup && x < limitXSup && x > limitXInf) // South
            return Math.abs(z - limitZSup);

        else if(z < limitZInf && x < limitXSup && x > limitXInf) // North
            return Math.abs(z - limitZInf);

        else if(x > limitXSup && z < limitZInf) // North-East
            return (int) location.distance(new Location(location.getWorld(), limitXSup, location.getBlockY(), limitZInf));

        else if(x > limitXSup && z > limitZSup) // South-East
            return (int) location.distance(new Location(location.getWorld(), limitXSup, location.getBlockY(), limitZSup));

        else if(x < limitXInf && z > limitZSup) // South-West
            return (int) location.distance(new Location(location.getWorld(), limitXInf, location.getBlockY(), limitZSup));

        else if(x < limitXInf && z < limitZInf)
            return (int) location.distance(new Location(location.getWorld(), limitXInf, location.getBlockY(), limitZInf));

        else return -1; // Should never happen.
    }
}
