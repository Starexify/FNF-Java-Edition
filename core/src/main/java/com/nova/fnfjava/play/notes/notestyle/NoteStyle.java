package com.nova.fnfjava.play.notes.notestyle;

import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.Paths;
import com.nova.fnfjava.data.IRegistryEntry;
import com.nova.fnfjava.data.notestyle.NoteStyleData;
import com.nova.fnfjava.data.notestyle.NoteStyleRegistry;
import com.nova.fnfjava.graphics.FunkinSprite;
import com.nova.fnfjava.play.Countdown;
import com.nova.fnfjava.play.notes.NoteDirection;
import com.nova.fnfjava.util.Constants;

public class NoteStyle implements IRegistryEntry<NoteStyleData> {
    public String id;
    public NoteStyleData noteStyleData;

    public NoteStyle(String id, NoteStyleRegistry.NoteStyleEntryParams params) {
        this.id = id;
    }

    public String getNoteAssetPath(boolean raw) {
        if (raw) {
            String rawPath = getData().assets.note.assetPath;
            if (rawPath == null) return getFallback().getNoteAssetPath(true);
            return rawPath;
        }

        String[] parts = getNoteAssetPath(true).split(Constants.LIBRARY_SEPARATOR);
        if (parts.length == 0) return null;
        if (parts.length == 1) return getNoteAssetPath(true);
        return parts[1];
    }

    public String getNoteAssetPath() {
        return getNoteAssetPath(false);
    }


    public String getHoldNoteAssetPath(boolean raw) {
        if (raw) {
            String rawPath = getData().assets.holdNote.assetPath;
            if (rawPath == null) return getFallback().getHoldNoteAssetPath(true);
            return rawPath;
        }

        String[] parts = getHoldNoteAssetPath(true).split(Constants.LIBRARY_SEPARATOR);
        if (parts.length == 0) return null;
        if (parts.length == 1) return Paths.image(parts[0]);
        return Paths.image(parts[1], parts[0]);
    }

    public String getHoldNoteAssetPath() {
        return getHoldNoteAssetPath(false);
    }


    public String getStrumlineAssetPath(boolean raw) {
        if (raw) return getData().assets.noteStrumline.assetPath != null ? getData().assets.noteStrumline.assetPath : getFallback().getStrumlineAssetPath(true);

        String[] parts = getStrumlineAssetPath(true).split(Constants.LIBRARY_SEPARATOR);
        if (parts.length <= 1) return getStrumlineAssetPath(true);
        return parts[1];
    }

    public String getStrumlineAssetPath() {
        return getStrumlineAssetPath(false);
    }


    public String getSplashAssetPath(boolean raw) {
        if (raw) {
            String rawPath = getData().assets.noteSplash.assetPath;
            if (rawPath == null) return getFallback().getSplashAssetPath(true);
            return rawPath;
        }

        String[] parts = getSplashAssetPath(true).split(Constants.LIBRARY_SEPARATOR);
        if (parts.length == 0) return null;
        if (parts.length == 1) return getSplashAssetPath(true);
        return parts[1];
    }

    public String getSplashAssetPath() {
        return getSplashAssetPath(false);
    }


    public String getHoldCoverRootAssetPath(NoteDirection direction, boolean raw) {
        if (raw) {
            String rawPath = getData().assets.holdNoteCover.assetPath;
            // remember, if we need a fallback for our *root* asset path,
            // we fallback and look for the *direction* asset path first
            if (rawPath == null) return getFallback().getHoldCoverDirectionAssetPath(direction, true);
            return rawPath;
        }

        String[] parts = getHoldCoverRootAssetPath(direction, true).split(Constants.LIBRARY_SEPARATOR);
        if (parts.length == 0) return null;
        if (parts.length == 1) return getHoldCoverRootAssetPath(direction, true);
        return parts[1];
    }

    public String getHoldCoverDirectionAssetPath(NoteDirection direction, boolean raw) {
        if (raw) {
            String rawPath = switch (direction) {
                case LEFT -> getData().assets.holdNoteCover.data.left.assetPath;
                case DOWN -> getData().assets.holdNoteCover.data.down.assetPath;
                case UP -> getData().assets.holdNoteCover.data.up.assetPath;
                case RIGHT -> getData().assets.holdNoteCover.data.right.assetPath;
            };

            if (rawPath == null) return getHoldCoverRootAssetPath(direction, true);
            return rawPath;
        }

        String[] parts = getHoldCoverDirectionAssetPath(direction, true).split(Constants.LIBRARY_SEPARATOR);
        if (parts.length == 0) return null;
        if (parts.length == 1) return getHoldCoverDirectionAssetPath(direction, true);
        return parts[1];
    }

