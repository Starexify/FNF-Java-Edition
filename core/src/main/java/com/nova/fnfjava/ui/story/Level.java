package com.nova.fnfjava.ui.story;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.AnimatedSprite;
import com.nova.fnfjava.Paths;
import com.nova.fnfjava.data.IRegistryEntry;
import com.nova.fnfjava.data.song.SongRegistry;
import com.nova.fnfjava.data.story.level.LevelData;
import com.nova.fnfjava.play.Song;
import com.nova.fnfjava.util.Constants;
import com.nova.fnfjava.util.ImageUtil;

public class Level implements IRegistryEntry<LevelData> {
    public String id;
    public LevelData levelData;

    public Level(String id, Object... params) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public LevelData getData() {
        return levelData;
    }

    @Override
    public void loadData(LevelData data) {
        if (data == null) throw new IllegalArgumentException("LevelData cannot be null");
        this.levelData = data;
    }

    /**
     * Get the list of songs in this level, as an array of IDs.
     * @return Array<String>
     */
    public Array<String> getSongs() {
        // Copy the array so that it can't be modified on accident
        return new Array<>(getData().songs);
    }

    public String getTitle() {
        return getData().name;
    }

    /**
     * Construct the title graphic for the level.
     * @return The constructed graphic as a sprite.
     */
    public AnimatedSprite buildTitleGraphic() {
        AnimatedSprite result = new AnimatedSprite().loadGraphic(Paths.image(getData().titleAsset));

        return result;
    }

    /**
     * Get the list of songs in this level, as an array of names, for display on the menu.
     * @param difficulty The difficulty of the level being displayed
     * @return The display names of the songs in this level
     */
    public Array<String> getSongDisplayNames(String difficulty) {
        Array<String> songList = getSongs() != null ? getSongs() : new Array<>();
        Array<String> songNameList = new Array<>();
        for (String songId : songList) songNameList.add(getSongDisplayName(songId, difficulty));
        return songNameList;
    }

    public static String getSongDisplayName(String songId, String difficulty) {
        Song song = SongRegistry.instance.fetchEntry(songId);
        if (song == null) return "Unknown";

        return song.songName;
    }

    public boolean isUnlocked() {
        return true;
    }

    public boolean isVisible() {
        return levelData.visible;
    }

    public Actor buildBackground() {
        // Image specified
        if (!getData().background.startsWith("#")) return new AnimatedSprite().loadGraphic(Paths.image(getData().background));

        // Color specified
        Image result = ImageUtil.createColored(Gdx.graphics.getWidth(), 400, Color.WHITE);
        result.setColor(getBackgroundColor());
        return result;
    }

    public boolean isBackgroundSimple() {
        return getData().background.startsWith("#");
    }

    public Color getBackgroundColor() {
        return Color.valueOf(getData().background);
    }

/*    public Array<LevelProp> buildProps(Array<LevelProp> existingProps){
        return null;
    }

    public Array<LevelProp> buildProps(){
        return buildProps(null);
    }*/

    @Override
    public void destroy() {

    }

    @Override
    public String toString() {
        return "Level{" +
            "id='" + id + '\'' +
            ", levelData=" + levelData +
            '}';
    }
}
