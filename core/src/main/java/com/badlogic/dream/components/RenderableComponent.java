package com.badlogic.dream.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class RenderableComponent implements Component {
    public TextureRegion region;

    public RenderableComponent(TextureRegion region){
        this.region = region;
    }

}
