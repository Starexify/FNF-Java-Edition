package com.nova.fnfjava.audio;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import games.rednblack.miniaudio.MASound;
import games.rednblack.miniaudio.MiniAudio;

public class SoundGroup extends Group {
    public Array<MASound> sounds;
    private MiniAudio miniAudio;

    private boolean playing = false;

    public void stop() {
        for (MASound sound : sounds)
            if (sound.isPlaying()) sound.stop();

        playing = false;
    }
}
