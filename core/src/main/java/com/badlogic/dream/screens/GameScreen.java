package com.badlogic.dream.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.dream.GDXRoot;
import com.badlogic.dream.entities.EntityFactory;
import com.badlogic.dream.systems.CollisionSystem;
import com.badlogic.dream.systems.ControlSystem;
import com.badlogic.dream.systems.RenderSystem;
import com.badlogic.dream.util.Media;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
    private final GDXRoot game;

    private Texture backgroundTexture;
    private Entity player;
    private Engine engine;

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;

    // Define your virtual world size (aspect ratio is kept by FitViewport)
    private static final float WORLD_WIDTH = 16f;
    private static final float WORLD_HEIGHT = 10f;

    public GameScreen(GDXRoot game) {
        this.game = game;
        this.engine = new Engine();

        // camera + viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        batch = game.batch;

        backgroundTexture = Media.get("background", Texture.class);

        // create player entity and add it
        player = EntityFactory.createPlayer(2, 5);
        engine.addEntity(player);

        engine.addEntity(EntityFactory.createEnemy(10, 5));

        // add systems
        engine.addSystem(new RenderSystem(game.batch, 120, false));
        engine.addSystem(new ControlSystem());
        engine.addSystem(new CollisionSystem(WORLD_WIDTH, WORLD_HEIGHT, 1));
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        viewport.apply();
        camera.update();

        batch.setProjectionMatrix(camera.combined);

        // clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // draw background
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.end();

        // let Ashley render entities
        engine.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() { }
}
