package com.badlogic.dream.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.dream.components.PositionComponent;
import com.badlogic.dream.components.RenderableComponent;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RenderSystem extends IteratingSystem {
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<RenderableComponent> tm = ComponentMapper.getFor(RenderableComponent.class);
    private SpriteBatch batch;

    public RenderSystem(SpriteBatch batch) {
        super(com.badlogic.ashley.core.Family.all(PositionComponent.class, RenderableComponent.class).get());
        this.batch = batch;
    }

    @Override
    public void update(float deltaTime) {
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent pos = pm.get(entity);
        RenderableComponent tex = tm.get(entity);

        if (tex.region != null) {
            batch.draw(tex.region, pos.x, pos.y, 1, 1.36f);
        }
    }
}
