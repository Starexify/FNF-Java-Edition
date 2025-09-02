package com.nova.fnfjava;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ScrollableStage extends Stage {
    public ScrollableStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
    }
}
