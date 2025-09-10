package com.nova.fnfjava.play.character;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class CharacterData {
    public String name;
    //public CharacterRenderType renderType;
    public float scale = 1f;
    public HealthIconData healthIcon;
    public Array<Float> offsets = new Array<>(new Float[]{0f, 0f});
    public Float danceEvery = 1.0f;

    public CharacterData() {

    }

    public class CharacterDataParser {
        public static final ObjectMap<String, CharacterData> characterCache = new ObjectMap<>();

        public static final float DEFAULT_DANCEEVERY = 1.0f;

        public static CharacterData fetchCharacterData(String charId) {
            if (characterCache.containsKey(charId)) return characterCache.get(charId);

            return null;
        }
    }

    public static class HealthIconData {
        public String id;
        public float scale = 1;
        public boolean flipX = false;
        public boolean isPixel = false;
        public Array<Float> offsets = new Array<>(new Float[]{0f, 25f});

        public HealthIconData() {

        }
    }
}
