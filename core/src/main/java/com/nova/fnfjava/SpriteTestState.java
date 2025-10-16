package com.nova.fnfjava;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.audio.FunkinSound;
import com.nova.fnfjava.graphics.FunkinSprite;
import com.nova.fnfjava.ui.MusicBeatState;

public class SpriteTestState extends MusicBeatState {
    public static Main main;

    public FunkinSprite gfDance;

    public SpriteTestState(Main main) {
        super(main);
    }

    @Override
    public void show() {
        super.show();

        if (Main.sound.music == null) playMenuMusic();

        gfDance = FunkinSprite.create(Gdx.graphics.getWidth() * 0.4F, Gdx.graphics.getHeight() * 0.07F, Paths.getAtlas("gfDanceTitle"));
        gfDance.animation.addByIndices("danceLeft", "gfDance", new Array<>(new Integer[]{30, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14}), 24);
        gfDance.animation.addByIndices("danceRight", "gfDance", new Array<>(new Integer[]{15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29}), 24);
        add(gfDance);
    }

    @Override
    public void render(float delta) {
        Conductor.getInstance().update();

        super.render(delta);
    }

    @Override
    public void beatHit(Signal<Integer> integerSignal, Integer beat) {
        super.beatHit(integerSignal, beat);
    }

    public void playMenuMusic() {
        Main.sound.playMusic("freakyMenu", new FunkinSound.FunkinSoundPlayMusicParams.Builder()
            .overrideExisting(true)
            .restartTrack(false)
            .persist(true)
            .build());
        Main.sound.music.fadeIn(4000f);
    }
}