    public String getHoldCoverDirectionAssetPath(NoteDirection noteDirection) {
        return getHoldCoverDirectionAssetPath(noteDirection, false);
    }


    public FunkinSprite buildCountdownSprite(Countdown.CountdownStep step) {
        var result = new FunkinSprite();
        switch (step) {
            case THREE:
                if (getData().assets.countdownThree == null) return getFallback().buildCountdownSprite(step);
                String assetPath3 = buildCountdownSpritePath(step);
                if (assetPath3 == null) return null;
                result.loadTexture(assetPath3);
                result.setScaleX(getData().assets.countdownThree.scale != null ? getData().assets.countdownThree.scale : 1.0f);
                result.setScaleY(getData().assets.countdownThree.scale != null ? getData().assets.countdownThree.scale : 1.0f);
                break;

            case TWO:
                if (getData().assets.countdownTwo == null) return getFallback().buildCountdownSprite(step);
                String assetPath2 = buildCountdownSpritePath(step);
                if (assetPath2 == null) return null;
                result.loadTexture(assetPath2);
                result.setScaleX(getData().assets.countdownTwo.scale != null ? getData().assets.countdownTwo.scale : 1.0f);
                result.setScaleY(getData().assets.countdownTwo.scale != null ? getData().assets.countdownTwo.scale : 1.0f);
                break;

            case ONE:
                if (getData().assets.countdownOne == null) return getFallback().buildCountdownSprite(step);
                String assetPath1 = buildCountdownSpritePath(step);
                if (assetPath1 == null) return null;
                result.loadTexture(assetPath1);
                result.setScaleX(getData().assets.countdownOne.scale != null ? getData().assets.countdownOne.scale : 1.0f);
                result.setScaleY(getData().assets.countdownOne.scale != null ? getData().assets.countdownOne.scale : 1.0f);
                break;

            case GO:
                if (getData().assets.countdownGo == null) return getFallback().buildCountdownSprite(step);
                String assetPathGo = buildCountdownSpritePath(step);
                if (assetPathGo == null) return null;
                result.loadTexture(assetPathGo);
                result.setScaleX(getData().assets.countdownGo.scale != null ? getData().assets.countdownGo.scale : 1.0f);
                result.setScaleY(getData().assets.countdownGo.scale != null ? getData().assets.countdownGo.scale : 1.0f);
                break;

            default:
                return null;
        }

        //result.scrollFactor.set(0, 0);
        //result.antialiasing = !isCountdownSpritePixel(step);
        result.updateHitbox();

        return result;
    }

    public String buildCountdownSpritePath(Countdown.CountdownStep step) {
        String basePath = switch (step) {
            case THREE -> getData().assets.countdownThree.assetPath;
            case TWO -> getData().assets.countdownTwo.assetPath;
            case ONE -> getData().assets.countdownOne.assetPath;
            case GO -> getData().assets.countdownGo.assetPath;
            default -> null;
        };

        if (basePath == null) return getFallback().buildCountdownSpritePath(step);

        String[] parts = basePath.split(Constants.LIBRARY_SEPARATOR);
        if (parts.length < 1) return null;
        if (parts.length == 1) return parts[0];
        return parts[1];
    }

    public Boolean isCountdownSpritePixel(Countdown.CountdownStep step) {
        switch (step) {
            case THREE:
                Boolean result3 = getData().assets.countdownThree.isPixel;
                if (result3 == null) result3 = getFallback().isCountdownSpritePixel(step) != null ? getFallback().isCountdownSpritePixel(step) : false;
                return result3;
            case TWO:
                Boolean result2 = getData().assets.countdownTwo.isPixel;
                if (result2 == null) result2 = getFallback().isCountdownSpritePixel(step) != null ? getFallback().isCountdownSpritePixel(step) : false;
                return result2;
            case ONE:
                Boolean result1 = getData().assets.countdownOne.isPixel;
                if (result1 == null) result1 = getFallback().isCountdownSpritePixel(step) != null ? getFallback().isCountdownSpritePixel(step) : false;
                return result1;
            case GO:
                Boolean resultGo = getData().assets.countdownGo.isPixel;
                if (resultGo == null) resultGo = getFallback().isCountdownSpritePixel(step) != null ? getFallback().isCountdownSpritePixel(step) : false;
                return resultGo;
            default:
                return false;
        }
    }

