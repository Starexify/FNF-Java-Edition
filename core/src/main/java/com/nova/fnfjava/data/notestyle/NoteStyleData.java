package com.nova.fnfjava.data.notestyle;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.nova.fnfjava.data.animation.UnnamedAnimationData;

public class NoteStyleData {
    public String name;
    public String author;
    public String fallback = null;
    public NoteStyleAssetsData assets;

    public NoteStyleData() {}

    public NoteStyleData(String name, String author, NoteStyleAssetsData assets) {
        this.name = name;
        this.author = author;
        this.assets = assets;
    }

    @Override
    public String toString() {
        return "NoteStyleData{" +
            "name='" + name + '\'' +
            ", author='" + author + '\'' +
            ", fallback='" + fallback + '\'' +
            ", assets=" + assets +
            '}';
    }

    public static class NoteStyleAssetsData implements Json.Serializable {
        public NoteStyleAssetData<NoteStyleData_Note> note = null;
        public NoteStyleAssetData<NoteStyleData_HoldNote> holdNote = null;
        public NoteStyleAssetData<NoteStyleData_NoteStrumline> noteStrumline = null;
        public NoteStyleAssetData<NoteStyleData_NoteSplash> noteSplash = null;
        public NoteStyleAssetData<NoteStyleData_HoldNoteCover> holdNoteCover = null;

        public NoteStyleAssetData<NoteStyleData_Countdown> countdownThree = null;
        public NoteStyleAssetData<NoteStyleData_Countdown> countdownTwo = null;
        public NoteStyleAssetData<NoteStyleData_Countdown> countdownOne = null;
        public NoteStyleAssetData<NoteStyleData_Countdown> countdownGo = null;

        public NoteStyleAssetData<NoteStyleData_Judgement> judgementSick = null;
        public NoteStyleAssetData<NoteStyleData_Judgement> judgementGood = null;
        public NoteStyleAssetData<NoteStyleData_Judgement> judgementBad = null;
        public NoteStyleAssetData<NoteStyleData_Judgement> judgementShit = null;

        public NoteStyleAssetData<NoteStyleData_ComboNum> comboNumber0 = null;
        public NoteStyleAssetData<NoteStyleData_ComboNum> comboNumber1 = null;
        public NoteStyleAssetData<NoteStyleData_ComboNum> comboNumber2 = null;
        public NoteStyleAssetData<NoteStyleData_ComboNum> comboNumber3 = null;
        public NoteStyleAssetData<NoteStyleData_ComboNum> comboNumber4 = null;
        public NoteStyleAssetData<NoteStyleData_ComboNum> comboNumber5 = null;
        public NoteStyleAssetData<NoteStyleData_ComboNum> comboNumber6 = null;
        public NoteStyleAssetData<NoteStyleData_ComboNum> comboNumber7 = null;
        public NoteStyleAssetData<NoteStyleData_ComboNum> comboNumber8 = null;
        public NoteStyleAssetData<NoteStyleData_ComboNum> comboNumber9 = null;

        @Override
        public void write(Json json) {
            if (note != null) json.writeValue("note", note);
            if (holdNote != null) json.writeValue("holdNote", holdNote);
            if (noteStrumline != null) json.writeValue("noteStrumline", noteStrumline);
            if (noteSplash != null) json.writeValue("noteSplash", noteSplash);
            if (holdNoteCover != null) json.writeValue("holdNoteCover", holdNoteCover);

            if (countdownThree != null) json.writeValue("countdownThree", countdownThree);
            if (countdownTwo != null) json.writeValue("countdownTwo", countdownTwo);
            if (countdownOne != null) json.writeValue("countdownOne", countdownOne);
            if (countdownGo != null) json.writeValue("countdownGo", countdownGo);

            if (judgementSick != null) json.writeValue("judgementSick", judgementSick);
            if (judgementGood != null) json.writeValue("judgementGood", judgementGood);
            if (judgementBad != null) json.writeValue("judgementBad", judgementBad);
            if (judgementShit != null) json.writeValue("judgementShit", judgementShit);

            if (comboNumber0 != null) json.writeValue("comboNumber0", comboNumber0);
            if (comboNumber1 != null) json.writeValue("comboNumber1", comboNumber1);
            if (comboNumber2 != null) json.writeValue("comboNumber2", comboNumber2);
            if (comboNumber3 != null) json.writeValue("comboNumber3", comboNumber3);
            if (comboNumber4 != null) json.writeValue("comboNumber4", comboNumber4);
            if (comboNumber5 != null) json.writeValue("comboNumber5", comboNumber5);
            if (comboNumber6 != null) json.writeValue("comboNumber6", comboNumber6);
            if (comboNumber7 != null) json.writeValue("comboNumber7", comboNumber7);
            if (comboNumber8 != null) json.writeValue("comboNumber8", comboNumber8);
            if (comboNumber9 != null) json.writeValue("comboNumber9", comboNumber9);
        }

