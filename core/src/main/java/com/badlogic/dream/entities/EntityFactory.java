package com.badlogic.dream.entities;

import com.badlogic.dream.components.PositionComponent;
import com.badlogic.dream.components.RenderableComponent;
import com.badlogic.dream.util.Media;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.ashley.core.Entity;

public class EntityFactory {
    public static Entity createPlayer(float x, float y) {
        Entity player = new Entity();
        TextureRegion idle = new TextureRegion(Media.get("idle", Texture.class));
        player.add(new PositionComponent(x, y));
        player.add(new RenderableComponent(idle));
        return player;
    }
}
