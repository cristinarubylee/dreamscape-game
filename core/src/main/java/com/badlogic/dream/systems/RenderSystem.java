package com.badlogic.dream.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.dream.components.PositionComponent;
import com.badlogic.dream.components.RenderableComponent;
import com.badlogic.dream.components.ColliderComponent;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class RenderSystem extends IteratingSystem {
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<RenderableComponent> tm = ComponentMapper.getFor(RenderableComponent.class);
    private ComponentMapper<ColliderComponent> cm = ComponentMapper.getFor(ColliderComponent.class);

    private SpriteBatch batch;
    private float pixelsPerWorldUnit;
    private ShapeRenderer shapeRenderer;
    private boolean debug;

    public RenderSystem(SpriteBatch batch, float pixelsPerWorldUnit, boolean debug) {
        super(com.badlogic.ashley.core.Family.all(PositionComponent.class, RenderableComponent.class).get());
        this.batch = batch;
        this.pixelsPerWorldUnit = pixelsPerWorldUnit;
        this.shapeRenderer = new ShapeRenderer();
        this.debug = debug;
    }

    @Override
    public void update(float deltaTime) {
        // normal texture rendering
        batch.begin();
        super.update(deltaTime);
        batch.end();

        if (!debug) return;
        // after textures, draw collider debug outlines (so they appear on top)
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (Entity entity : getEntities()) {
            ColliderComponent collider = cm.get(entity);
            PositionComponent pos = pm.get(entity);

            if (collider != null) {
                shapeRenderer.setColor(Color.RED);
                collider.debugDraw(shapeRenderer, pos);
            }
        }

        shapeRenderer.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent pos = pm.get(entity);
        RenderableComponent tex = tm.get(entity);
        ColliderComponent collider = cm.get(entity);

        TextureRegion region = tex.region;
        if (region != null) {
            float width  = region.getRegionWidth()/pixelsPerWorldUnit;
            float height = region.getRegionHeight()/pixelsPerWorldUnit;

            float drawX = pos.x - width/2f;
            float drawY = pos.y - height/2f;

            batch.draw(region, drawX, drawY, width, height);
        }
    }

}
