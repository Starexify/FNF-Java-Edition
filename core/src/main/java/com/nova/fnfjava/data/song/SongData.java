package com.nova.fnfjava.data.song;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.nova.fnfjava.Conductor;
import com.nova.fnfjava.util.Constants;

import java.util.Objects;

public class SongData {
    public static class SongMetadata {
        // Required
        public String songName = "Unknown";
        public String artist = "Unknown";
        public SongPlayData playData = new SongPlayData();

        // Optional
        public String charter = null;
        public Integer divisions = 96;
        public Boolean looped = false;
        public SongOffsets offsets;
        public String generatedBy = SongRegistry.DEFAULT_GENERATEDBY;
        public SongTimeFormat timeFormat = SongTimeFormat.MILLISECONDS;
        public Array<SongTimeChange> timeChanges = new Array<>(new SongTimeChange[]{new SongTimeChange(0, 100)});
        public transient String variation;

        public SongMetadata() {}

        public SongMetadata(String songName, String artist, String charter, String variation) {
            this.songName = songName != null ? songName : "Unknown";
            this.artist = artist != null ? artist : "Unknown";
            this.charter = charter;
            this.timeFormat = SongTimeFormat.MILLISECONDS;
            this.divisions = 96;
            this.looped = false;
            this.playData = new SongPlayData();
            this.timeChanges = new Array<>(new SongTimeChange[]{new SongTimeChange(0, 100)});
            this.generatedBy = SongRegistry.DEFAULT_GENERATEDBY;
            this.variation = variation != null ? variation : Constants.DEFAULT_VARIATION;
        }

        @Override
        public String toString() {
            return "SongMetadata{" +
                "songName='" + songName + '\'' +
                ", artist='" + artist + '\'' +
                ", playData=" + playData +
                ", charter='" + charter + '\'' +
                ", divisions=" + divisions +
                ", looped=" + looped +
                ", offsets=" + offsets +
                ", generatedBy='" + generatedBy + '\'' +
                ", timeFormat=" + timeFormat +
                ", timeChanges=" + timeChanges +
                ", variation='" + variation + '\'' +
                '}';
        }
    }

    public enum SongTimeFormat {
        TICKS("ticks"),
        FLOAT("float"),
        MILLISECONDS("ms");

        public final String id;

        SongTimeFormat(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public static SongTimeFormat fromString(String id) {
            for (SongTimeFormat format : values()) if (format.getId().equals(id)) return format;
            return MILLISECONDS;
        }

        @Override
        public String toString() {
            return id;
        }

        public static void register(Json json) {
            json.setSerializer(SongTimeFormat.class, new Json.Serializer<SongTimeFormat>() {
                @Override
                public void write(Json json, SongTimeFormat object, Class knownType) {
                    json.writeValue(object.getId());
                }

                @Override
                public SongTimeFormat read(Json json, JsonValue jsonData, Class type) {
                    return SongTimeFormat.fromString(jsonData.asString());
                }
            });
        }
    }

    public static class SongTimeChange {
        public static final Array<Integer> DEFAULT_BEAT_TUPLETS = new Array<Integer>(new Integer[]{4, 4, 4, 4});
        public static final SongTimeChange DEFAULT_SONGTIMECHANGE = new SongTimeChange(0, 100);
        public static final Array<SongTimeChange> DEFAULT_SONGTIMECHANGES = new Array<SongTimeChange>(new SongTimeChange[]{DEFAULT_SONGTIMECHANGE});

        // Required
        public float timeStamp;
        public float bpm;

        // Optional
        public Float beatTime = null;
        public int timeSignatureNum = 4;
        public int timeSignatureDen = 4;
        public Array<Integer> beatTuplets = new Array<>(DEFAULT_BEAT_TUPLETS);

        public SongTimeChange() {}

        public SongTimeChange(float timeStamp, float bpm) {
            this.timeStamp = timeStamp;
            this.bpm = bpm;
        }

        public SongTimeChange(float timeStamp, float bpm, int timeSignatureNum, int timeSignatureDen, Float beatTime, Array<Integer> beatTuplets) {
            this.timeStamp = timeStamp;
            this.bpm = bpm;
            this.timeSignatureNum = timeSignatureNum;
            this.timeSignatureDen = timeSignatureDen;
            this.beatTime = beatTime;
            this.beatTuplets = (beatTuplets != null) ? new Array<>(beatTuplets) : new Array<>(DEFAULT_BEAT_TUPLETS);
        }

        @Override
        public String toString() {
            return "SongTimeChange{" +
                "timeStamp=" + timeStamp +
                ", bpm=" + bpm +
                ", beatTime=" + beatTime +
                ", timeSignatureNum=" + timeSignatureNum +
                ", timeSignatureDen=" + timeSignatureDen +
                ", beatTuplets=" + beatTuplets +
                '}';
        }
    }

