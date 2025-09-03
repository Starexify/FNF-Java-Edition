package com.nova.fnfjava.ui.story;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.nova.fnfjava.AnimatedSprite;
import com.nova.fnfjava.Axes;
import com.nova.fnfjava.Paths;

public class LevelTitle extends Group {
    static final int LOCK_PAD = 4;

    public final Level level;
    public float targetY;

    public AnimatedSprite title;
    public AnimatedSprite lock;

    public LevelTitle(int x, int y, Level level) {
        super();
        this.setPosition(x, y);

        this.level = level;

        if (this.level == null) throw new IllegalArgumentException("Level cannot be null!");

        buildLevelTitle();
        buildLevelLock();
    }

    public boolean isFlashing = false;

    public float flashTick = 0;
    public final float flashFramerate = 20;

    @Override
    public void act(float delta) {
        super.act(delta);

        this.setY(MathUtils.lerp(getY(), targetY, delta * 0.451f));

        if (isFlashing) {
            flashTick += delta;
            if (flashTick >= 1 / flashFramerate) {
                flashTick %= 1 / flashFramerate;
                if (title.getColor().equals(Color.WHITE)) title.setColor(Color.valueOf("#33ffffFF"));
                else title.setColor(Color.WHITE);
            }
        }
    }

    public void showLock() {
        lock.setVisible(true);
        this.setX(this.getX() - ((lock.getWidth() + LOCK_PAD) / 2));
    }

    public void hideLock() {
        lock.setVisible(false);
        this.setX(this.getX() + ((lock.getWidth() + LOCK_PAD) / 2));
    }

    public void buildLevelTitle() {
        title = level.buildTitleGraphic();
        addActor(title);
    }

    public void buildLevelLock() {
        lock = new AnimatedSprite(0, 0).loadGraphic(Paths.image("storymenu/ui/lock"));
        lock.setX(title.getX() + title.getWidth() + LOCK_PAD);
        lock.setVisible(false);
        addActor(lock);
    }

    public LevelTitle screenCenter(Axes axes) {
        if (axes.hasX()) setX((Gdx.graphics.getWidth() - getWidth()) / 2f);
        if (axes.hasY()) setY((Gdx.graphics.getHeight() - getHeight()) / 2f);
        return this;
    }
}
