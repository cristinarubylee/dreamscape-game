package com.badlogic.dream.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class CollisionComponent implements Component {
    public enum Type {
        PLAYER, ENEMY, BULLET, WALL
    }

    public Type type;

    public CollisionComponent(Entity other) {

    }
}
