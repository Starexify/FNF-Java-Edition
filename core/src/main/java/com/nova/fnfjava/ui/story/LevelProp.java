package com.nova.fnfjava.ui.story;

import com.nova.fnfjava.FunkinSprite;
import com.nova.fnfjava.data.story.level.LevelData;

public class LevelProp extends FunkinSprite {
    public LevelData.LevelPropData propData = null;

    public LevelProp(float x, float y) {
        super(x, y);
    }

    public LevelData.LevelPropData setPropData(LevelData.LevelPropData value) {
        // Only reset the prop if the asset path has changed.
        if (propData == null || !(value.equals(propData))) {
            this.propData = value;

            this.setVisible(this.propData != null);
            //danceEvery = this.propData?.danceEvery ?? 1.0;

            //applyData();
        }

        return this.propData;
    }

}
