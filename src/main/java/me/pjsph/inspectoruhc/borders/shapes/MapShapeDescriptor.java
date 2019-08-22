package me.pjsph.inspectoruhc.borders.shapes;

import org.bukkit.Location;

public abstract class MapShapeDescriptor {

    public abstract boolean isInsideBorder(final Location location, final Double diameter, final Location center);

    public abstract double getDistanceToBorder(final Location location, final Double diameter, final Location center);

}
