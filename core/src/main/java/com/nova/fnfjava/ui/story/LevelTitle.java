package com.nova.fnfjava.ui.story;

import com.badlogic.gdx.graphics.Color;
import com.nova.fnfjava.graphics.AnimatedSprite;
import com.nova.fnfjava.Paths;
import com.nova.fnfjava.group.TypedActorGroup;
import com.nova.fnfjava.util.MathUtil;

public class LevelTitle extends TypedActorGroup<AnimatedSprite> {
    public static final int LOCK_PAD = 4;

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

        this.setY(MathUtil.smoothLerpPrecision(getY(), targetY, delta, 0.451f));

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
        add(title);
    }

    public void buildLevelLock() {
        lock = new AnimatedSprite(0, 0).loadGraphic(Paths.image("storymenu/ui/lock"));
        lock.setX(title.getX() + title.getWidth() + LOCK_PAD);
        add(lock);
        lock.setVisible(false);
    }
}
