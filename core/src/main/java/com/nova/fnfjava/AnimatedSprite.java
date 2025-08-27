package com.nova.fnfjava;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.nova.fnfjava.animation.AnimationController;

public class AnimatedSprite extends Actor {
    public AnimationController animation;

    public int frameWidth = 0;
    public int frameHeight = 0;

    public TextureRegion frame;
    public TextureRegion[] frames;
    public TextureAtlas atlas;

    public AnimatedSprite(float x, float y) {
        animation = new AnimationController(this);
        setPosition(x, y);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        animation.update(delta);

        TextureRegion currentFrame = getCurrentDisplayFrame();
        if (currentFrame != null && (getWidth() == 0 || getHeight() == 0)) {
            setSize(currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = getCurrentDisplayFrame();
        if (currentFrame != null) {
            batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
            batch.draw(currentFrame, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
            batch.setColor(1, 1, 1, 1);
        }
    }

    private TextureRegion getCurrentDisplayFrame() {
        TextureRegion animFrame = animation.getCurrentFrame();
        if (animFrame != null) return animFrame;

        return frame;
    }
}
