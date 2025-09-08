package com.nova.fnfjava.play.song;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.data.IRegistryEntry;
import com.nova.fnfjava.data.song.SongData;
import com.nova.fnfjava.data.song.SongRegistry;
import com.nova.fnfjava.ui.freeplay.charselect.PlayableCharacter;
import com.nova.fnfjava.util.Constants;
import com.nova.fnfjava.util.SortUtil;

public class Song implements IRegistryEntry<SongData.SongMetadata> {
    public static final String DEFAULT_SONGNAME = "Unknown";

    public String id;
    public SongData.SongMetadata metadata;

    public final ObjectMap<String, SongData.SongMetadata> _metadata;

    public final ObjectMap<String, ObjectMap<String, SongDifficulty>> difficulties;

    public String songName;

    public Song(String id, SongRegistry.SongEntryParams params) {
        this.id = id;

        difficulties = new ObjectMap<>();
        _metadata = new ObjectMap<>();
    }

    @Override
    public void loadData(SongData.SongMetadata data) {
        if (data == null) throw new IllegalArgumentException("SongMetadata cannot be null");
        this.metadata = data;

        _metadata.clear();
        if (data != null) {
            _metadata.put(Constants.DEFAULT_VARIATION, data);
        } else {
            Main.logger.setTag(this.getClass().getSimpleName()).warn("Could not find song data for songId: " + id);
        }

        populateDifficulties();
    }

    public void populateDifficulties() {
        if (_metadata == null || _metadata.size == 0) return;

        for (SongData.SongMetadata metadata : _metadata.values()) {
            if (metadata == null || metadata.playData == null) continue;

            if (metadata.playData.difficulties.size == 0) {
                Main.logger.setTag(this.getClass().getSimpleName()).warn("Song " + id + " (variation " + metadata.variation + ") has no difficulties listed in metadata!");
                continue;
            }

            ObjectMap<String, SongDifficulty> difficultyMap = new ObjectMap<>();

            for (String diffId : metadata.playData.difficulties) {
                SongDifficulty difficulty = new SongDifficulty(this, diffId, metadata.variation);
                difficulty.songName = metadata.songName;
                difficulty.songArtist = metadata.artist;
                difficulty.charter = metadata.charter != null ? metadata.charter : Constants.DEFAULT_CHARTER;
                difficulty.timeFormat = metadata.timeFormat;
                difficulty.divisions = metadata.divisions;
                difficulty.timeChanges = metadata.timeChanges;
                difficulty.looped = metadata.looped;
                difficulty.generatedBy = metadata.generatedBy;
                difficulty.offsets = metadata.offsets != null ? metadata.offsets : new SongData.SongOffsets();

                difficulty.difficultyRating = metadata.playData.ratings.get(diffId) != null ? metadata.playData.ratings.get(diffId) : 0;
                difficulty.album = metadata.playData.album;
                difficulty.stickerPack = metadata.playData.stickerPack;

                difficulty.stage = metadata.playData.stage;
                difficulty.noteStyle = metadata.playData.noteStyle;

                difficulty.characters = metadata.playData.characters;
                difficultyMap.put(diffId, difficulty);
            }
            difficulties.put(metadata.variation, difficultyMap);
        }
    }

    public SongDifficulty getDifficulty(String diffId, String variation, Array<String> variations) {
        if (diffId == null) diffId = listDifficulties(variation, variations).get(0);
        if (variation == null) variation = Constants.DEFAULT_VARIATION;
        if (variations == null) variations = new Array<>(new String[]{variation});

        for (String currentVariation : variations) {
            Main.logger.setTag(this.getClass().getSimpleName()).debug(currentVariation);
            Main.logger.setTag(this.getClass().getSimpleName()).debug(difficulties.toString());
            if (difficulties.get(currentVariation) != null && difficulties.get(currentVariation).containsKey(diffId))
                return difficulties.get(currentVariation).get(diffId);
        }

        return null;
    }

    public SongDifficulty getDifficulty(String diffId, String variation) {
        return getDifficulty(diffId, variation, new Array<>(new String[]{variation}));
    }

    public String getFirstValidVariation(String diffId, PlayableCharacter currentCharacter, Array<String> possibleVariations) {
        if (possibleVariations == null) possibleVariations = getVariationsByCharacter(currentCharacter);

        if (diffId == null) diffId = listDifficulties(null, possibleVariations).get(0);

        for (String variationId : possibleVariations)
            if (difficulties.get(variationId) != null && difficulties.get(variationId).containsKey(diffId)) return variationId;

        return null;
    }

    public String getFirstValidVariation(String diffId) {
        return getFirstValidVariation(diffId, null, getVariationsByCharacter());
    }