    public static class SongOffsets {
        public float instrumental = 0f;
        public ObjectMap<String, Float> altInstrumentals = new ObjectMap<>();
        public ObjectMap<String, Float> vocals = new ObjectMap<>();
        public ObjectMap<String, ObjectMap<String, Float>> altVocals = new ObjectMap<>();

        public SongOffsets() {}

        public SongOffsets(float instrumental) {
            this.instrumental = instrumental;
        }

        public SongOffsets(float instrumental, ObjectMap<String, Float> altInstrumentals, ObjectMap<String, Float> vocals, ObjectMap<String, ObjectMap<String, Float>> altVocals) {
            this.instrumental = instrumental;
            this.altInstrumentals = altInstrumentals != null ? altInstrumentals : new ObjectMap<>();
            this.vocals = vocals != null ? vocals : new ObjectMap<>();
            this.altVocals = altVocals != null ? altVocals : new ObjectMap<>();
        }
    }

    public static class SongMusicData {
        // Required fields
        public String songName = "Unknown";
        public String artist = "Unknown";

        // Optional fields
        public Integer divisions = 96;
        public Boolean looped = false;

        public String generatedBy = SongRegistry.DEFAULT_GENERATEDBY;

        public SongTimeFormat timeFormat = SongTimeFormat.MILLISECONDS;
        public Array<SongTimeChange> timeChanges = new Array<>(new SongTimeChange[]{new SongTimeChange(0, 100)});

        public transient String variation;

        public SongMusicData() {
            this("Unknown", "Unknown", Constants.DEFAULT_VARIATION);
        }

        public SongMusicData(String songName, String artist, String variation) {
            this.songName = songName != null ? songName : "Unknown";
            this.artist = artist != null ? artist : "Unknown";
            this.variation = variation != null ? variation : Constants.DEFAULT_VARIATION;
        }

        @Override
        public String toString() {
            return "SongMusicData{" +
                "songName='" + songName + '\'' +
                ", artist='" + artist + '\'' +
                ", divisions=" + divisions +
                ", looped=" + looped +
                ", generatedBy='" + generatedBy + '\'' +
                ", timeFormat=" + timeFormat +
                ", timeChanges=" + timeChanges +
                ", variation='" + variation + '\'' +
                '}';
        }
    }

    public static class SongPlayData {
        // Required
        public Array<String> difficulties = new Array<>();
        public String stage;
        public String noteStyle;

        // Optional
        public Array<String> songVariations = new Array<>();
        public SongCharacterData characters;
        public ObjectMap<String, Integer> ratings = new ObjectMap<String, Integer>() {{
            put("normal", 0);
        }};
        public String album = null;
        public String stickerPack = null;
        public int previewStart = 0;
        public int previewEnd = 15000;

        public SongPlayData() {}

        @Override
        public String toString() {
            return "SongPlayData{" +
                "songVariations=" + songVariations +
                ", difficulties=" + difficulties +
                ", stage='" + stage + '\'' +
                ", noteStyle='" + noteStyle + '\'' +
                ", ratings=" + ratings +
                ", album='" + album + '\'' +
                ", stickerPack='" + stickerPack + '\'' +
                ", previewStart=" + previewStart +
                ", previewEnd=" + previewEnd +
                '}';
        }
    }

    public static class SongCharacterData {
        public String player = "";
        public String girlfriend = "";
        public String opponent = "";
        public String instrumental = "";
        public Array<String> altInstrumentals = new Array<>();
        public Array<String> opponentVocals = null;
        public Array<String> playerVocals = null;

        public SongCharacterData() {

        }

        public SongCharacterData(String player, String girlfriend, String opponent, String instrumental, Array<String> altInstrumentals, Array<String> opponentVocals, Array<String> playerVocals) {
            this.player = player;
            this.girlfriend = girlfriend;
            this.opponent = opponent;
            this.instrumental = instrumental;

            this.altInstrumentals = altInstrumentals;
            this.opponentVocals = opponentVocals;
            this.playerVocals = playerVocals;

            if (opponentVocals == null) this.opponentVocals = new Array<>(new String[]{opponent});
            if (playerVocals == null) this.playerVocals = new Array<>(new String[]{player});
        }

        @Override
        public String toString() {
            return "SongCharacterData{" +
                "player='" + player + '\'' +
                ", girlfriend='" + girlfriend + '\'' +
                ", opponent='" + opponent + '\'' +
                ", instrumental='" + instrumental + '\'' +
                ", altInstrumentals=" + altInstrumentals +
                ", opponentVocals=" + opponentVocals +
                ", playerVocals=" + playerVocals +
                '}';
        }
    }

