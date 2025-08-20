package com.badlogic.dream.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.dream.components.ColliderComponent;
import com.badlogic.dream.components.PositionComponent;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CollisionSystem extends EntitySystem {
    private float worldWidth;
    private float worldHeight;
    private int cellSize;
    private int gridWidth;
    private int gridHeight;

    private Array<Entity>[][] grid;
    private ComponentMapper<ColliderComponent> colliderMapper;
    private ComponentMapper<PositionComponent> positionMapper;

    private Set<EntityPair> checkedPairs;
    private ImmutableArray<Entity> entities; // entities with Position + Collider

    public CollisionSystem(float worldWidth, float worldHeight, int cellSize) {
        super();

        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.cellSize = cellSize;
        this.gridWidth = (int) (worldWidth / cellSize) + 1;
        this.gridHeight = (int) (worldHeight / cellSize) + 1;

        // Create 2D grid
        grid = new Array[gridWidth][gridHeight];
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                grid[x][y] = new Array<>();
            }
        }

        this.checkedPairs = new HashSet<>();
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent.class, ColliderComponent.class).get());

        colliderMapper = ComponentMapper.getFor(ColliderComponent.class);
        positionMapper = ComponentMapper.getFor(PositionComponent.class);
    }

    @Override
    public void update(float deltaTime) {
        clearGrid();
        checkedPairs.clear();

        populateGrid();
        checkCollisions();
    }

    private void clearGrid() {
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                grid[x][y].clear();
            }
        }
    }

    private void populateGrid() {
        for (Entity entity : entities) {
            PositionComponent position = positionMapper.get(entity);
            ColliderComponent collider = colliderMapper.get(entity);

            if (collider == null) continue;

            // Calculate collider bounds
            Rectangle bounds = getColliderBounds(position, collider);

            // Insert entity into grid cells
            insertIntoGrid(entity, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    private Rectangle getColliderBounds(PositionComponent position, ColliderComponent collider) {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;

        for (ColliderComponent.Shape shape : collider.getShapes()) {
            Rectangle bounds = shape.getBounds(position.x, position.y);
            minX = Math.min(minX, bounds.x);
            minY = Math.min(minY, bounds.y);
            maxX = Math.max(maxX, bounds.x + bounds.width);
            maxY = Math.max(maxY, bounds.y + bounds.height);
        }

        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    private void insertIntoGrid(Entity entity, float x, float y, float width, float height) {
        int minCellX = Math.max(0, (int) (x / cellSize));
        int maxCellX = Math.min(gridWidth - 1, (int) ((x + width) / cellSize));
        int minCellY = Math.max(0, (int) (y / cellSize));
        int maxCellY = Math.min(gridHeight - 1, (int) ((y + height) / cellSize));

        for (int cellX = minCellX; cellX <= maxCellX; cellX++) {
            for (int cellY = minCellY; cellY <= maxCellY; cellY++) {
                grid[cellX][cellY].add(entity);
            }
        }
    }

    private void checkCollisions() {
        for (Entity entity : entities) {
            PositionComponent position = positionMapper.get(entity);
            ColliderComponent collider = colliderMapper.get(entity);

            if (collider == null) continue;

            // Get nearby entities from grid
            Set<Entity> nearbyEntities = getNearbyEntities(entity, position, collider);

            for (Entity other : nearbyEntities) {
                if (entity == other) continue;

                EntityPair pair = new EntityPair(entity, other);
                if (checkedPairs.contains(pair)) continue;
                checkedPairs.add(pair);

                checkEntityCollision(entity, other);
            }
        }
    }

    private Set<Entity> getNearbyEntities(Entity entity,
                                          PositionComponent position,
                                          ColliderComponent collider) {
        Set<Entity> nearbyEntities = new HashSet<>();

        // Compute the bounding box for this entity
        Rectangle bounds = getColliderBounds(position, collider);

        // Determine which grid cells the bounding box spans
        int minCellX = Math.max(0, (int)(bounds.x / cellSize));
        int maxCellX = Math.min(gridWidth - 1, (int)((bounds.x + bounds.width) / cellSize));
        int minCellY = Math.max(0, (int)(bounds.y / cellSize));
        int maxCellY = Math.min(gridHeight - 1, (int)((bounds.y + bounds.height) / cellSize));

        // Loop through all relevant cells
        for (int cellX = minCellX; cellX <= maxCellX; cellX++) {
            for (int cellY = minCellY; cellY <= maxCellY; cellY++) {
                for (Entity e : grid[cellX][cellY]) {
                    if (e != entity) {
                        nearbyEntities.add(e);
                    }
                }
            }
        }

        return nearbyEntities;
    }

    private void checkEntityCollision(Entity entityA, Entity entityB) {
        PositionComponent posA = positionMapper.get(entityA);
        PositionComponent posB = positionMapper.get(entityB);

        ColliderComponent colliderA = colliderMapper.get(entityA);
        ColliderComponent colliderB = colliderMapper.get(entityB);

        if (colliderA == null || colliderB == null) return;

        if (colliderA.isCollidingWith(colliderB, posA, posB)) {
            handleCollision(entityA, entityB, colliderA, colliderB);
        }
    }

    private void handleCollision(Entity entityA, Entity entityB,
                                 ColliderComponent colliderA, ColliderComponent colliderB) {
        System.out.println("COLLISION!!!");
    }

    /** Private helper class for entity pairs */
    private static class EntityPair {
        private final Entity entityA, entityB;
        private final int hashCode;

        public EntityPair(Entity entityA, Entity entityB) {
            if (entityA.hashCode() < entityB.hashCode()) {
                this.entityA = entityA;
                this.entityB = entityB;
            } else {
                this.entityA = entityB;
                this.entityB = entityA;
            }
            this.hashCode = Objects.hash(this.entityA, this.entityB);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            EntityPair that = (EntityPair) obj;
            return Objects.equals(entityA, that.entityA) &&
                Objects.equals(entityB, that.entityB);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }
}