        @Override
        public void read(Json json, JsonValue jsonData) {
            note = (NoteStyleAssetData<NoteStyleData_Note>) json.readValue(NoteStyleAssetData.class, jsonData.get("note"));
            holdNote = (NoteStyleAssetData<NoteStyleData_HoldNote>) json.readValue(NoteStyleAssetData.class, jsonData.get("holdNote"));
            noteStrumline = (NoteStyleAssetData<NoteStyleData_NoteStrumline>) json.readValue(NoteStyleAssetData.class, jsonData.get("noteStrumline"));
            noteSplash = (NoteStyleAssetData<NoteStyleData_NoteSplash>) json.readValue(NoteStyleAssetData.class, jsonData.get("noteSplash"));
            holdNoteCover = (NoteStyleAssetData<NoteStyleData_HoldNoteCover>) json.readValue(NoteStyleAssetData.class, jsonData.get("holdNoteCover"));

            countdownThree = (NoteStyleAssetData<NoteStyleData_Countdown>) json.readValue(NoteStyleAssetData.class, jsonData.get("countdownThree"));
            countdownTwo = (NoteStyleAssetData<NoteStyleData_Countdown>) json.readValue(NoteStyleAssetData.class, jsonData.get("countdownTwo"));
            countdownOne = (NoteStyleAssetData<NoteStyleData_Countdown>) json.readValue(NoteStyleAssetData.class, jsonData.get("countdownOne"));
            countdownGo = (NoteStyleAssetData<NoteStyleData_Countdown>) json.readValue(NoteStyleAssetData.class, jsonData.get("countdownGo"));

            judgementSick = (NoteStyleAssetData<NoteStyleData_Judgement>) json.readValue(NoteStyleAssetData.class, jsonData.get("judgementSick"));
            judgementGood = (NoteStyleAssetData<NoteStyleData_Judgement>) json.readValue(NoteStyleAssetData.class, jsonData.get("judgementGood"));
            judgementBad = (NoteStyleAssetData<NoteStyleData_Judgement>) json.readValue(NoteStyleAssetData.class, jsonData.get("judgementBad"));
            judgementShit = (NoteStyleAssetData<NoteStyleData_Judgement>) json.readValue(NoteStyleAssetData.class, jsonData.get("judgementShit"));

            comboNumber0 = (NoteStyleAssetData<NoteStyleData_ComboNum>) json.readValue(NoteStyleAssetData.class, jsonData.get("comboNumber0"));
            comboNumber1 = (NoteStyleAssetData<NoteStyleData_ComboNum>) json.readValue(NoteStyleAssetData.class, jsonData.get("comboNumber1"));
            comboNumber2 = (NoteStyleAssetData<NoteStyleData_ComboNum>) json.readValue(NoteStyleAssetData.class, jsonData.get("comboNumber2"));
            comboNumber3 = (NoteStyleAssetData<NoteStyleData_ComboNum>) json.readValue(NoteStyleAssetData.class, jsonData.get("comboNumber3"));
            comboNumber4 = (NoteStyleAssetData<NoteStyleData_ComboNum>) json.readValue(NoteStyleAssetData.class, jsonData.get("comboNumber4"));
            comboNumber5 = (NoteStyleAssetData<NoteStyleData_ComboNum>) json.readValue(NoteStyleAssetData.class, jsonData.get("comboNumber5"));
            comboNumber6 = (NoteStyleAssetData<NoteStyleData_ComboNum>) json.readValue(NoteStyleAssetData.class, jsonData.get("comboNumber6"));
            comboNumber7 = (NoteStyleAssetData<NoteStyleData_ComboNum>) json.readValue(NoteStyleAssetData.class, jsonData.get("comboNumber7"));
            comboNumber8 = (NoteStyleAssetData<NoteStyleData_ComboNum>) json.readValue(NoteStyleAssetData.class, jsonData.get("comboNumber8"));
            comboNumber9 = (NoteStyleAssetData<NoteStyleData_ComboNum>) json.readValue(NoteStyleAssetData.class, jsonData.get("comboNumber9"));
        }
    }

    public static class NoteStyleAssetData<T> implements Json.Serializable {
        public String assetPath;
        public float scale = 1.0f;
        public Array<Float> offsets = new Array<>(new Float[]{0f, 0f});
        public boolean isPixel = false;
        public float alpha = 1.0f;
        public boolean animated = false;
        public T data = null;

        public NoteStyleAssetData() {}

        public NoteStyleAssetData(String assetPath) {
            this.assetPath = assetPath;
        }


