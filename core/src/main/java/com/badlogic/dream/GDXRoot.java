package com.badlogic.dream;

import com.badlogic.dream.screens.LoadingScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

public class GDXRoot extends Game {
    public SpriteBatch batch;
    public Viewport viewport;

    @Override
    public void create () {
        batch = new SpriteBatch();
        viewport = new FitViewport(16, 10);

        this.setScreen(new LoadingScreen(this));
    }

    @Override
    public void render () {
        super.render();
//        float speed = 200 * Gdx.graphics.getDeltaTime();
//        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  player.position.adjustX(-speed);
//        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) player.position.adjustX(speed);
//        if (Gdx.input.isKeyPressed(Input.Keys.UP))    player.position.adjustY(speed);
//        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  player.position.adjustY(-speed);
//
//        Gdx.gl.glClearColor(0, 0, 0, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//        batch.begin();
//        batch.draw(player.renderable.region,
//            player.position.getX(),
//            player.position.getY());
//        batch.end();
    }

    @Override
    public void dispose () {
        batch.dispose();
    }
}