    public static class SongChartData {
        public ObjectMap<String, Float> scrollSpeed;
        public Array<SongEventData> events;
        public ObjectMap<String, Array<SongNoteData>> notes;

        public String generatedBy = SongRegistry.DEFAULT_GENERATEDBY;

        public transient String variation;

        public SongChartData() {
        }

        public Float getScrollSpeed(String diff) {
            Float result = this.scrollSpeed.get(diff);

            if (result == 0.0f && !Objects.equals(diff, "default")) return getScrollSpeed("default");

            return (result == 0.0f) ? 1.0f : result;
        }
    }

    public static class SongEventData implements Json.Serializable {
        private float time;
        public String eventKind;
        public Object value = null;
        public transient boolean activated = false;
        private transient Float stepTime = null;

        public SongEventData() {
            this(0f, "", null);
        }

        public SongEventData(float time, String eventKind) {
            this(time, eventKind, null);
        }

        public SongEventData(float time, String eventKind, Object value) {
            this.time = time;
            this.eventKind = eventKind;
            this.value = value;
        }

        public void setTime(float time) {
            this.stepTime = null;
            this.time = time;
        }

        public float getStepTime(boolean force) {
            if (stepTime != null && !force) return stepTime;

            stepTime = Conductor.getInstance().getTimeInSteps(this.time);
            return stepTime;
        }

        public boolean equals(SongEventData other) {
            if (other == null) return false;
            return Float.compare(this.time, other.time) == 0 && this.eventKind.equals(other.eventKind) && Objects.equals(this.value, other.value);
        }

        public boolean greaterThan(SongEventData other) {
            return this.time > other.time;
        }

        public boolean lessThan(SongEventData other) {
            return this.time < other.time;
        }

        @Override
        public void write(Json json) {
            json.writeValue("t", time);
            json.writeValue("e", eventKind);
            if (value != null) json.writeValue("v", value);
        }

        @Override
        public void read(Json json, JsonValue jsonData) {
            // Try alias first, then full name as fallback
            time = jsonData.getFloat("t", jsonData.getFloat("time", 0f));
            eventKind = jsonData.getString("e", jsonData.getString("eventKind", ""));

            JsonValue valueData = jsonData.get("v");
            if (valueData == null) valueData = jsonData.get("value");
            if (valueData != null) {
                if (valueData.isString()) value = valueData.asString();
                else if (valueData.isNumber()) value = valueData.asFloat();
                else if (valueData.isBoolean()) value = valueData.asBoolean();
                else if (valueData.isArray()) value = json.readValue(Object.class, valueData);
                else if (valueData.isObject()) value = json.readValue(Object.class, valueData);
                else value = json.readValue(Object.class, valueData);
            }
        }

        @Override
        public String toString() {
            return "SongEventData{time=" + time + ", eventKind='" + eventKind + '\'' + ", value=" + value + ", activated=" + activated + ", stepTime=" + stepTime + '}';
        }
    }

    public static class SongNoteData implements Json.Serializable {
        private float time;
        public int data;
        private float length = 0f;
        public String kind = null;
        public Array<NoteParamData> params = new Array<>();
        private transient Float stepTime = null;
        private transient Float stepLength = null;

        public SongNoteData() {
            this(0f, 0, 0f, null, null);
        }

        public SongNoteData(float time, int data) {
            this(time, data, 0f, null, null);
        }

        public SongNoteData(float time, int data, float length) {
            this(time, data, length, null, null);
        }

        public SongNoteData(float time, int data, float length, String kind) {
            this(time, data, length, kind, null);
        }

        public SongNoteData(float time, int data, float length, String kind, Array<NoteParamData> params) {
            this.time = time;
            this.data = data;
            this.length = length;
            setKind(kind);
            this.params = params != null ? params : new Array<>();
        }

        // Getters/setters with cache invalidation
        public void setTime(float time) {
            this.stepTime = null;
            this.time = time;
        }

        public void setLength(float length) {
            this.stepLength = null;
            this.length = length;
        }

        public String getKind() {
            if (kind == null || kind.isEmpty()) return null;
            return kind;
        }

        public void setKind(String kind) {
            if (kind != null && kind.isEmpty()) kind = null;
            this.kind = kind;
        }

        // Utility methods
        public boolean isHoldNote() {
            return length > 0;
        }

        public int getDirection() {
            return getDirection(4);
        }

        public int getDirection(int strumlineSize) {
            return data % strumlineSize;
        }

        public String getDirectionName() {
            return getDirectionName(4);
        }

