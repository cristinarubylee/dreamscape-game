package com.badlogic.dream.entities;

import com.badlogic.dream.components.*;
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
        player.add(new ControlComponent());

        ColliderComponent collider = new ColliderComponent();

        // Capsule dimensions
        float radius = 0.5f;         // semicircle radius
        float height = 2f;           // full capsule height
        float rectHeight = height - 2 * radius; // middle rectangle height

        // Add middle rectangle
        collider.addShape(new ColliderComponent.RectShape(
            radius * 2,   // width = diameter
            rectHeight,   // height
            -radius, -rectHeight/2          // offset at capsule center
        ));

        // Add top semicircle
        collider.addShape(new ColliderComponent.CircleShape(
            radius,
            0,
            rectHeight / 2
        ));

        // Add bottom semicircle
        collider.addShape(new ColliderComponent.CircleShape(
            radius,
            0,
            -rectHeight / 2
        ));

        player.add(collider);
        return player;
    }


    public static Entity createEnemy(float x, float y) {
        Entity player = new Entity();
        TextureRegion idle = new TextureRegion(Media.get("idle", Texture.class));
        player.add(new PositionComponent(x, y));
        player.add(new RenderableComponent(idle));

        ColliderComponent collider = new ColliderComponent();
        collider.addShape(new ColliderComponent.CircleShape(2, 0, 0));
        player.add(collider);
        return player;
    }

    public static Entity createBullet(float x, float y) {
        Entity bullet = new Entity();
        TextureRegion tex = new TextureRegion(Media.get("drop", Texture.class));
        bullet.add(new PositionComponent(x, y));
        bullet.add(new RenderableComponent(tex));
        bullet.add(new VelocityComponent(1, 0));
        return bullet;
    }
}
