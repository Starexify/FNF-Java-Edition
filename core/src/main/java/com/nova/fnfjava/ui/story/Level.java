package com.nova.fnfjava.ui.story;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.nova.fnfjava.AnimatedSprite;
import com.nova.fnfjava.Paths;
import com.nova.fnfjava.data.IRegistryEntry;
import com.nova.fnfjava.data.story.level.LevelData;
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

    public AnimatedSprite buildTitleGraphic() {
        AnimatedSprite result = new AnimatedSprite().loadGraphic(Paths.image(getData().titleAsset));

        return result;
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
