package com.badlogic.dream.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.dream.components.PositionComponent;

import java.util.ArrayList;
import java.util.List;

public class ColliderComponent implements Component {

    // === Abstract shape class ===
    public static abstract class Shape {
        public abstract boolean overlaps(Shape other, float xA, float yA, float xB, float yB);
        public abstract void draw(ShapeRenderer renderer, float worldX, float worldY);
        public abstract Rectangle getBounds(float worldX, float worldY);
    }

    // === Circle shape ===
    public static class CircleShape extends Shape {
        public float radius, offsetX, offsetY;

        public CircleShape(float radius, float offsetX, float offsetY) {
            this.radius = radius;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        @Override
        public boolean overlaps(Shape other, float xA, float yA, float xB, float yB) {
            if (other instanceof CircleShape c) {
                float dx = (xA + offsetX) - (xB + c.offsetX);
                float dy = (yA + offsetY) - (yB + c.offsetY);
                float rSum = radius + c.radius;
                return dx * dx + dy * dy <= rSum * rSum;
            }
            if (other instanceof RectShape r) {
                return Intersector.overlaps(
                    new Circle(xA + offsetX, yA + offsetY, radius),
                    new Rectangle(xB + r.offsetX, yB + r.offsetY, r.width, r.height));
            }
            if (other instanceof PolygonShape p) {
                Polygon poly = p.getWorldPolygon(xB, yB);
                float[] verts = poly.getTransformedVertices();

                // Circle center in polygon?
                if (poly.contains(xA + offsetX, yA + offsetY)) {
                    return true;
                }

                // Check each edge against circle
                Circle circle = new Circle(xA + offsetX, yA + offsetY, radius);
                for (int i = 0; i < verts.length; i += 2) {
                    float x1 = verts[i];
                    float y1 = verts[i + 1];
                    float x2 = verts[(i + 2) % verts.length];
                    float y2 = verts[(i + 3) % verts.length];

                    if (Intersector.intersectSegmentCircle(
                        new Vector2(x1, y1), new Vector2(x2, y2),
                        new Vector2(circle.x, circle.y), circle.radius * circle.radius)) {
                        return true;
                    }
                }
                return false;
            }

            return false;
        }

        @Override
        public void draw(ShapeRenderer renderer, float worldX, float worldY) {
            renderer.circle(worldX + offsetX, worldY + offsetY, radius, 100);
        }

        @Override
        public Rectangle getBounds(float worldX, float worldY) {
            return new Rectangle(worldX + offsetX - radius, worldY + offsetY - radius,
                radius * 2, radius * 2);
        }
    }

    // === Rectangle shape ===
    public static class RectShape extends Shape {
        public float width, height, offsetX, offsetY;

        public RectShape(float width, float height, float offsetX, float offsetY) {
            this.width = width;
            this.height = height;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        @Override
        public boolean overlaps(Shape other, float xA, float yA, float xB, float yB) {
            Rectangle r1 = new Rectangle(xA + offsetX, yA + offsetY, width, height);

            if (other instanceof RectShape r) {
                Rectangle r2 = new Rectangle(xB + r.offsetX, yB + r.offsetY, r.width, r.height);
                return r1.overlaps(r2);
            }
            if (other instanceof CircleShape c) {
                return Intersector.overlaps(
                    new Circle(xB + c.offsetX, yB + c.offsetY, c.radius), r1);
            }
            if (other instanceof PolygonShape p) {
                return Intersector.overlapConvexPolygons(
                    p.getWorldPolygon(xB, yB),
                    new Polygon(new float[]{
                        r1.x, r1.y,
                        r1.x + r1.width, r1.y,
                        r1.x + r1.width, r1.y + r1.height,
                        r1.x, r1.y + r1.height
                    }));
            }
            return false;
        }

        @Override
        public void draw(ShapeRenderer renderer, float worldX, float worldY) {
            renderer.rect(worldX + offsetX, worldY + offsetY, width, height);
        }

        @Override
        public Rectangle getBounds(float worldX, float worldY) {
            return new Rectangle(worldX + offsetX, worldY + offsetY, width, height);
        }
    }

    // === Polygon shape ===
    public static class PolygonShape extends Shape {
        private Polygon polygon; // local-space polygon
        private float offsetX, offsetY;

        public PolygonShape(float[] vertices, float offsetX, float offsetY) {
            this.polygon = new Polygon(vertices);
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        public Polygon getWorldPolygon(float worldX, float worldY) {
            Polygon p = new Polygon(polygon.getVertices());
            p.setPosition(worldX + offsetX, worldY + offsetY);
            return p;
        }

        @Override
        public boolean overlaps(Shape other, float xA, float yA, float xB, float yB) {
            Polygon polyA = getWorldPolygon(xA, yA);

            if (other instanceof PolygonShape p) {
                return Intersector.overlapConvexPolygons(polyA, p.getWorldPolygon(xB, yB));
            }
            if (other instanceof RectShape r) {
                Rectangle rect = new Rectangle(xB + r.offsetX, yB + r.offsetY, r.width, r.height);
                Polygon rectPoly = new Polygon(new float[]{
                    rect.x, rect.y,
                    rect.x + rect.width, rect.y,
                    rect.x + rect.width, rect.y + rect.height,
                    rect.x, rect.y + rect.height
                });
                return Intersector.overlapConvexPolygons(polyA, rectPoly);
            }
            if (other instanceof CircleShape c) {
                Circle circle = new Circle(xB + c.offsetX, yB + c.offsetY, c.radius);
                // Approximate: check overlap against bounding box
                return Intersector.overlaps(circle, polyA.getBoundingRectangle());
            }
            return false;
        }

        @Override
        public void draw(ShapeRenderer renderer, float worldX, float worldY) {
            Polygon p = getWorldPolygon(worldX, worldY);
            float[] verts = p.getTransformedVertices();
            renderer.polygon(verts);
        }

        @Override
        public Rectangle getBounds(float worldX, float worldY) {
            return getWorldPolygon(worldX, worldY).getBoundingRectangle();
        }
    }

    // === ColliderComponent proper ===
    private final List<Shape> shapes = new ArrayList<>();

    public void addShape(Shape shape) {
        shapes.add(shape);
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public boolean isCollidingWith(ColliderComponent other, PositionComponent posA, PositionComponent posB) {
        for (Shape sA : shapes) {
            for (Shape sB : other.shapes) {
                if (sA.overlaps(sB, posA.x, posA.y, posB.x, posB.y)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Rectangle getBounds(PositionComponent pos) {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;

        for (Shape s : shapes) {
            Rectangle r = s.getBounds(pos.x, pos.y);
            minX = Math.min(minX, r.x);
            minY = Math.min(minY, r.y);
            maxX = Math.max(maxX, r.x + r.width);
            maxY = Math.max(maxY, r.y + r.height);
        }

        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    public void debugDraw(ShapeRenderer renderer, PositionComponent pos) {
        renderer.setColor(Color.RED);
        for (Shape s : shapes) {
            s.draw(renderer, pos.x, pos.y);
        }
    }
}
