package com.nova.fnfjava.play.song;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.nova.fnfjava.Assets;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.Paths;
import com.nova.fnfjava.audio.FunkinSound;
import com.nova.fnfjava.audio.VoicesGroup;
import com.nova.fnfjava.data.IRegistryEntry;
import com.nova.fnfjava.data.song.SongData;
import com.nova.fnfjava.data.song.SongRegistry;
import com.nova.fnfjava.ui.freeplay.charselect.PlayableCharacter;
import com.nova.fnfjava.util.Constants;
import com.nova.fnfjava.util.SortUtil;

import java.util.Arrays;
import java.util.Objects;

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

    public void cacheCharts(boolean force) {
        if (force) clearCharts();

        Main.logger.setTag("Song").info("Caching " + getVariations().size + " chart files for song " + id);
        for (String variation : getVariations()) {
            SongData.SongChartData chart = SongRegistry.instance.parseEntryChartDataWithMigration(id, variation);
            if (chart == null) continue;
            applyChartData(chart, variation);
        }
        Main.logger.setTag("Song").info("Done caching charts.");
    }

    public void applyChartData(SongData.SongChartData chartData, String variation) {
        ObjectMap<String, Array<SongData.SongNoteData>> chartNotes = chartData.notes;
        for (String diffId : chartNotes.keys()) {
            SongDifficulty nullDiff = getDifficulty(diffId, variation);
            SongDifficulty difficulty = nullDiff != null ? nullDiff : new SongDifficulty(this, diffId, variation);
            if (nullDiff == null) {
                Main.logger.setTag("Song").info("Fabricated new difficulty for " + diffId + ".");
                SongData.SongMetadata metadata = _metadata.get(variation);
                difficulties.get(variation).put(diffId, difficulty);
                if (metadata != null) {
                    difficulty.songName = metadata.songName;
                    difficulty.songArtist = metadata.artist;
                    difficulty.charter = metadata.charter != null ? metadata.charter : Constants.DEFAULT_CHARTER;
                    difficulty.timeFormat = metadata.timeFormat;
                    difficulty.divisions = metadata.divisions;
                    difficulty.timeChanges = metadata.timeChanges;
                    difficulty.looped = metadata.looped;
                    difficulty.generatedBy = metadata.generatedBy;
                    difficulty.offsets = metadata.offsets != null ?  metadata.offsets : new SongData.SongOffsets();

                    difficulty.stage = metadata.playData.stage;
                    difficulty.noteStyle = metadata.playData.noteStyle;

                    difficulty.characters = metadata.playData.characters;
                }
            }
            difficulty.notes = chartNotes.get(diffId) != null ? chartNotes.get(diffId) : new Array<>();
            difficulty.scrollSpeed = chartData.getScrollSpeed(diffId) != null ? chartData.getScrollSpeed(diffId) : 1.0f;

            difficulty.events = chartData.events;
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

    public void clearCharts() {
        for (ObjectMap<String, SongDifficulty> variationMap : difficulties.values())
            for (SongDifficulty diff : variationMap.values())
                diff.clearChart();
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
        public Array<SongData.SongNoteData> notes;
        public Array<SongData.SongEventData> events;

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

        public void clearChart() {
            notes = null;
        }

        public String getInstPath(String instrumental) {
            if (characters != null) {
                if (!Objects.equals(instrumental, "") && characters.altInstrumentals.contains(instrumental, false)) {
                    var instId = "-" + instrumental;
                    return Paths.inst(this.song.id, instId);
                } else {
                    // Fallback to default instrumental.
                    var instId = !(characters.instrumental != null ? characters.instrumental : "").equals("") ? "-" + characters.instrumental : "";
                    return Paths.inst(this.song.id, instId);
                }
            } else return Paths.inst(this.song.id);
        }

        public void cacheInst(String instrumental) {
            Assets.cacheSound(getInstPath(instrumental));
        }

        public void cacheVocals() {
            for (String voice : buildVoiceList()) {
                Main.logger.setTag("SongDifficulty").info("Caching vocal track: $voice");
                Assets.cacheSound(voice);
            }
        }

        public Array<String> buildVoiceList() {
            Array<String> result = new Array<>();
            result.addAll(buildPlayerVoiceList());
            result.addAll(buildOpponentVoiceList());
            if (result.size == 0) {
                String suffix = (variation != null && !variation.equals("") && !variation.equals("default")) ? "-" + variation : "";
                // Try to use `Voices.ogg` if no other voices are found.
                if (Assets.exists(Paths.voices(this.song.id, ""))) result.add(Paths.voices(this.song.id, suffix));
            }
            return result;
        }

        public Array<String> buildPlayerVoiceList() {
            String suffix = (variation != null && !variation.equals("") && !variation.equals("default")) ? "-" + variation : "";

            // Automatically resolve voices by removing suffixes.
            // For example, if `Voices-bf-car-erect.ogg` does not exist, check for `Voices-bf-erect.ogg`.
            // Then, check for  `Voices-bf-car.ogg`, then `Voices-bf.ogg`.

            if (characters.playerVocals == null) {
                String playerId = characters.player;
                String playerVoice = Paths.voices(this.song.id, "-" + playerId + suffix);

                while (playerVoice != null && !Assets.exists(playerVoice)) {
                    // Remove the last suffix.
                    // For example, bf-car becomes bf.
                    playerId = removeLastSegment(playerId);
                    // Try again.
                    playerVoice = playerId.isEmpty() ? null : Paths.voices(this.song.id, "-" + playerId + suffix);
                }
                if (playerVoice == null) {
                    // Try again without $suffix.
                    playerId = characters.player;
                    playerVoice = Paths.voices(this.song.id, "-" + playerId);
                    while (playerVoice != null && !Assets.exists(playerVoice)) {
                        // Remove the last suffix.
                        playerId = removeLastSegment(playerId);
                        // Try again.
                        playerVoice = playerId == "" ? null : Paths.voices(this.song.id, "-" + playerId + suffix);
                    }
                }

                return playerVoice != null ? new Array<>(new String[]{playerVoice}) : new Array<>();
            } else {
                // The metadata explicitly defines the list of voices.
                Array<String> playerIds = characters.playerVocals != null ? characters.playerVocals : new Array<>(new String[]{characters.player});
                Array<String> playerVoices = new Array<>();
                for (String id : playerIds) playerVoices.add(Paths.voices(this.song.id, "-" + id + suffix));

                return playerVoices;
            }
        }

        public Array<String> buildOpponentVoiceList() {
            String suffix = (variation != null && !variation.equals("") && !variation.equals("default")) ? "-" + variation : "";

            // Automatically resolve voices by removing suffixes.
            // For example, if `Voices-bf-car-erect.ogg` does not exist, check for `Voices-bf-erect.ogg`.
            // Then, check for  `Voices-bf-car.ogg`, then `Voices-bf.ogg`.

            if (characters.opponentVocals == null) {
                String opponentId = characters.opponent;
                String opponentVoice = Paths.voices(this.song.id, "-" + opponentId + suffix);
                while (opponentVoice != null && !Assets.exists(opponentVoice)) {
                    // Remove the last suffix.
                    opponentId = removeLastSegment(opponentId);
                    // Try again.
                    opponentVoice = opponentId == "" ? null : Paths.voices(this.song.id, "-${opponentId}$suffix");
                }
                if (opponentVoice == null) {
                    // Try again without $suffix.
                    opponentId = characters.opponent;
                    opponentVoice = Paths.voices(this.song.id, "-${opponentId}");
                    while (opponentVoice != null && !Assets.exists(opponentVoice)) {
                        // Remove the last suffix.
                        opponentId = removeLastSegment(opponentId);
                        // Try again.
                        opponentVoice = opponentId == "" ? null : Paths.voices(this.song.id, "-" + opponentId + suffix);
                    }
                }

                return opponentVoice != null ? new Array<>(new String[]{opponentVoice}) : new Array<>();
            } else {
                // The metadata explicitly defines the list of voices.
                Array<String> opponentIds = characters.opponentVocals != null ? characters.opponentVocals : new Array<>(new String[]{characters.opponent});

                Array<String> opponentVoices = new Array<>();
                for (String id : opponentIds) opponentVoices.add(Paths.voices(this.song.id, "-" + id + suffix));

                return opponentVoices;
            }
        }

        public VoicesGroup buildVocals(String instId) {
            VoicesGroup result = new VoicesGroup();

            Array<String> playerVoiceList = this.buildPlayerVoiceList();
            Array<String> opponentVoiceList = this.buildOpponentVoiceList();

            /*for (String playerVoice : playerVoiceList) {
                if (!Assets.exists(playerVoice)) continue;
                result.addPlayerVoice(FunkinSound.load(playerVoice, 1.0, false, false, false, false, null, null, true));
            }

            for (String opponentVoice : opponentVoiceList) {
                if (!Assets.exists(opponentVoice)) continue;
                result.addOpponentVoice(FunkinSound.load(opponentVoice, 1.0, false, false, false, false, null, null, true));
            }

            result.forEach(function(snd:FunkinSound) {
                snd.important = true;
            });

            result.playerVoicesOffset = offsets.getVocalOffset(characters.player, instId);
            result.opponentVoicesOffset = offsets.getVocalOffset(characters.opponent, instId);

            return result;*/
        }

        public String removeLastSegment(String input) {
            String[] parts = input.split("-");
            if (parts.length > 1) {
                return String.join("-", Arrays.copyOfRange(parts, 0, parts.length - 1));
            }
            return "";
        }
    }
}