    public Array<Float> getCountdownSpriteOffsets(Countdown.CountdownStep step) {
        switch (step) {
            case THREE:
                var result3 = getData().assets.countdownThree.offsets;
                if (result3 == null) result3 = getFallback().getCountdownSpriteOffsets(step) != null ? getFallback().getCountdownSpriteOffsets(step) : new Array<>(new Float[]{0f, 0f});
                return result3;
            case TWO:
                var result2 = getData().assets.countdownTwo.offsets;
                if (result2 == null) result2 = getFallback().getCountdownSpriteOffsets(step) != null ? getFallback().getCountdownSpriteOffsets(step) : new Array<>(new Float[]{0f, 0f});
                return result2;
            case ONE:
                var result1 = getData().assets.countdownOne.offsets;
                if (result1 == null) result1 = getFallback().getCountdownSpriteOffsets(step) != null ? getFallback().getCountdownSpriteOffsets(step) : new Array<>(new Float[]{0f, 0f});
                return result1;
            case GO:
                var result = getData().assets.countdownGo.offsets;
                if (result == null) result = getFallback().getCountdownSpriteOffsets(step) != null ? getFallback().getCountdownSpriteOffsets(step) : new Array<>(new Float[]{0f, 0f});
                return result;
            default:
                return new Array<>(new Float[]{0f, 0f});
        }
    }

    public String getCountdownSoundPath(Countdown.CountdownStep step, boolean raw) {
        if (raw) {
            String rawPath = switch (step) {
                case THREE -> getData().assets.countdownThree.data.audioPath;
                case TWO -> getData().assets.countdownTwo.data.audioPath;
                case ONE -> getData().assets.countdownOne.data.audioPath;
                case GO -> getData().assets.countdownGo.data.audioPath;
                default -> null;
            };
            return (rawPath == null) ? getFallback().getCountdownSoundPath(step, true) : rawPath;
        }

        String[] parts = getCountdownSoundPath(step, true).split(Constants.LIBRARY_SEPARATOR);
        if (parts.length == 0) return null;
        if (parts.length == 1) return Paths.image(parts[0]);
        return Paths.sound(parts[1], parts[0]);
    }

    public String getCountdownSoundPath(Countdown.CountdownStep step) {
        return getCountdownSoundPath(step, false);
    }


    public String buildJudgementSpritePath(String rating) {
        String basePath = switch (rating) {
            case "sick" -> getData().assets.judgementSick.assetPath;
            case "good" -> getData().assets.judgementGood.assetPath;
            case "bad" -> getData().assets.judgementBad.assetPath;
            case "shit" -> getData().assets.judgementShit.assetPath;
            default -> null;
        };
        if (basePath == null) return getFallback().buildJudgementSpritePath(rating);

        String[] parts = basePath.split(Constants.LIBRARY_SEPARATOR);
        if (parts.length < 1) return null;
        if (parts.length == 1) return parts[0];
        return parts[1];
    }

    public String buildComboNumSpritePath(int digit) {
        String basePath = switch (digit) {
            case 0 -> getData().assets.comboNumber0.assetPath;
            case 1 -> getData().assets.comboNumber1.assetPath;
            case 2 -> getData().assets.comboNumber2.assetPath;
            case 3 -> getData().assets.comboNumber3.assetPath;
            case 4 -> getData().assets.comboNumber4.assetPath;
            case 5 -> getData().assets.comboNumber5.assetPath;
            case 6 -> getData().assets.comboNumber6.assetPath;
            case 7 -> getData().assets.comboNumber7.assetPath;
            case 8 -> getData().assets.comboNumber8.assetPath;
            case 9 -> getData().assets.comboNumber9.assetPath;
            default -> null;
        };

        if (basePath == null) return getFallback().buildComboNumSpritePath(digit);

        String[] parts = basePath.split(Constants.LIBRARY_SEPARATOR);
        if (parts.length < 1) return null;
        if (parts.length == 1) return parts[0];
        return parts[1];
    }


    public NoteStyle getFallback() {
        if (getData() == null || getData().fallback == null) return null;
        return NoteStyleRegistry.instance.fetchEntry(getData().fallback);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public NoteStyleData getData() {
        return this.noteStyleData;
    }

    @Override
    public void loadData(NoteStyleData data) {
        if (data == null) throw new IllegalArgumentException("StageData cannot be null");
        this.noteStyleData = data;
    }

    @Override
    public void destroy() {
    }
}
