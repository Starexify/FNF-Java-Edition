package com.nova.fnfjava.data.song;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class SongTimeChange {
    public static final Array<Integer> DEFAULT_BEAT_TUPLETS = new Array<Integer>(new Integer[]{4, 4, 4, 4});
    public static final SongTimeChange DEFAULT_SONGTIMECHANGE = new Builder(0, 100).build();
    public static final Array<SongTimeChange> DEFAULT_SONGTIMECHANGES = new Array<SongTimeChange>(new SongTimeChange[]{DEFAULT_SONGTIMECHANGE});

    public float timeStamp;
    public Float beatTime;
    public float bpm;
    public int timeSignatureNum;
    public int timeSignatureDen;
    public Array<Integer> beatTuplets;


    public SongTimeChange(Builder builder) {
        this.timeStamp = builder.timeStamp;
        this.bpm = builder.bpm;

        this.timeSignatureNum = builder.timeSignatureNum;
        this.timeSignatureDen = builder.timeSignatureDen;

        this.beatTime = builder.beatTime;
        this.beatTuplets = new Array<Integer>(builder.beatTuplets);
    }

    public static class Builder {
        public float timeStamp;
        public float bpm;
        public Float beatTime = null;
        public int timeSignatureNum = 4;
        public int timeSignatureDen = 4;
        public Array<Integer> beatTuplets = new Array<Integer>(DEFAULT_BEAT_TUPLETS);

        public Builder(float timeStamp, float bpm) {
            this.timeStamp = timeStamp;
            this.bpm = bpm;
        }

        public SongTimeChange build() {
            return new SongTimeChange(this);
        }
    }

    public void toJson(Json json) {
        json.writeObjectStart();
        json.writeValue("t", timeStamp);
        json.writeValue("bpm", bpm);
        json.writeValue("n", timeSignatureNum);
        json.writeValue("d", timeSignatureDen);
        if (beatTime != null) json.writeValue("b", beatTime);
        if (beatTuplets != null) json.writeValue("bt", beatTuplets);
        json.writeObjectEnd();
    }

    public static SongTimeChange fromJson(JsonValue jsonData) {
        float timeStamp = jsonData.getFloat("t", 0);
        float bpm = jsonData.getFloat("bpm", 100);
        int n = jsonData.getInt("n", 4);
        int d = jsonData.getInt("d", 4);

        Builder builder = new Builder(timeStamp, bpm);
        builder.timeSignatureNum = n;
        builder.timeSignatureDen = d;

        if (jsonData.has("b")) builder.beatTime = jsonData.getFloat("b");

        if (jsonData.has("bt")) {
            JsonValue btValue = jsonData.get("bt");
            if (btValue.isArray()) {
                Array<Integer> tuplets = new Array<>();
                for (JsonValue item : btValue) {
                    tuplets.add(item.asInt());
                }
                builder.beatTuplets = tuplets;
            }
        }

        return builder.build();
    }
}
