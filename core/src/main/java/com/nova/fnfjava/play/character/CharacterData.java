package com.nova.fnfjava.play.character;

import com.badlogic.gdx.utils.ObjectMap;

public class CharacterData {
    public String name;
    //public CharacterRenderType renderType;
    public float scale = 1f;

    public class CharacterDataParser {
        public static final ObjectMap<String, CharacterData> characterCache = new ObjectMap<>();

        public static final float DEFAULT_DANCEEVERY = 1.0f;

        public static CharacterData fetchCharacterData(String charId) {
            if (characterCache.containsKey(charId)) return characterCache.get(charId);

            return null;
        }
    }
}
