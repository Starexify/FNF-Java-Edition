package com.nova.fnfjava.data.stage;

import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.data.animation.AnimationData;
import io.vavr.control.Either;

public class StageData {
    public String name = "Unknown";
    public Array<StageDataProp> props = new Array<>();
    public StageDataCharacters characters;
    public Float cameraZoom = 1.0f;
    public String directory = "shared";

    public StageData() {
        this.characters = makeDefaultCharacters();
    }

    public StageDataCharacters makeDefaultCharacters() {
        return new StageDataCharacters(
            new StageDataCharacter(new Array<>(new Float[]{-100f, -100f})),
            new StageDataCharacter(new Array<>(new Float[]{100f, -100f})),
            new StageDataCharacter(new Array<>(new Float[]{0f, 0f}))
        );
    }

    @Override
    public String toString() {
        return "StageData{" +
            "name='" + name + '\'' +
            ", props=" + props +
            ", characters=" + characters +
            ", cameraZoom=" + cameraZoom +
            ", directory='" + directory + '\'' +
            '}';
    }

    public static class StageDataCharacters {
        public StageDataCharacter bf;
        public StageDataCharacter dad;
        public StageDataCharacter gf;

        public StageDataCharacters() {}

        public StageDataCharacters(StageDataCharacter bf, StageDataCharacter dad, StageDataCharacter gf) {
            this.bf = bf;
            this.dad = dad;
            this.gf = gf;
        }

        @Override
        public String toString() {
            return "StageDataCharacters{" +
                "bf=" + bf +
                ", dad=" + dad +
                ", gf=" + gf +
                '}';
        }
    }

    public static class StageDataProp {
        public String name;
        public String assetPath;
        public Array<Float> position;
        public int zIndex = 0;
        public boolean isPixel = false;
        public boolean flipX = false;
        public boolean flipY = false;
        public Either<Float, Array<Float>> scale = Either.left(1.0f);
        public float alpha = 1.0f;
        public float danceEvery = 0.0f;
        public Array<Float> scroll = new Array<>(new Float[]{1f, 1f});
        public Array<AnimationData> animations = new Array<>();
        public String startingAnimation;
        public String animType = "atlas";
        public float angle = 0.0f;
        public String blend = "";
        public String color = "#FFFFFF";

        public StageDataProp() {}

        @Override
        public String toString() {
            return "StageDataProp{" +
                "name='" + name + '\'' +
                ", assetPath='" + assetPath + '\'' +
                ", position=" + position +
                ", zIndex=" + zIndex +
                ", isPixel=" + isPixel +
                ", flipX=" + flipX +
                ", flipY=" + flipY +
                ", scale=" + scale +
                ", alpha=" + alpha +
                ", danceEvery=" + danceEvery +
                ", scroll=" + scroll +
                ", animations=" + animations +
                ", startingAnimation='" + startingAnimation + '\'' +
                ", animType='" + animType + '\'' +
                ", angle=" + angle +
                ", blend='" + blend + '\'' +
                ", color='" + color + '\'' +
                '}';
        }
    }

    public static class StageDataCharacter {
        int zIndex = 0;
        Array<Float> position = new Array<>(new Float[]{0f, 0f});
        public float scale = 1;
        Array<Float> cameraOffsets;
        Array<Float> scroll = new Array<>(new Float[]{1f, 1f});
        float alpha = 1.0f;
        float angle = 0.0f;

        public StageDataCharacter() {}

        public StageDataCharacter(Array<Float> cameraOffsets) {
            this.cameraOffsets = cameraOffsets;
        }

        @Override
        public String toString() {
            return "StageDataCharacter{" +
                "zIndex=" + zIndex +
                ", position=" + position +
                ", scale=" + scale +
                ", cameraOffsets=" + cameraOffsets +
                ", scroll=" + scroll +
                ", alpha=" + alpha +
                ", angle=" + angle +
                '}';
        }
    }
}
