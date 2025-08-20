package com.badlogic.dream.components;

import com.badlogic.ashley.core.Component;

public class CollisionComponent implements Component {
    public enum Type {
        PLAYER, ENEMY, BULLET, WALL
    }

    public Type type;

    public CollisionComponent(Type type) {
        this.type = type;
    }
}
