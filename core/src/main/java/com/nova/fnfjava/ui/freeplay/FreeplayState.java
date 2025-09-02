package com.nova.fnfjava.ui.freeplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.text.FlxText;
import com.nova.fnfjava.ui.MainMenuState;
import com.nova.fnfjava.ui.MusicBeatState;
import com.nova.fnfjava.ui.MusicBeatSubState;
import com.nova.fnfjava.ui.transition.stickers.StickerSubState;

public class FreeplayState extends MusicBeatSubState {
    public FreeplayState(Main main, FreeplayStateParams params, StickerSubState stickers) {
        super(main);
    }

    public FreeplayState(Main main) {
        super(main);
    }

    @Override
    public void show() {
        super.show();

        FlxText pauseText = new FlxText(0, 0, "PAUSED");
        pauseText.setFormat("VCR_OSD_MONO", 32, Color.WHITE);
        add(pauseText);

        openCallback = () -> System.out.println("Freeplay opened!");
        closeCallback = () -> System.out.println("Freeplay closed!");
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            close(); // This will call closeSubState() on the parent
        }
    }

    public static MusicBeatState build(Main main,FreeplayStateParams params, StickerSubState stickers) {
        return new FreeplayState(main, params, stickers);
    }

    public record FreeplayStateParams(String character) {}
}