        @Override
        public void write(Json json) {
            json.writeValue("assetPath", assetPath);
            if (scale != 1.0f) json.writeValue("scale", scale);
            if (offsets.get(0) != 0f || offsets.get(1) != 0f) json.writeValue("offsets", offsets);
            if (isPixel) json.writeValue("isPixel", isPixel);
            if (alpha != 1.0f) json.writeValue("alpha", alpha);
            if (animated) json.writeValue("animated", animated);
            if (data != null) json.writeValue("data", data);
        }

        @Override
        public void read(Json json, JsonValue jsonData) {
            assetPath = jsonData.getString("assetPath", "");
            scale = jsonData.getFloat("scale", 1.0f);

            JsonValue offsetsData = jsonData.get("offsets");
            if (offsetsData != null && offsetsData.isArray()) offsets = json.readValue(Array.class, Float.class, offsetsData);
            else offsets = new Array<>(new Float[]{0f, 0f});

            isPixel = jsonData.getBoolean("isPixel", false);
            alpha = jsonData.getFloat("alpha", 1.0f);
            animated = jsonData.getBoolean("animated", false);

            JsonValue dataValue = jsonData.get("data");
            if (dataValue != null) data = (T) json.readValue(Object.class, dataValue);
        }
    }

    public static class NoteStyleData_Note {
        public UnnamedAnimationData left;
        public UnnamedAnimationData down;
        public UnnamedAnimationData up;
        public UnnamedAnimationData right;
    }

    public static class NoteStyleData_Countdown {
        public String audioPath;
    }

    public static class NoteStyleData_HoldNote implements Json.Serializable {
        @Override public void write(Json json) {}
        @Override public void read(Json json, JsonValue jsonData) {}
    }

    public static class NoteStyleData_Judgement implements Json.Serializable {
        @Override public void write(Json json) {}
        @Override public void read(Json json, JsonValue jsonData) {}
    }

    public static class NoteStyleData_ComboNum implements Json.Serializable {
        @Override public void write(Json json) {}
        @Override public void read(Json json, JsonValue jsonData) {}
    }

    public static class NoteStyleData_NoteStrumline implements Json.Serializable {
        public UnnamedAnimationData leftStatic;
        public UnnamedAnimationData leftPress;
        public UnnamedAnimationData leftConfirm;
        public UnnamedAnimationData leftConfirmHold;
        public UnnamedAnimationData downStatic;
        public UnnamedAnimationData downPress;
        public UnnamedAnimationData downConfirm;
        public UnnamedAnimationData downConfirmHold;
        public UnnamedAnimationData upStatic;
        public UnnamedAnimationData upPress;
        public UnnamedAnimationData upConfirm;
        public UnnamedAnimationData upConfirmHold;
        public UnnamedAnimationData rightStatic;
        public UnnamedAnimationData rightPress;
        public UnnamedAnimationData rightConfirm;
        public UnnamedAnimationData rightConfirmHold;

        @Override
        public void write(Json json) {
            json.writeValue("leftStatic", leftStatic);
            json.writeValue("leftPress", leftPress);
            json.writeValue("leftConfirm", leftConfirm);
            json.writeValue("leftConfirmHold", leftConfirmHold);
            json.writeValue("downStatic", downStatic);
            json.writeValue("downPress", downPress);
            json.writeValue("downConfirm", downConfirm);
            json.writeValue("downConfirmHold", downConfirmHold);
            json.writeValue("upStatic", upStatic);
            json.writeValue("upPress", upPress);
            json.writeValue("upConfirm", upConfirm);
            json.writeValue("upConfirmHold", upConfirmHold);
            json.writeValue("rightStatic", rightStatic);
            json.writeValue("rightPress", rightPress);
            json.writeValue("rightConfirm", rightConfirm);
            json.writeValue("rightConfirmHold", rightConfirmHold);
        }

        @Override
        public void read(Json json, JsonValue jsonData) {
            leftStatic = json.readValue(UnnamedAnimationData.class, jsonData.get("leftStatic"));
            leftPress = json.readValue(UnnamedAnimationData.class, jsonData.get("leftPress"));
            leftConfirm = json.readValue(UnnamedAnimationData.class, jsonData.get("leftConfirm"));
            leftConfirmHold = json.readValue(UnnamedAnimationData.class, jsonData.get("leftConfirmHold"));
            downStatic = json.readValue(UnnamedAnimationData.class, jsonData.get("downStatic"));
            downPress = json.readValue(UnnamedAnimationData.class, jsonData.get("downPress"));
            downConfirm = json.readValue(UnnamedAnimationData.class, jsonData.get("downConfirm"));
            downConfirmHold = json.readValue(UnnamedAnimationData.class, jsonData.get("downConfirmHold"));
            upStatic = json.readValue(UnnamedAnimationData.class, jsonData.get("upStatic"));
            upPress = json.readValue(UnnamedAnimationData.class, jsonData.get("upPress"));
            upConfirm = json.readValue(UnnamedAnimationData.class, jsonData.get("upConfirm"));
            upConfirmHold = json.readValue(UnnamedAnimationData.class, jsonData.get("upConfirmHold"));
            rightStatic = json.readValue(UnnamedAnimationData.class, jsonData.get("rightStatic"));
            rightPress = json.readValue(UnnamedAnimationData.class, jsonData.get("rightPress"));
            rightConfirm = json.readValue(UnnamedAnimationData.class, jsonData.get("rightConfirm"));
            rightConfirmHold = json.readValue(UnnamedAnimationData.class, jsonData.get("rightConfirmHold"));
        }
    }

