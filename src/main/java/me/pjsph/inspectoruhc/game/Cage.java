package me.pjsph.inspectoruhc.game;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.Map;

public class Cage {
    private final Location baseLocation;

    private Material material = Material.BARRIER;
    private MaterialData materialData = null;

    private final boolean buildCeiling;
    private final boolean visibleWalls;

    private int radius = -1;
    private int internalHeight = 3;

    private boolean built = false;
    private Map<Location, SimpleBlock> blocksBuilt = new HashMap<>();

    public Cage(Location baseLocation, boolean buildCeiling, boolean visibleWalls) {
        this.baseLocation = baseLocation;
        this.buildCeiling = buildCeiling;
        this.visibleWalls = visibleWalls;
    }

    public void setCustomMaterial(Material customMaterial, MaterialData data) {
        this.material = customMaterial == null ? Material.BARRIER : customMaterial;
        this.materialData = data;
    }

    public void setCustomMaterial(Material customMaterial, byte data) {
        setCustomMaterial(customMaterial, new MaterialData(this.material, data));
    }

    public void setCustomMaterial(Material customMaterial) {
        setCustomMaterial(customMaterial, null);
    }

    public void setInternalHeight(int internalHeight) {
        this.internalHeight = internalHeight;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    private void setBlock(final Location location, final Material material) {
        setBlock(location, material, null);
    }

    private void setBlock(final Location location, final Material material, byte data) {
        setBlock(location, material, new MaterialData(material, data));
    }

    private void setBlock(final Location location, final Material material, MaterialData data) {
        final Block block = location.getBlock();

        if(!blocksBuilt.containsKey(location))
            blocksBuilt.put(location, new SimpleBlock(block.getType(), block.getState().getData().clone()));

        block.setType(material);
        if(data != null) block.setData(data.getData());
    }

    public void build() {
        if(built) return;

        final int externalRadius = radius + 1;
        final int xMin = baseLocation.getBlockX() - externalRadius;
        final int xMax = baseLocation.getBlockX() + externalRadius;
        final int zMin = baseLocation.getBlockZ() - externalRadius;
        final int zMax = baseLocation.getBlockZ() + externalRadius;

        final World world = baseLocation.getWorld();

        /* Underground */
        for(int x = xMin; x <= xMax; x++)
            for(int z = zMin; z <= zMax; z++)
                setBlock(new Location(world, x, baseLocation.getBlockY() - 2, z), Material.BARRIER);

        /* Ground */
        for(int x = xMin; x <= xMax - 1; x++)
            for(int z = zMin; z <= zMax - 1; z++)
                setBlock(new Location(world, x, baseLocation.getBlockY() - 1, z), material, materialData);

        /* Walls */
        final Material wallsMaterial = visibleWalls ? material : Material.BARRIER;
        final MaterialData wallsMaterialData = visibleWalls ? materialData : null;

        for(int x = xMin; x <= xMax; x++) {
            for(int y = baseLocation.getBlockY() - 1; y < baseLocation.getBlockY() + internalHeight; y++) {
                setBlock(new Location(world, x, y, zMin), wallsMaterial, wallsMaterialData);
                setBlock(new Location(world, x, y, zMax), wallsMaterial, wallsMaterialData);
            }
        }

        for(int z = zMin; z <= zMax; z++) {
            for(int y = baseLocation.getBlockY() - 1; y < baseLocation.getBlockY() + internalHeight; y++) {
                setBlock(new Location(world, xMin, y, z), wallsMaterial, wallsMaterialData);
                setBlock(new Location(world, xMax, y, z), wallsMaterial, wallsMaterialData);
            }
        }

        /* Ceiling */
        final Material ceilingMaterial = buildCeiling ? material : Material.BARRIER;
        final MaterialData ceilingMaterialData = buildCeiling ? materialData : null;

        int xMinCeiling = xMin, xMaxCeiling = xMax, zMinCeiling = zMin, zMaxCeiling = zMax;

        if(buildCeiling && !visibleWalls) {
            xMinCeiling++;
            xMaxCeiling--;
            zMinCeiling++;
            zMaxCeiling--;
        }

        for(int x = xMinCeiling; x <= xMaxCeiling; x++)
            for(int z = zMinCeiling; z <= zMaxCeiling; z++)
                setBlock(new Location(world, x, baseLocation.getBlockY() + internalHeight, z), ceilingMaterial, ceilingMaterialData);

        built = true;
    }

    public void destroy() {
        for(Map.Entry<Location, SimpleBlock> entry : blocksBuilt.entrySet()) {
            final Block block = entry.getKey().getBlock();
            final SimpleBlock originalBlock = entry.getValue();

            block.setType(originalBlock.material);

            if(originalBlock.materialData != null)
                block.getState().setData(originalBlock.materialData);
        }

        built = false;
    }

    private class SimpleBlock {
        public Material material;
        public MaterialData materialData;

        public SimpleBlock(Material material, MaterialData materialData) {
            this.material = material;
            this.materialData = materialData;
        }

    }

}
