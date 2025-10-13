package com.nova.fnfjava;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.nova.fnfjava.audio.FunkinSound;
import com.nova.fnfjava.graphics.AnimatedSprite;
import com.nova.fnfjava.graphics.FunkinSprite;
import com.nova.fnfjava.ui.MusicBeatState;

public class SpriteTestState extends MusicBeatState {
    public static Main main;

    public AnimatedSprite logoBl;

    public SpriteTestState(Main main) {
        super(main);
    }

    @Override
    public void show() {
        super.show();

        if (Main.sound.music == null) playMenuMusic();

        logoBl = new AnimatedSprite(-150, 100);
        logoBl.atlas = new TextureAtlas("assets/images/logoBumpin.atlas");
        logoBl.animation.addByPrefix("bump", "logo bumpin", 24);
        logoBl.animation.play("bump");

        add(logoBl);
    }

    @Override
    public void render(float delta) {
        Conductor.getInstance().update();

        super.render(delta);
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
