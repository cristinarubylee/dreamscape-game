package com.badlogic.dream.screens;

import com.badlogic.dream.GDXRoot;
import com.badlogic.dream.entities.EntityFactory;
import com.badlogic.dream.entities.Player;
import com.badlogic.dream.util.Media;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

public class GameScreen implements Screen {
    private final GDXRoot game;

    private Texture backgroundTexture;
    private Player player;

    public GameScreen (GDXRoot game){
        this.game = game;
        backgroundTexture = Media.get("background", Texture.class);
        player = EntityFactory.createPlayer(2, 5);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        float speed = 5 * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  player.position.adjustX(-speed);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) player.position.adjustX(speed);
        if (Gdx.input.isKeyPressed(Input.Keys.UP))    player.position.adjustY(speed);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  player.position.adjustY(-speed);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(backgroundTexture, 0 ,0, 16, 10);
        game.batch.draw(player.renderable.region,
            player.position.getX(),
            player.position.getY(), 1, 1.36f);
        game.batch.end();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
