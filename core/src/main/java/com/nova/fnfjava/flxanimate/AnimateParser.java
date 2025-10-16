package com.nova.fnfjava.flxanimate;

import com.badlogic.gdx.utils.Json;
import com.nova.fnfjava.Paths;

public class AnimateParser {
    public final Json parser = new Json();

    public AnimateParser(String assetPath) {
        //AnimateData data = parser.fromJson();
    }

    public void setupParser() {
        parser.setIgnoreUnknownFields(true);
    }


}
