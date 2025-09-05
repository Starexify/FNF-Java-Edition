package com.nova.fnfjava.save;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.nova.fnfjava.Main;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Save {
    public static Save instance;
    public RawSaveData data;

    public static final String SAVE_DATA_VERSION = "1.0.0";

    public static final String SAVE_PATH = "FunkinCrew";
    public static final String SAVE_NAME = "Funkin";
    public static Kryo kryo;

    static {
        setupKryo();
    }

    private static void setupKryo() {
        kryo = new Kryo();
        kryo.register(Save.class);
        kryo.register(RawSaveData.class);
        kryo.register(SaveHighScoresData.class);
        kryo.register(SaveScoreData.class);
        kryo.register(SaveScoreTallyData.class);

        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
    }

    public Save(RawSaveData data) {
        if (data == null) this.data = Save.getDefault();
        else this.data = data;
    }

    public Save() {
        this(null);
    }

    public static Save getInstance() {
        if (instance == null) return load();
        return instance;
    }

    public static Save load() {
        Main.logger.setTag("SAVE").info("Loading save...");
        Save loadedSave = new Save();
        if (instance == null) instance = loadedSave;
        return loadedSave;
    }

    public static RawSaveData getDefault() {
        return new RawSaveData();
    }

    public void flush() {
        try (Output output = new Output(new FileOutputStream(getSaveFilePath()))) {
            kryo.writeObject(output, this);
            Main.logger.setTag("SAVE").info("Save flushed successfully");
        } catch (Exception e) {
            Main.logger.setTag("SAVE").error("Failed to flush save", e);
        }
    }

    public static String getSaveFilePath() {
        return Gdx.files.local(SAVE_NAME + ".dat").file().getAbsolutePath();
    }

    /**
     * Return the score the user achieved for a given level on a given difficulty.
     *
     * @param levelId      The ID of the level/week.
     * @param difficultyId The difficulty to check.
     * @return A data structure containing score, judgement counts, and accuracy. Returns null if no score is saved.
     */
    public SaveScoreData getLevelScore(String levelId, String difficultyId) {
        if (data.scores == null) data.scores = new SaveHighScoresData();
        if (data.scores.levels == null) data.scores.levels = new HashMap<>();

        Map<String, SaveScoreData> level = data.scores.levels.get(levelId);
        if (level == null) {
            level = new HashMap<>();
            data.scores.levels.put(levelId, level);
        }
        return level.get(difficultyId);
    }

    /**
     * Apply the score the user achieved for a given level on a given difficulty.
     */
    public void setLevelScore(String levelId, String difficultyId, SaveScoreData score) {
        if (data.scores == null) data.scores = new SaveHighScoresData();
        if (data.scores.levels == null) data.scores.levels = new HashMap<>();

        Map<String, SaveScoreData> level = data.scores.levels.get(levelId);
        if (level == null) {
            level = new HashMap<>();
            data.scores.levels.put(levelId, level);
        }
        level.put(difficultyId, score);

        flush();
    }

    public static class RawSaveData {
        public SaveHighScoresData scores = new SaveHighScoresData();

        public RawSaveData() {
        }
    }

    public static class SaveHighScoresData {
        public Map<String, Map<String, SaveScoreData>> songs = new HashMap<>();
        public Map<String, Map<String, SaveScoreData>> levels = new HashMap<>();
    }

    public record SaveScoreData(int score, SaveScoreTallyData tallies, long timestamp) {
        public SaveScoreData(int score, SaveScoreTallyData tallies) {
            this(score, tallies, System.currentTimeMillis());
        }

        public SaveScoreData() {
            this(0, new SaveScoreTallyData(), 0L);
        }
    }

    public record SaveScoreTallyData(int sick, int good, int bad, int shit, int missed, int combo, int maxCombo, int totalNotesHit, int totalNotes) {
        public SaveScoreTallyData() {
            this(0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
    }
}
