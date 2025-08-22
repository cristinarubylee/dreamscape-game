package com.badlogic.dream.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.dream.components.ControlComponent;
import com.badlogic.dream.components.PositionComponent;
import com.badlogic.dream.entities.EntityFactory;

public class ShootingSystem extends IteratingSystem {
    private ComponentMapper<ControlComponent> cm = ComponentMapper.getFor(ControlComponent.class);
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    public ShootingSystem() {
        super(Family.all(ControlComponent.class, PositionComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ControlComponent control = cm.get(entity);
        if (control.isShooting) {
            PositionComponent pos = pm.get(entity);
            getEngine().addEntity(EntityFactory.createBullet(pos.x, pos.y));
        }
    }
}
