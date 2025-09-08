package com.nova.fnfjava.data.animation;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class UnnamedAnimationData implements Json.Serializable {
    public String prefix;
    public String assetPath = null;
    public float[] offsets = {0f, 0f};
    public Boolean looped = false;
    public Boolean flipX = false;
    public Boolean flipY = false;
    public Integer frameRate = 24;
    public Integer[] frameIndices = null;

    public UnnamedAnimationData() {}

    public UnnamedAnimationData(String prefix, int frameRate, boolean looped) {
        this.prefix = prefix;
        this.frameRate = frameRate;
        this.looped = looped;
    }

    @Override
    public void write(Json json) {
        json.writeValue("prefix", prefix);
        if (assetPath != null) json.writeValue("assetPath", assetPath);
        if (offsets[0] != 0f || offsets[1] != 0f) json.writeValue("offsets", offsets);
        if (looped != false) json.writeValue("looped", looped);
        if (flipX != false) json.writeValue("flipX", flipX);
        if (flipY != false) json.writeValue("flipY", flipY);
        if (frameRate != 24) json.writeValue("frameRate", frameRate);
        if (frameIndices != null) json.writeValue("frameIndices", frameIndices);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        prefix = jsonData.getString("prefix", "");
        assetPath = jsonData.getString("assetPath", null);

        JsonValue offsetsData = jsonData.get("offsets");
        if (offsetsData != null && offsetsData.isArray()) offsets = json.readValue(float[].class, offsetsData);
        else offsets = new float[]{0f, 0f};

        looped = jsonData.getBoolean("looped", false);
        flipX = jsonData.getBoolean("flipX", false);
        flipY = jsonData.getBoolean("flipY", false);
        frameRate = jsonData.getInt("frameRate", 24);

        JsonValue frameIndicesData = jsonData.get("frameIndices");
        if (frameIndicesData != null) frameIndices = json.readValue(Integer[].class, frameIndicesData);
    }
}
