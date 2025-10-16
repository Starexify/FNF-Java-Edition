package com.nova.fnfjava.flxanimate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.nova.fnfjava.Assets;
import com.nova.fnfjava.Main;

public class AnimateAtlas {
    public static final Json parser = new Json();

    public String path;
    public Array<SpritemapInput> spritemaps;

    public AnimateAtlas() {
        setupParser();
    }

    public void setupParser() {
        parser.setIgnoreUnknownFields(true);
    }

    public static AnimateAtlas fromAnimate(String path) {
        boolean hasAnimation = Assets.exists(path + "/Animation.json");
        if (!hasAnimation) {
            Main.logger.setTag("AnimateAtlas").warn("No Animation.json file was found for path " + path + ".");
            return null;
        }

        FileHandle animation = Gdx.files.internal(path + "/Animation.json");
        FileHandle pathToSpritemap = Gdx.files.internal(path + "/spritemap1.json");
        String jsonContent = pathToSpritemap.readString("UTF-8");

        if (jsonContent.startsWith("\uFEFF")) jsonContent = jsonContent.substring(1);
        SpritemapData smData = parser.fromJson(SpritemapData.class, jsonContent);
        AnimateData animData = parser.fromJson(AnimateData.class, animation);

        Array<SpritemapInput> spritemaps = new Array<>();
        for (String file : Assets.listFilesInDirectory(path)) {
            if (file.contains("spritemap") && file.endsWith(".json")) spritemaps.add(new SpritemapInput(file, file));
        }

        AnimateAtlas frames = new AnimateAtlas();
        frames.spritemaps = spritemaps;

        return frames;
    }

    public record SpritemapInput(String graphic, String json) {
    }
}
