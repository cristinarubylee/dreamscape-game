package com.badlogic.dream.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Static utility class for managing game assets
 */
public class Media {
    public static AssetManager manager;
    private static ObjectMap<String, ObjectMap<String, String>> assetMap;
    private static JsonValue root;

    public static void loadBootstrapAssets() {
        manager = new AssetManager();

        Json json = new Json();
        root = json.fromJson(null, Gdx.files.internal("assets/bootstrap_assets.json"));

        loadCategories();
    }

    public static void loadGameAssets() {
        // Manager should not be null assuming we load bootstrap first, but just in case!
        if (manager == null) manager = new AssetManager();

        Json json = new Json();
        root = json.fromJson(null, Gdx.files.internal("assets/game_assets.json"));

        loadCategories();
    }

    private static void loadCategories() {
        loadCategory("textures", Texture.class);
        loadCategory("sounds", Sound.class);
        loadCategory("music", Music.class);
    }

    private static <T> void loadCategory(String category, Class<T> type){

        if(root.get(category) == null ){
            System.out.println("Missing category" + category);
            return;
        }

        JsonValue entries = root.get(category);

        for(JsonValue entry : entries) {
            String path = "assets/" + entry.asString();
            if (type == Texture.class) {
                // create a parameter with linear filtering
                TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
                param.minFilter = Texture.TextureFilter.Linear;
                param.magFilter = Texture.TextureFilter.Linear;
                manager.load(path, Texture.class, param);
            } else {
                manager.load(path, type); // default for sounds/music
            }
        }
    }

    public static void finishLoading() {
        manager.finishLoading();
    }

    /**
     *
     * @param name the alias of the asset to retrieve
     * @param <T> the type of the asset (e.g., Texture, Sound, Music)
     * @return the loaded asset instance
     * @throws RuntimeException if no asset is found for the given alias and type
     */
    public static <T> T get(String name, Class<T> type) {
        String path = findPath(name);
        if (path == null) throw new RuntimeException("No " + type + " found for key: " + name);
        return manager.get(path, type);
    }

    public static String findPath(String key) {
        if (root == null) return null;

        for (JsonValue category : root) {
            for (JsonValue entry : category) {
                if (entry.name().equals((key))) {
                    return "assets/" + entry.asString();
                }
            }
        }
        return null;
    }

    public static void dispose() {
        manager.dispose();
    }
}
