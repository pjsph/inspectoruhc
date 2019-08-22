package me.pjsph.inspectoruhc.borders;

import me.pjsph.inspectoruhc.borders.shapes.MapShapeDescriptor;
import me.pjsph.inspectoruhc.borders.shapes.SquaredMapShape;

public enum MapShape {
    SQUARED(new SquaredMapShape());

    private MapShapeDescriptor shape;

    MapShape(MapShapeDescriptor shape) {
        this.shape = shape;
    }

    public MapShapeDescriptor getShape() {
        return shape;
    }
}
