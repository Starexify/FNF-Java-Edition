package com.nova.fnfjava.play;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.nova.fnfjava.Conductor;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.data.notestyle.NoteStyleRegistry;
import com.nova.fnfjava.play.notes.notestyle.NoteStyle;
import com.nova.fnfjava.util.Constants;

public class Countdown {
    public static CountdownStep countdownStep = CountdownStep.BEFORE;
    public static NoteStyle noteStyle;
    public static Timer countdownTimer = null;

    public static boolean performCountdown() {
        countdownStep = CountdownStep.BEFORE;
        //boolean cancelled = propagateCountdownEvent(countdownStep);
        //if (cancelled) return false;

        stopCountdown();

        PlayState.instance.isInCountdown = true;
        Conductor.getInstance().update(PlayState.instance.startTimestamp + Conductor.getInstance().getBeatLengthMs() * -5);

        countdownTimer = new Timer();
        countdownTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if (PlayState.instance == null) {
                    cancel();
                    return;
                }

                countdownStep = decrement(countdownStep);

                // Countdown graphic.
                showCountdownGraphic(countdownStep);

                // Countdown sound.
                playCountdownSound(countdownStep);

                //var cancelled = propagateCountdownEvent(countdownStep);
                //  if (cancelled) pauseCountdown();

                if (countdownStep == CountdownStep.AFTER) stopCountdown();

            }
        }, Conductor.getInstance().getBeatLengthMs() / 1000, 0, 4);

        return true;
    }

    public static void fetchNoteStyle(String noteStyleId, boolean force) {
        if (noteStyle != null && !force) return;

        if (noteStyleId == null) noteStyleId = PlayState.instance.getCurrentChart().noteStyle;

        noteStyle = NoteStyleRegistry.instance.fetchEntry(noteStyleId);
        if (noteStyle == null) noteStyle = NoteStyleRegistry.instance.fetchDefault();
    }

    public static void fetchNoteStyle() {
        fetchNoteStyle(null, false);
    }

    public static void showCountdownGraphic(CountdownStep index) {
        fetchNoteStyle();

        var countdownSprite = noteStyle.buildCountdownSprite(index);
        if (countdownSprite == null) return;

/*        var fadeEase = FlxEase.cubeInOut;
        if (noteStyle.isCountdownSpritePixel(index)) fadeEase = EaseUtil.stepped(8);

        FlxTween.tween(countdownSprite, {alpha: 0}, Conductor.instance.beatLengthMs / 1000,
            {
                ease: fadeEase,
            onComplete: function(twn:FlxTween) {
            countdownSprite.destroy();
        }
      });*/
        //countdownSprite.cameras = [PlayState.instance.camHUD];
        PlayState.instance.add(countdownSprite);
        countdownSprite.screenCenter();

        Array<Float> offsets = noteStyle.getCountdownSpriteOffsets(index);
        countdownSprite.addX(offsets.get(0));
        countdownSprite.addY(offsets.get(1));
    }

    public static void stopCountdown() {
        if (countdownTimer != null) {
            countdownTimer.clear();
            countdownTimer = null;
        }
    }

    public static void playCountdownSound(CountdownStep step) {
        fetchNoteStyle();
        String path = noteStyle.getCountdownSoundPath(step);
        if (path == null) return;

        Main.sound.playOnce(path, Constants.COUNTDOWN_VOLUME);
    }

    public static CountdownStep decrement(CountdownStep step) {
        switch (step) {
            case BEFORE: return CountdownStep.THREE;
            case THREE: return CountdownStep.TWO;
            case TWO: return CountdownStep.ONE;
            case ONE: return CountdownStep.GO;
            case GO: return CountdownStep.AFTER;

            default: return CountdownStep.AFTER;
        }
    }

    public enum CountdownStep {
        BEFORE,
        THREE,
        TWO,
        ONE,
        GO,
        AFTER;
    }
}
