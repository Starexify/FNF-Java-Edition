package com.nova.fnfjava.data.story.level;

import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.data.animation.AnimationData;

public class LevelData {
    public String name;
    public String titleAsset;
    public Array<LevelPropData> props;
    public boolean visible;
    public Array<String> songs;
    public String background;

    public LevelData() {
        this.name = "";
        this.titleAsset = "";
        this.props = new Array<>();
        this.visible = true;
        this.songs = new Array<>();
        this.songs.add("bopeebo");
        this.background = "#F9CF51";
    }

    public LevelData(String name, String titleAsset) {
        this();
        this.name = name;
        this.titleAsset = titleAsset;
    }

    public LevelData(String name, String titleAsset, Array<LevelPropData> props, boolean visible, Array<String> songs, String background) {
        this.name = name;
        this.titleAsset = titleAsset;
        this.props = props != null ? new Array<>(props) : new Array<>();
        this.visible = visible;
        this.songs = songs != null ? new Array<>(songs) : new Array<>();
        if (this.songs.isEmpty()) this.songs.add("bopeebo");
        this.background = background != null ? background : "#F9CF51";
    }

    public static class LevelPropData {
        public String assetPath;
        public float scale;
        public float alpha;
        public boolean isPixel;
        public float danceEvery;
        public Array<Float> offsets;
        public Array<AnimationData> animations;

        public LevelPropData() {
            this.assetPath = "";
            this.scale = 1.0f;
            this.alpha = 1.0f;
            this.isPixel = false;
            this.danceEvery = 1.0f;
            this.offsets = new Array<>();
            this.offsets.add(0.0f);
            this.offsets.add(0.0f);
            this.animations = new Array<>();
        }

        public LevelPropData(String assetPath) {
            this();
            this.assetPath = assetPath;
        }

        public LevelPropData(String assetPath, float scale, float alpha, boolean isPixel, float danceEvery, Array<Float> offsets, Array<AnimationData> animations) {
            this.assetPath = assetPath;
            this.scale = scale;
            this.alpha = alpha;
            this.isPixel = isPixel;
            this.danceEvery = danceEvery;
            this.offsets = offsets != null ? new Array<>(offsets) : new Array<>();
            if (this.offsets.size < 2) {
                this.offsets.clear();
                this.offsets.add(0.0f);
                this.offsets.add(0.0f);
            }
            this.animations = animations != null ? new Array<>(animations) : new Array<>();
        }
    }
}