        public String getDirectionName(int strumlineSize) {
            return buildDirectionName(data, strumlineSize);
        }

        public static String buildDirectionName(int data, int strumlineSize) {
            return switch (data % strumlineSize) {
                case 0 -> "Left";
                case 1 -> "Down";
                case 2 -> "Up";
                case 3 -> "Right";
                default -> "Unknown";
            };
        }

        public int getStrumlineIndex() {
            return getStrumlineIndex(4);
        }

        public int getStrumlineIndex(int strumlineSize) {
            return (int) Math.floor((float) data / strumlineSize);
        }

        public boolean getMustHitNote() {
            return getMustHitNote(4);
        }

        public boolean getMustHitNote(int strumlineSize) {
            return getStrumlineIndex(strumlineSize) == 0;
        }

        public float getStepTime() {
            return getStepTime(false);
        }

        public float getStepTime(boolean force) {
            if (stepTime != null && !force) return stepTime;

            stepTime = Conductor.getInstance().getTimeInSteps(this.time);
            return stepTime;
        }

        public float getStepLength() {
            return getStepLength(false);
        }

        public float getStepLength(boolean force) {
            if (length <= 0) return 0f;

            if (stepLength != null && !force) return stepLength;

            stepLength = Conductor.getInstance().getTimeInSteps(this.time + this.length) - getStepTime();
            return stepLength;
        }

        // Comparison methods
        public boolean equals(SongNoteData other) {
            if (other == null) return false;

            // Handle kind comparison (null/empty are equivalent)
            String thisKind = getKind();
            String otherKind = other.getKind();

            if (thisKind == null && otherKind != null) return false;
            if (thisKind != null && otherKind == null) return false;
            if (thisKind != null && !thisKind.equals(otherKind)) return false;

            return Float.compare(this.time, other.time) == 0 &&
                this.data == other.data &&
                Float.compare(this.length, other.length) == 0 &&
                this.params.equals(other.params);
        }

        public boolean greaterThan(SongNoteData other) {
            return other != null && this.time > other.time;
        }

        public boolean lessThan(SongNoteData other) {
            return other != null && this.time < other.time;
        }

        public boolean greaterThanOrEquals(SongNoteData other) {
            return other != null && this.time >= other.time;
        }

        public boolean lessThanOrEquals(SongNoteData other) {
            return other != null && this.time <= other.time;
        }

        @Override
        public void write(Json json) {
            json.writeValue("t", time);
            json.writeValue("d", data);
            if (length > 0) json.writeValue("l", length);
            if (kind != null && !kind.isEmpty()) json.writeValue("k", kind);
            if (params.size > 0) json.writeValue("p", params);
        }

        @Override
        public void read(Json json, JsonValue jsonData) {
            time = jsonData.getFloat("t", jsonData.getFloat("time", 0f));
            data = jsonData.getInt("d", jsonData.getInt("data", 0));
            setLength(jsonData.getFloat("l", jsonData.getFloat("length", 0f)));
            setKind(jsonData.getString("k", jsonData.getString("kind", null)));

            // Try alias first, then full name as fallback
            JsonValue paramsData = jsonData.get("p");
            if (paramsData == null) paramsData = jsonData.get("params");
            if (paramsData != null && paramsData.isArray()) params = json.readValue(Array.class, NoteParamData.class, paramsData);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("SongNoteData(").append(time).append("ms, ");
            if (length > 0) sb.append("[").append(length).append("ms hold] ");
            sb.append(data);
            if (kind != null && !kind.isEmpty()) sb.append(" [kind: ").append(kind).append("]");
            sb.append(")");
            return sb.toString();
        }
    }

    public static class NoteParamData implements Json.Serializable {
        public String name;
        public Object value;

        public NoteParamData() {
            this("", null);
        }

        public NoteParamData(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public void write(Json json) {
            json.writeValue("n", name);
            if (value != null) json.writeValue("v", value);
        }

        @Override
        public void read(Json json, JsonValue jsonData) {
            // Try alias first, then full name as fallback
            name = jsonData.getString("n", jsonData.getString("name", ""));

            JsonValue valueData = jsonData.get("v");
            if (valueData == null) valueData = jsonData.get("value");
            if (valueData != null) {
                // Handle different value types like in SongEventData
                if (valueData.isString()) value = valueData.asString();
                else if (valueData.isNumber()) value = valueData.asFloat();
                else if (valueData.isBoolean()) value = valueData.asBoolean();
                else value = json.readValue(Object.class, valueData);
            }
        }

        @Override
        public String toString() {
            return "NoteParamData{name='" + name + '\'' + ", value=" + value + '}';
        }

    }
}

