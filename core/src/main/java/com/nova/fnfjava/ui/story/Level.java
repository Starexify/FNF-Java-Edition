package com.nova.fnfjava.ui.story;

import com.nova.fnfjava.AnimatedSprite;
import com.nova.fnfjava.Paths;
import com.nova.fnfjava.data.IRegistryEntry;
import com.nova.fnfjava.data.story.level.LevelData;

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

    public boolean isUnlocked() {
        return true;
    }

    public boolean isVisible() {
        return levelData.visible;
    }

    public AnimatedSprite buildTitleGraphic() {
        AnimatedSprite result = new AnimatedSprite().loadGraphic(Paths.image(levelData.titleAsset));

        return result;
    }

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
