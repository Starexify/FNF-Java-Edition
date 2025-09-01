package com.nova.fnfjava.ui.freeplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.text.FlxText;
import com.nova.fnfjava.ui.MusicBeatSubState;

public class FreeplayState extends MusicBeatSubState {
    public FreeplayState(Main main) {
        super(main);
    }

    @Override
    public void create() {
        super.create();

        FlxText pauseText = new FlxText(0, 0, "PAUSED");
        pauseText.setFormat("VCR_OSD_MONO", 32, Color.WHITE);
        add(pauseText);

        openCallback = () -> System.out.println("Freeplay opened!");
        closeCallback = () -> System.out.println("Freeplay closed!");
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            close(); // This will call closeSubState() on the parent
        }
    }
}