    public Array<String> getVariationsByCharacter(PlayableCharacter character) {
        if (character == null) {
            Array<String> result = getVariations();
            result.sort(SortUtil.defaultsThenAlphabetically(Constants.DEFAULT_VARIATION_LIST));
            return result;
        }

        Array<String> result = new Array<>();
        for (String variation : getVariations()) {
            SongData.SongMetadata metadata = _metadata.get(variation);

            String playerCharId = metadata.playData.characters.player;
            if (playerCharId == null) continue;

            if (character.shouldShowCharacter(playerCharId)) result.add(variation);
        }

        result.sort(SortUtil.defaultsThenAlphabetically(Constants.DEFAULT_VARIATION_LIST));

        return result;
    }

    public Array<String> getVariationsByCharacter() {
        return getVariationsByCharacter(null);
    }

    public Array<String> listDifficulties(String variationId, Array<String> variationIds, boolean showLocked, boolean showHidden) {
        if (variationIds == null) variationIds = new Array<>();
        if (variationId != null) variationIds.add(variationId);

        if (variationIds.size == 0) return new Array<>();

        Array<String> diffFiltered = new Array<>();

        for (String variation : variationIds) {
            SongData.SongMetadata metadata = _metadata.get(variation);
            if (metadata != null && metadata.playData != null && metadata.playData.difficulties != null) {
                // Add all difficulties from this variation
                for (String difficulty : metadata.playData.difficulties) if (difficulty != null) diffFiltered.add(difficulty);
            }
        }

        Array<String> distinctDiffFiltered = new Array<>();
        for (String difficulty : diffFiltered)
            if (!distinctDiffFiltered.contains(difficulty, false)) distinctDiffFiltered.add(difficulty);

        return distinctDiffFiltered;
    }

    public Array<String> listDifficulties(String variationId, Array<String> variationIds) {
        return listDifficulties(variationId, variationIds, false, false);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public SongData.SongMetadata getData() {
        return metadata;
    }

    // Getters
    public Array<String> getVariations() {
        return _metadata.keys().toArray();
    }

    public String getSongName() {
        if (getData() != null) return getData().songName != null ? getData().songName : DEFAULT_SONGNAME;
        if (_metadata.size > 0) return _metadata.get(Constants.DEFAULT_VARIATION).songName != null ? _metadata.get(Constants.DEFAULT_VARIATION).songName : DEFAULT_SONGNAME;
        return DEFAULT_SONGNAME;
    }


    @Override
    public void destroy() {}

    @Override
    public String toString() {
        return "Song{" +
            "id='" + id + '\'' +
            ", metadata=" + metadata +
            '}';
    }

    public static class SongDifficulty {
        public final Song song;
        public final String difficulty;
        public final String variation;
        //public Array<SongNoteData> notes;
        //public Array<SongEventData> events;
        public String songName = Constants.DEFAULT_SONGNAME;
        public String songArtist = Constants.DEFAULT_ARTIST;
        public String charter = Constants.DEFAULT_CHARTER;
        public SongData.SongTimeFormat timeFormat = Constants.DEFAULT_TIMEFORMAT;
        public Integer divisions = null;
        public boolean looped = false;
        public SongData.SongOffsets offsets = new SongData.SongOffsets();
        public String generatedBy = SongRegistry.DEFAULT_GENERATEDBY;
        public Array<SongData.SongTimeChange> timeChanges = new Array<>();
        public String stage = Constants.DEFAULT_STAGE;
        public String noteStyle = Constants.DEFAULT_NOTE_STYLE;
        public SongData.SongCharacterData characters = null;
        public float scrollSpeed = Constants.DEFAULT_SCROLLSPEED;
        public int difficultyRating = 0;
        public String album = null;
        public String stickerPack = null;

        public SongDifficulty(Song song, String diffId, String variation) {
            this.song = song;
            this.difficulty = diffId;
            this.variation = variation;
        }

        @Override
        public String toString() {
            return "SongDifficulty{" +
                "song=" + song +
                ", difficulty='" + difficulty + '\'' +
                ", variation='" + variation + '\'' +
                ", songName='" + songName + '\'' +
                ", songArtist='" + songArtist + '\'' +
                ", charter='" + charter + '\'' +
                ", timeFormat=" + timeFormat +
                ", divisions=" + divisions +
                ", looped=" + looped +
                ", offsets=" + offsets +
                ", generatedBy='" + generatedBy + '\'' +
                ", timeChanges=" + timeChanges +
                ", stage='" + stage + '\'' +
                ", noteStyle='" + noteStyle + '\'' +
                ", characters=" + characters +
                ", scrollSpeed=" + scrollSpeed +
                ", difficultyRating=" + difficultyRating +
                ", album='" + album + '\'' +
                ", stickerPack='" + stickerPack + '\'' +
                '}';
        }
    }
}
