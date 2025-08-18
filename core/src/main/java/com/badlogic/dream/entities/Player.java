package com.badlogic.dream.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.dream.components.PositionComponent;
import com.badlogic.dream.components.RenderableComponent;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player extends Entity {
    public PositionComponent position;
    public RenderableComponent renderable;

    public Player(float x, float y){
        position = new PositionComponent(x, y);
        Texture texture = new Texture("assets/idle.png");
        renderable = new RenderableComponent(new TextureRegion(texture));
    }
}
