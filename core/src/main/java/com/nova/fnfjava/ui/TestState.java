package com.nova.fnfjava.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.nova.fnfjava.Main;

public class TestState extends MusicBeatState {
    public TestState(Main main) {
        super(main);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            Gdx.app.log("DEBUG", "W key is pressed (polling)");
        }

        super.render(delta);
    }
}
