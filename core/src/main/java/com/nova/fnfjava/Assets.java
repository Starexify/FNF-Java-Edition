package com.nova.fnfjava;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.play.Countdown;
import com.nova.fnfjava.play.notes.NoteDirection;
import com.nova.fnfjava.play.notes.notestyle.NoteStyle;
import games.rednblack.miniaudio.MASound;

import java.util.concurrent.ConcurrentHashMap;

public class Assets {
    public static final ConcurrentHashMap<String, Texture> generatedTextures = new ConcurrentHashMap<>();

    public static MASound getSound(String path) {
        if (!isPathLoaded(path)) {
            Main.assetManager.load(path, MASound.class);
            Main.assetManager.finishLoading(); // Only if you need synchronous loading
        }
        return Main.assetManager.get(path, MASound.class);
    }

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

    /**
     * Ensures a texture with the given key is cached.
     * @param key The key of the texture to cache.
     */
    public static void cacheTexture(String key) {
        if (isPathLoaded(key)) return;

        try {
            if (!exists(key)) {
                Main.logger.setTag("Assets").error("Failed to cache texture (file not found): " + key);
                return;
            }

            Main.assetManager.load(key, Texture.class);
            Main.assetManager.finishLoading();

            Main.logger.setTag("Assets").info("Successfully cached texture: " + key);

        } catch (Exception e) {
            Main.logger.setTag("Assets").error("Failed to cache texture: " + key, e);
        }
    }

    public static void cacheSound(String key) {
        if (isPathLoaded(key)) return;

        try {
            if (!exists(key)) {
                Main.logger.setTag("Assets").error("Failed to cache sound (file not found): " + key);
                return;
            }

            Main.assetManager.load(key, MASound.class);
            Main.assetManager.finishLoading();

            Main.logger.setTag("Assets").info("Successfully cached sound: " + key);

        } catch (Exception e) {
            Main.logger.setTag("Assets").error("Failed to cache sound: " + key, e);
        }
    }

    public static void cacheNoteStyle(NoteStyle style) {
        cacheTexture(Paths.image(style.getNoteAssetPath() != null ? style.getNoteAssetPath() : "note.png"));
        cacheTexture(style.getHoldNoteAssetPath() != null ? style.getHoldNoteAssetPath() : "noteHold.png");
        cacheTexture(Paths.image(style.getStrumlineAssetPath() != null ? style.getStrumlineAssetPath() : "strumline.png"));
        cacheTexture(Paths.image(style.getSplashAssetPath() != null ? style.getSplashAssetPath() : "noteSplash.png"));

        cacheTexture(Paths.image(style.getHoldCoverDirectionAssetPath(NoteDirection.LEFT) != null ? style.getHoldCoverDirectionAssetPath(NoteDirection.LEFT) : "LEFT"));
        cacheTexture(Paths.image(style.getHoldCoverDirectionAssetPath(NoteDirection.RIGHT) != null ? style.getHoldCoverDirectionAssetPath(NoteDirection.RIGHT) : "RIGHT"));
        cacheTexture(Paths.image(style.getHoldCoverDirectionAssetPath(NoteDirection.UP) != null ? style.getHoldCoverDirectionAssetPath(NoteDirection.UP) : "UP"));
        cacheTexture(Paths.image(style.getHoldCoverDirectionAssetPath(NoteDirection.DOWN) != null ? style.getHoldCoverDirectionAssetPath(NoteDirection.DOWN) : "DOWN"));

        // cacheTexture(Paths.image(style.buildCountdownSpritePath(THREE) ?? "THREE"));
        cacheTexture(Paths.image(style.buildCountdownSpritePath(Countdown.CountdownStep.TWO) != null ? style.buildCountdownSpritePath(Countdown.CountdownStep.TWO) : "TWO"));
        cacheTexture(Paths.image(style.buildCountdownSpritePath(Countdown.CountdownStep.ONE) != null ? style.buildCountdownSpritePath(Countdown.CountdownStep.ONE) : "ONE"));
        cacheTexture(Paths.image(style.buildCountdownSpritePath(Countdown.CountdownStep.GO) != null ? style.buildCountdownSpritePath(Countdown.CountdownStep.GO) : "GO"));

        cacheSound(style.getCountdownSoundPath(Countdown.CountdownStep.THREE) != null ? style.getCountdownSoundPath(Countdown.CountdownStep.THREE) : "THREE");
        cacheSound(style.getCountdownSoundPath(Countdown.CountdownStep.TWO) != null ? style.getCountdownSoundPath(Countdown.CountdownStep.TWO) : "TWO");
        cacheSound(style.getCountdownSoundPath(Countdown.CountdownStep.ONE) != null ? style.getCountdownSoundPath(Countdown.CountdownStep.ONE) : "ONE");
        cacheSound(style.getCountdownSoundPath(Countdown.CountdownStep.GO) != null ? style.getCountdownSoundPath(Countdown.CountdownStep.GO) : "GO");

        cacheTexture(Paths.image(style.buildJudgementSpritePath("sick") != null ? style.buildJudgementSpritePath("sick") : "sick"));
        cacheTexture(Paths.image(style.buildJudgementSpritePath("good") != null ? style.buildJudgementSpritePath("good") : "good"));
        cacheTexture(Paths.image(style.buildJudgementSpritePath("bad") != null ? style.buildJudgementSpritePath("bad") : "bad"));
        cacheTexture(Paths.image(style.buildJudgementSpritePath("shit") != null ? style.buildJudgementSpritePath("shit") : "shit"));

        cacheTexture(Paths.image(style.buildComboNumSpritePath(0) != null ? style.buildComboNumSpritePath(0) : "0"));
        cacheTexture(Paths.image(style.buildComboNumSpritePath(1) != null ? style.buildComboNumSpritePath(1) : "1"));
        cacheTexture(Paths.image(style.buildComboNumSpritePath(2) != null ? style.buildComboNumSpritePath(2) : "2"));
        cacheTexture(Paths.image(style.buildComboNumSpritePath(3) != null ? style.buildComboNumSpritePath(3) : "3"));
        cacheTexture(Paths.image(style.buildComboNumSpritePath(4) != null ? style.buildComboNumSpritePath(4) : "4"));
        cacheTexture(Paths.image(style.buildComboNumSpritePath(5) != null ? style.buildComboNumSpritePath(5) : "5"));
        cacheTexture(Paths.image(style.buildComboNumSpritePath(6) != null ? style.buildComboNumSpritePath(6) : "6"));
        cacheTexture(Paths.image(style.buildComboNumSpritePath(7) != null ? style.buildComboNumSpritePath(7) : "7"));
        cacheTexture(Paths.image(style.buildComboNumSpritePath(8) != null ? style.buildComboNumSpritePath(8) : "8"));
        cacheTexture(Paths.image(style.buildComboNumSpritePath(9) != null ? style.buildComboNumSpritePath(9) : "9"));
    }


    public static boolean isPathLoaded(String path) {
        return Main.assetManager.isLoaded(path);
    }

    public static boolean exists(String path) {
        return Gdx.files.internal(path).exists();
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
