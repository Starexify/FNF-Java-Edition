package com.nova.fnfjava;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nova.fnfjava.ui.MusicBeatState;

public class ScrollableStage extends Stage {
    private MusicBeatState parentState;

    public ScrollableStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
    }

    public void setParentState(MusicBeatState parentState) {
        this.parentState = parentState;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        boolean handled = super.scrolled(amountX, amountY);
        if (!handled && parentState != null) handled = parentState.handleMouseWheel(amountY);

        return super.scrolled(amountX, amountY);
    }
}
