package me.pjsph.inspectoruhc.borders;

import org.bukkit.Location;
import org.bukkit.World;

public class VanillaWorldBorder extends WorldBorder {

    private final World world;
    private final org.bukkit.WorldBorder border;

    public VanillaWorldBorder(World world) {
        this.world = world;
        this.border = world.getWorldBorder();
    }

    @Override
    public void init() {
        setDamageBuffer(5);
        setDamageAmount(0.2);
        setWarningDistance(5);
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public double getDiameter() {
        return border.getSize();
    }

    @Override
    public void setDiameter(double diameter) {
        border.setSize(diameter);
    }

    @Override
    public void setDiameter(double diameter, long time) {
        border.setSize(diameter, time);
    }

    @Override
    public Location getCenter() {
        return border.getCenter();
    }

    @Override
    public void setCenter(double x, double y) {
        border.setCenter(x, y);
    }

    @Override
    public void setCenter(Location center) {
        border.setCenter(center);
    }

    @Override
    public double getDamageBuffer() {
        return border.getDamageBuffer();
    }

    @Override
    public void setDamageBuffer(double distance) {
        border.setDamageBuffer(distance);
    }

    @Override
    public double getDamageAmount() {
        return border.getDamageAmount();
    }

    @Override
    public void setDamageAmount(double damage) {
        border.setDamageAmount(damage);
    }

    @Override
    public int getWarningTime() {
        return border.getWarningTime();
    }

    @Override
    public void setWarningTime(int seconds) {
        border.setWarningTime(seconds);
    }

    @Override
    public int getWarningDistance() {
        return border.getWarningDistance();
    }

    @Override
    public void setWarningDistance(int blocks) {
        border.setWarningDistance(blocks);
    }

    @Override
    public MapShape getShape() {
        return MapShape.SQUARED;
    }

    @Override
    public void setShape(MapShape shape) {}
}
