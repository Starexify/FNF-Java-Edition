package com.nova.fnfjava.ui.title;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.badlogic.gdx.video.scenes.scene2d.VideoActor;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.Paths;
import com.nova.fnfjava.ui.MusicBeatState;
import com.nova.fnfjava.widgets.RadialGauge;

import java.io.FileNotFoundException;

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

    public static final float HOLD_TIME = 1.5f;

    public RadialGauge pie;
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
        try {
            playVideoNative(videoPath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        pie = new RadialGauge(0, 0, 40, Color.WHITE, 45, RadialGauge.RadialGaugeShape.CIRCLE, true, 20);
        pie.setX(Gdx.graphics.getWidth() - (80 * 1.5f));
        pie.setY(pie.getHeight() * 0.5f);
        pie.setBackgroundColor(Color.valueOf("#C5C4C48A"));
        add(pie);
    }

    /**
     * Get the path of a random video to display to the user.
     *
     * @return The video path to play.
     */
    public String getVideoPath() {
        String result = VIDEO_PATHS[nextVideoToPlay].path();

        nextVideoToPlay = (nextVideoToPlay + 1) % VIDEO_PATHS.length;

        return result;
    }

    public VideoActor vid;
    public void playVideoNative(String filePath) throws FileNotFoundException {
        vid = new VideoActor(VideoPlayerCreator.createVideoPlayer());
        if (vid != null) {
            vid.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            vid.setPosition(0, 0);
            vid.toFront();
            vid.getVideoPlayer().setOnCompletionListener(file -> onAttractEnd());
            add(vid);

            FileHandle videoFile = Gdx.files.internal(filePath);
            if (vid.getVideoPlayer().load(videoFile)) vid.getVideoPlayer().play();
        } else {
            Gdx.app.log("AttractState", "ALERT: Video is null! Could not play cutscene!");
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            holdDelta += delta;
            holdDelta = MathUtils.clamp(holdDelta, 0, HOLD_TIME);

            pie.setScale(MathUtils.lerp(pie.getScaleX(), 1.3f, (float) Math.exp(-delta * 140.0)));
        } else {
            holdDelta = MathUtils.lerp(holdDelta, -0.1f, MathUtils.clamp((delta * 3), 0, 1));
            holdDelta = MathUtils.clamp(holdDelta, 0, HOLD_TIME);
            pie.setScale(MathUtils.lerp(pie.getScaleX(), 1f, (float) Math.exp(-delta * 160.0)));
        }

        pie.setAmount(Math.min(1, Math.max(0, (holdDelta / HOLD_TIME) * 1.025f)));
        pie.setAlpha(MathUtils.map(0.025f, 1f, 0f, 1f, pie.getAmount()));

        // If the dial is full, skip the video.
        if (pie.getAmount() >= 1) onAttractEnd();
    }

    /**
     * When the attraction state ends (after the video ends or the user presses any button),
     * switch immediately to the title screen.
     */
    public void onAttractEnd() {
        if (vid != null) {
            vid.getVideoPlayer().stop();
            vid.remove();
            vid = null;
        }

        main.switchState(new TitleState(main));
    }

    @Override
    public void pause() {
        super.pause();
        vid.getVideoPlayer().pause();
    }

    @Override
    public void resume() {
        super.resume();
        vid.getVideoPlayer().resume();
    }

    public record VideoEntry(String path) { }
}
