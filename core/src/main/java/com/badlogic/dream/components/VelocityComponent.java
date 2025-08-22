package com.badlogic.dream.components;

import com.badlogic.ashley.core.Component;

public class VelocityComponent implements Component {
    public float x = 0f; // horizontal speed (units per second)
    public float y = 0f; // vertical speed (units per second)

    public VelocityComponent(float x, float y){
        this.x = x;
        this.y = y;
    }
}
