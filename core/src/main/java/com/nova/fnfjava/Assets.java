package com.nova.fnfjava;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Assets {
    public static Texture getTexture(String path) {
        if (!isPathLoaded(path)) {
            Main.assetManager.load(path, Texture.class);
            Main.assetManager.finishLoading(); // Only if you need synchronous loading
        }
        return Main.assetManager.get(path, Texture.class);
    }

    public static TextureAtlas getAtlas(String path) {
        if (!isPathLoaded(path)) {
            Main.assetManager.load(path, TextureAtlas.class);
            Main.assetManager.finishLoading(); // Only if you need synchronous loading
        }
        return Main.assetManager.get(path, TextureAtlas.class);
    }

    public static void loadAtlas(String path) {
        Main.assetManager.load(path, TextureAtlas.class);
    }

    public static void loadTexture(String path) {
        Main.assetManager.load(path, Texture.class);
    }

    public static boolean isPathLoaded(String path) {
        return Main.assetManager.isLoaded(path);
    }

    public static String getText(String path) {
        return Gdx.files.internal(path).readString();
    }

    public static boolean update() {
        return Main.assetManager.update();
    }
}
