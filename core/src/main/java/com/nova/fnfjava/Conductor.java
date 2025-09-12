package com.nova.fnfjava;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.data.song.SongData;
import com.nova.fnfjava.data.song.SongDataUtils;
import com.nova.fnfjava.util.Constants;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Conductor {
    public static Conductor _instance;

    public static Signal<Integer> measureHit = new Signal<>();
    public Signal<Integer> onMeasureHit = new Signal<>();
    public static Signal<Integer> beatHit = new Signal<>();
    public Signal<Integer> onBeatHit = new Signal<>();
    public static Signal<Integer> stepHit = new Signal<>();
    public Signal<Integer> onStepHit = new Signal<>();

    public Array<SongData.SongTimeChange> timeChanges = new Array<>();
    public SongData.SongTimeChange currentTimeChange;

    public float songPosition = 0f;
    public float songPositionDelta = 0f;
    public float prevTimestamp = 0f;
    public float prevTime = 0f;

    public Float bpmOverride = null;

    public int currentMeasure = 0;
    public int currentBeat = 0;
    public int currentStep = 0;

    public float currentMeasureTime = 0f;
    public float currentBeatTime = 0f;
    public float currentStepTime = 0f;

    public float instrumentalOffset = 0f;
    public float formatOffset = 0f;

    public Conductor() {
    }

    public void forceBPM(Float bpm) {
        if (bpm != null) Main.logger.setTag(getClass().getSimpleName()).info("Forcing BPM to " + bpm);
        else Main.logger.setTag(getClass().getSimpleName()).info("Resetting BPM to default");

        this.bpmOverride = bpm;
    }

    public static Conductor getInstance() {
        if (_instance == null) setInstance(new Conductor());
        return _instance;
    }

    public static void setInstance(Conductor instance) {
        // Clear old instance first
        if (_instance != null) {
            clearSingleton(_instance);
        }

        _instance = instance;

        // Set up new instance
        if (_instance != null) {
            setupSingleton(_instance);
        }
    }

    public static void setupSingleton(Conductor input) {
        input.onMeasureHit.add(Conductor::dispatchMeasureHit);
        input.onBeatHit.add(Conductor::dispatchBeatHit);
        input.onStepHit.add(Conductor::dispatchStepHit);
    }

    public static void clearSingleton(Conductor input) {
        input.onMeasureHit.remove(Conductor::dispatchMeasureHit);
        input.onBeatHit.remove(Conductor::dispatchBeatHit);
        input.onStepHit.remove(Conductor::dispatchStepHit);
    }

    public static void dispatchMeasureHit(Signal<Integer> signal, Integer measure) {
        measureHit.dispatch(measure);
    }

    public static void dispatchBeatHit(Signal<Integer> signal, Integer beat) {
        beatHit.dispatch(beat);
    }

    public static void dispatchStepHit(Signal<Integer> signal, Integer step) {
        stepHit.dispatch(step);
    }

    public void update(Float songPos, boolean applyOffsets) {
        float currentTime = Main.sound.isPlaying() ? Main.sound.getTime() : 0.0f;
        float currentLength = Main.sound.isPlaying() ? Main.sound.getLength() : 0.0f;
        if (songPos == null) songPos = currentTime;

        songPos += applyOffsets ? getCombinedOffset() : 0;

        float oldMeasure = this.currentMeasure;
        float oldBeat = this.currentBeat;
        float oldStep = this.currentStep;

        if (Main.sound.isPlaying()) {
            this.songPosition = MathUtils.clamp(Math.min(getCombinedOffset(), 0), songPos, currentLength);
            this.songPositionDelta += Gdx.graphics.getDeltaTime() * 1000f * Main.sound.getPitch();
        } else {
            this.songPosition = songPos;
        }

        currentTimeChange = timeChanges.size > 0 ? timeChanges.get(0) : null;

        if (this.songPosition > 0.0f) {
            for (int i = 0; i < timeChanges.size; i++) {
                if (this.songPosition >= timeChanges.get(i).timeStamp) {
                    currentTimeChange = timeChanges.get(i);
                }
                if (this.songPosition < timeChanges.get(i).timeStamp) break;
            }
        }

        if (currentTimeChange == null && bpmOverride == null && Main.sound.isPlaying()) {
            Main.logger.setTag(this.getClass().getSimpleName()).warn("Conductor is broken, timeChanges is empty.");
        } else if (currentTimeChange != null && this.songPosition > 0.0f) {
            float songPositionMs = this.songPosition * 1000f;
            this.currentStepTime = roundDecimal((currentTimeChange.beatTime * Constants.STEPS_PER_BEAT) +
                (songPositionMs - currentTimeChange.timeStamp) / getStepLengthMs(), 6);
            this.currentBeatTime = currentStepTime / Constants.STEPS_PER_BEAT;
            this.currentMeasureTime = currentStepTime / getStepsPerMeasure();
            this.currentStep = (int) Math.floor(currentStepTime);
            this.currentBeat = (int) Math.floor(currentBeatTime);
            this.currentMeasure = (int) Math.floor(currentMeasureTime);
        } else {
            float songPositionMs = this.songPosition * 1000f;
            this.currentStepTime = songPositionMs / getStepLengthMs();
            this.currentBeatTime = currentStepTime / Constants.STEPS_PER_BEAT;
            this.currentMeasureTime = currentStepTime / getStepsPerMeasure();
            this.currentStep = (int) Math.floor(currentStepTime);
            this.currentBeat = (int) Math.floor(currentBeatTime);
            this.currentMeasure = (int) Math.floor(currentMeasureTime);
        }

        // Dispatch signals if changed
        if (currentStep != oldStep) this.onStepHit.dispatch(currentStep);
        if (currentBeat != oldBeat) this.onBeatHit.dispatch(currentBeat);
        if (currentMeasure != oldMeasure) this.onMeasureHit.dispatch(currentMeasure);

        // Only update the timestamp if songPosition actually changed
        if (prevTime != this.songPosition) {
            this.songPositionDelta = 0;
            // Update the timestamp for use in-between frames
            prevTime = this.songPosition;
            prevTimestamp = System.currentTimeMillis();
        }
    }

    public void update(Float songPos) {
        update(songPos, true);
    }

    public void update() {
        update(null, true);
    }

    public void mapTimeChanges(Array<SongData.SongTimeChange> songTimeChanges) {
        timeChanges = new Array<>();

        SongDataUtils.sortTimeChanges(songTimeChanges);

        for (SongData.SongTimeChange songTimeChange : songTimeChanges) {
            // TODO: Maybe handle this different?
            // Do we care about BPM at negative timestamps?
            if (songTimeChange.timeStamp < 0.0f) songTimeChange.timeStamp = 0.0f;

            if (songTimeChange.timeStamp <= 0.0f) {
                songTimeChange.beatTime = 0.0f;
            } else {
                // Calculate the beat time of this timestamp
                songTimeChange.beatTime = 0.0f;

                if (songTimeChange.timeStamp > 0.0f && timeChanges.size > 0) {
                    SongData.SongTimeChange prevTimeChange = timeChanges.get(timeChanges.size - 1);
                    songTimeChange.beatTime = roundDecimal(
                        prevTimeChange.beatTime +
                            ((songTimeChange.timeStamp - prevTimeChange.timeStamp) * prevTimeChange.bpm / Constants.SECS_PER_MIN / Constants.MS_PER_SEC),
                        4
                    );
                }
            }

            timeChanges.add(songTimeChange);
        }

        if (timeChanges.size > 0) Main.logger.setTag(this.getClass().getSimpleName()).info("Done mapping time changes: " + timeChanges);

        this.update(this.songPosition, false);
    }

    public float getTimeInSteps(float ms) {
        if (timeChanges.size == 0) {
            // Assume a constant BPM equal to the forced value.
            return (float) Math.floor(ms / getStepLengthMs());
        } else {
            float resultStep = 0f;

            SongData.SongTimeChange lastTimeChange = timeChanges.get(0);
            for (SongData.SongTimeChange timeChange : timeChanges) {
                if (ms >= timeChange.timeStamp) {
                    lastTimeChange = timeChange;
                    resultStep = lastTimeChange.beatTime * Constants.STEPS_PER_BEAT;
                } else {
                    // This time change is after the requested time.
                    break;
                }
            }

            float lastStepLengthMs = ((Constants.SECS_PER_MIN / lastTimeChange.bpm) * Constants.MS_PER_SEC) / getTimeSignatureNumerator();
            float resultFractionalStep = (ms - lastTimeChange.timeStamp) / lastStepLengthMs;
            resultStep += resultFractionalStep;

            return resultStep;
        }
    }

    public float getBPM() {
        if (bpmOverride != null) return bpmOverride;
        if (currentTimeChange == null) return Constants.DEFAULT_BPM;
        return currentTimeChange.bpm;
    }

    public float getStartingBPM() {
        if (bpmOverride != null) return bpmOverride;
        var timeChange = timeChanges.get(0);
        if (timeChange == null) return Constants.DEFAULT_BPM;
        return timeChange.bpm;
    }

    public float getMeasureLength() {
        return getBeatLengthMs() * getTimeSignatureNumerator();
    }

    public float getBeatLengthMs() {
        return ((Constants.SECS_PER_MIN / getBPM()) * Constants.MS_PER_SEC);
    }

    public float getStepLengthMs() {
        return getBeatLengthMs() / getTimeSignatureNumerator();
    }

    public int getTimeSignatureNumerator() {
        if (currentTimeChange == null) return Constants.DEFAULT_TIME_SIGNATURE_NUM;
        return currentTimeChange.timeSignatureNum;
    }

    public int getTimeSignatureDenominator() {
        if (currentTimeChange == null) return Constants.DEFAULT_TIME_SIGNATURE_DEN;
        return currentTimeChange.timeSignatureDen;
    }

    public float getInstrumentalOffsetSteps() {
        float startingStepLengthMs = ((Constants.SECS_PER_MIN / getStartingBPM()) * Constants.MS_PER_SEC) / getTimeSignatureNumerator();
        return instrumentalOffset / startingStepLengthMs;
    }

    public float getCombinedOffset() {
        return instrumentalOffset + formatOffset + Preferences.getGlobalOffset();
    }

    public int getStepsPerMeasure() {
        // TODO: Is this always an integer?
        return getTimeSignatureNumerator() * Constants.STEPS_PER_BEAT;
    }

    public float roundDecimal(float value, int decimals) {
        return BigDecimal.valueOf(value).setScale(decimals, RoundingMode.HALF_UP).floatValue();
    }
}