    public static class NoteStyleData_NoteSplash implements Json.Serializable {
        public boolean enabled = true;
        public int framerateDefault = 24;
        public int framerateVariance = 2;
        public String blendMode = "normal";
        public Array<UnnamedAnimationData> leftSplashes = null;
        public Array<UnnamedAnimationData> downSplashes = null;
        public Array<UnnamedAnimationData> upSplashes = null;
        public Array<UnnamedAnimationData> rightSplashes = null;

        @Override
        public void write(Json json) {
            if (!enabled) json.writeValue("enabled", enabled);
            if (framerateDefault != 24) json.writeValue("framerateDefault", framerateDefault);
            if (framerateVariance != 2) json.writeValue("framerateVariance", framerateVariance);
            if (!"normal".equals(blendMode)) json.writeValue("blendMode", blendMode);
            if (leftSplashes != null) json.writeValue("leftSplashes", leftSplashes);
            if (downSplashes != null) json.writeValue("downSplashes", downSplashes);
            if (upSplashes != null) json.writeValue("upSplashes", upSplashes);
            if (rightSplashes != null) json.writeValue("rightSplashes", rightSplashes);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void read(Json json, JsonValue jsonData) {
            enabled = jsonData.getBoolean("enabled", true);
            framerateDefault = jsonData.getInt("framerateDefault", 24);
            framerateVariance = jsonData.getInt("framerateVariance", 2);
            blendMode = jsonData.getString("blendMode", "normal");
            leftSplashes = json.readValue(Array.class, UnnamedAnimationData.class, jsonData.get("leftSplashes"));
            downSplashes = json.readValue(Array.class, UnnamedAnimationData.class, jsonData.get("downSplashes"));
            upSplashes = json.readValue(Array.class, UnnamedAnimationData.class, jsonData.get("upSplashes"));
            rightSplashes = json.readValue(Array.class, UnnamedAnimationData.class, jsonData.get("rightSplashes"));
        }
    }

    public static class NoteStyleData_HoldNoteCover implements Json.Serializable {
        public boolean enabled = true;
        public NoteStyleData_HoldNoteCoverDirectionData left = null;
        public NoteStyleData_HoldNoteCoverDirectionData down = null;
        public NoteStyleData_HoldNoteCoverDirectionData up = null;
        public NoteStyleData_HoldNoteCoverDirectionData right = null;

        @Override
        public void write(Json json) {
            if (!enabled) json.writeValue("enabled", enabled);
            if (left != null) json.writeValue("left", left);
            if (down != null) json.writeValue("down", down);
            if (up != null) json.writeValue("up", up);
            if (right != null) json.writeValue("right", right);
        }

        @Override
        public void read(Json json, JsonValue jsonData) {
            enabled = jsonData.getBoolean("enabled", true);
            left = json.readValue(NoteStyleData_HoldNoteCoverDirectionData.class, jsonData.get("left"));
            down = json.readValue(NoteStyleData_HoldNoteCoverDirectionData.class, jsonData.get("down"));
            up = json.readValue(NoteStyleData_HoldNoteCoverDirectionData.class, jsonData.get("up"));
            right = json.readValue(NoteStyleData_HoldNoteCoverDirectionData.class, jsonData.get("right"));
        }
    }

    public static class NoteStyleData_HoldNoteCoverDirectionData implements Json.Serializable {
        public String assetPath = null;
        public UnnamedAnimationData start = null;
        public UnnamedAnimationData hold = null;
        public UnnamedAnimationData end = null;

        @Override
        public void write(Json json) {
            if (assetPath != null) json.writeValue("assetPath", assetPath);
            if (start != null) json.writeValue("start", start);
            if (hold != null) json.writeValue("hold", hold);
            if (end != null) json.writeValue("end", end);
        }

        @Override
        public void read(Json json, JsonValue jsonData) {
            assetPath = jsonData.getString("assetPath", null);
            start = json.readValue(UnnamedAnimationData.class, jsonData.get("start"));
            hold = json.readValue(UnnamedAnimationData.class, jsonData.get("hold"));
            end = json.readValue(UnnamedAnimationData.class, jsonData.get("end"));
        }
    }
}
