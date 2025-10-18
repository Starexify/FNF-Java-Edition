package com.nova.fnfjava;

import com.badlogic.ashley.signals.Signal;
import com.nova.fnfjava.audio.FunkinSound;
import com.nova.fnfjava.ui.MusicBeatState;

public class SpriteTestState extends MusicBeatState {
    public static Main main;

    public SpriteTestState(Main main) {
        super(main);
    }

    @Override
    public void show() {
        super.show();

        if (Main.sound.music == null) playMenuMusic();
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
