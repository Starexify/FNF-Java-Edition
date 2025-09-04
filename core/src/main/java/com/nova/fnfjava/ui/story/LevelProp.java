package com.nova.fnfjava.ui.story;

import com.badlogic.gdx.Gdx;
import com.nova.fnfjava.Paths;
import com.nova.fnfjava.data.story.level.LevelData;
import com.nova.fnfjava.play.stage.Bopper;
import com.nova.fnfjava.util.assets.AnimationUtil;

public class LevelProp extends Bopper {
    public LevelData.LevelPropData propData = null;

    public LevelProp(float x, float y, LevelData.LevelPropData propData) {
        super(x, y, propData.danceEvery);
        this.setPropData(propData);
    }

    public void playConfirm() {
        if (hasAnimation("confirm")) playAnimation("confirm", true, true);
    }

    public void applyData() {
        if (propData == null) {
            setVisible(false);
            return;
        } else
            setVisible(true);

        // Reset animation state.
        this.shouldAlternate = null;

        boolean isAnimated = propData.animations.size > 0;
        if (isAnimated) this.setAtlas(Paths.getAtlas(propData.assetPath));
        else {
            loadGraphic(Paths.image(propData.assetPath));
            this.active = false;
        }

        if (this.atlas == null || this.atlas.getRegions().size == 0) {
            Gdx.app.error("LevelProp", "ERROR: Could not build texture for level prop (" + propData.assetPath +").");
            return;
        }

        float scale = propData.scale * (propData.isPixel ? 6 : 1);
        this.setScale(scale, scale);
        //this.antialiasing = !propData.isPixel;
        this.getColor().a = propData.alpha;
        this.setX(propData.offsets.get(0));
        this.setFlxY(propData.offsets.get(1));

        AnimationUtil.addAtlasAnimations(this, propData.animations);

        this.dance();
        this.animation.setPaused(true);
    }

    public LevelData.LevelPropData setPropData(LevelData.LevelPropData value) {
        // Only reset the prop if the asset path has changed.
        if (propData == null || !(value.equals(propData))) {
            this.propData = value;

            this.setVisible(this.propData != null);
            danceEvery = this.propData.danceEvery != null ? propData.danceEvery : 1.0f;

            applyData();
        }

        return this.propData;
    }

    public static LevelProp build(LevelData.LevelPropData propData) {
        if (propData == null) return null;
        return new LevelProp(0, 0, propData);
    }
}
