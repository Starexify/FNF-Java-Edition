package com.nova.fnfjava.ui.title;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.Paths;
import com.nova.fnfjava.ui.MusicBeatState;

import java.lang.reflect.Array;

/**
 * After 40 seconds of inactivity on the title screen,
 * the game will enter the Attract state, as a reference to physical arcade machines.
 * <p>
 * In the current version, this just plays generic game/merch trailers,
 * but this can be updated to include gameplay footage, or something more elaborate.
 */
public class AttractState extends MusicBeatState {
    /**
     * The videos that can be played by the Attract state.
     * Each entry contains the path to the video file.
     */
    public static final VideoEntry[] VIDEO_PATHS = {
        new VideoEntry(Paths.videos("mobileRelease")),
        new VideoEntry(Paths.videos("boyfriendEverywhere"))
    };

    public static int nextVideoToPlay = 0;

    // public FlxPieDial pie;
    public float holdDelta = 0;

    public AttractState(Main main) {
        super(main);
    }

    @Override
    public void show() {
        super.show();
        if (Main.sound.music != null) {
            Main.sound.music.dispose();
            Main.sound.music = null;
        }

        String videoPath = getVideoPath();
        Gdx.app.log("AttractState", "Playing native video " + videoPath);
        playVideoNative(videoPath);
    }

    /**
     * Get the path of a random video to display to the user.
     * @return The video path to play.
     */
    public String getVideoPath() {
        String result = VIDEO_PATHS[nextVideoToPlay].path();

        nextVideoToPlay = (nextVideoToPlay + 1) % VIDEO_PATHS.length;

        return result;
    }

    public void playVideoNative(String filePath) {
        //vid = new FunkinVideoSprite(0, 0);

    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            holdDelta += delta;
/*            holdDelta = holdDelta.clamp(0, HOLD_TIME);

            pie.scale.x = pie.scale.y = FlxMath.lerp(pie.scale.x, 1.3, Math.exp(-elapsed * 140.0));*/
        } else {
/*            holdDelta = FlxMath.lerp(holdDelta, -0.1, (elapsed * 3).clamp(0, 1));
            holdDelta = holdDelta.clamp(0, HOLD_TIME);
            pie.scale.x = pie.scale.y = FlxMath.lerp(pie.scale.x, 1, Math.exp(-elapsed * 160.0));*/
        }

/*        pie.amount = Math.min(1, Math.max(0, (holdDelta / HOLD_TIME) * 1.025));
        pie.alpha = FlxMath.remapToRange(pie.amount, 0.025, 1, 0, 1);

        if (pie.amount >= 1) onAttractEnd();*/
    }

    /**
     * When the attraction state ends (after the video ends or the user presses any button),
     * switch immediately to the title screen.
     */
    public void onAttractEnd() {

    }

    public record VideoEntry(String path) {}
}
