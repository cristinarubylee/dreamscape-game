package com.badlogic.dream.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.dream.components.ControlComponent;
import com.badlogic.dream.components.PositionComponent;
import com.badlogic.dream.entities.EntityFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class ControlSystem extends IteratingSystem {
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<ControlComponent> cm = ComponentMapper.getFor(ControlComponent.class);

    public ControlSystem() {
        super(com.badlogic.ashley.core.Family.all(PositionComponent.class, ControlComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        float speed = deltaTime * 5;
        PositionComponent position = pm.get(entity);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  position.x -= speed;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) position.x += speed;
        if (Gdx.input.isKeyPressed(Input.Keys.UP))    position.y += speed;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  position.y -=speed;

        ControlComponent control = cm.get(entity);
        control.isShooting = Gdx.input.isKeyPressed(Input.Keys.SPACE);
    }
}
