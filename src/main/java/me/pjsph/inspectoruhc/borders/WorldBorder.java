package me.pjsph.inspectoruhc.borders;

import org.bukkit.Location;
import org.bukkit.World;

public abstract class WorldBorder {

    public void init() {}

    public abstract World getWorld();

    public abstract double getDiameter();

    public abstract void setDiameter(double diameter);

    public abstract void setDiameter(double diameter, long time);

    public abstract Location getCenter();

    public abstract void setCenter(double x, double y);

    public abstract void setCenter(Location center);

    public abstract double getDamageBuffer();

    public abstract void setDamageBuffer(double distance);

    public abstract double getDamageAmount();

    public abstract void setDamageAmount(double damage);

    public abstract int getWarningTime();

    public abstract void setWarningTime(int seconds);

    public abstract int getWarningDistance();

    public abstract void setWarningDistance(int blocks);

    public abstract MapShape getShape();

    public abstract void setShape(MapShape shape);

    public static WorldBorder getInstance(World world) {
        return new VanillaWorldBorder(world);
    }
}
