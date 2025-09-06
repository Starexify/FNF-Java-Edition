package com.nova.fnfjava;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

import java.util.concurrent.ConcurrentHashMap;

public class Assets {
    public static final ConcurrentHashMap<String, Texture> generatedTextures = new ConcurrentHashMap<>();


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

    public static boolean exists(String path) {
        return Gdx.files.internal(path).exists();
    }

    public static boolean isPathLoaded(String path) {
        return Main.assetManager.isLoaded(path);
    }

    public static String getText(String path) {
        return Gdx.files.internal(path).readString();
    }

    /**
     * Creates or retrieves a flat colored rectangular texture, similar to FlxG.bitmap.create
     * @param width Width of the texture
     * @param height Height of the texture
     * @param color Color of the rectangle
     * @param unique Whether to create a unique instance (bypass cache)
     * @param key Optional cache key. If null, auto-generated from parameters
     * @return The created or cached texture
     */
    public static Texture createColoredTexture(int width, int height, Color color, boolean unique, String key) {
        // Generate cache key if not provided
        if (key == null) key = "colored_" + width + "x" + height + "_" + Float.floatToIntBits(color.r) +
                "_" + Float.floatToIntBits(color.g) + "_" + Float.floatToIntBits(color.b) +
                "_" + Float.floatToIntBits(color.a);

        // If unique is requested, modify key to ensure uniqueness
        if (unique) key = key + "_unique_" + System.nanoTime();

        // Check if we already have this texture cached
        if (generatedTextures.containsKey(key)) return generatedTextures.get(key);

        // Create new texture
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        // Cache the texture (even unique ones, since they have unique keys)
        generatedTextures.put(key, texture);

        return texture;
    }

    public static Texture createColoredTexture(int width, int height, Color color) {
        return createColoredTexture(width, height, color, false, null);
    }

    public static Array<String> listFilesInDirectory(String directoryPath) {
        Array<String> fileList = new Array<>();
        FileHandle directory = Gdx.files.internal(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            addFilesRecursively(directory, fileList);
        }

        return fileList;
    }

    public static void addFilesRecursively(FileHandle directory, Array<String> fileList) {
        for (FileHandle file : directory.list()) {
            if (file.isDirectory()) {
                addFilesRecursively(file, fileList);
            } else {
                fileList.add(file.path());
            }
        }
    }

    public static void disposeGeneratedTextures() {
        for (Texture texture : generatedTextures.values()) texture.dispose();
        generatedTextures.clear();
    }

    public static void dispose() {
        disposeGeneratedTextures();
    }
}
