package com.badlogic.dream.entities;

import com.badlogic.dream.util.Media;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class EntityFactory {
    public static Player createPlayer(float x, float y) {
        TextureRegion idle = new TextureRegion(Media.get("idle", Texture.class));
        return new Player(x, y, idle);
    }
}
