package com.nova.fnfjava;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.nova.fnfjava.ui.MusicBeatState;
import com.nova.fnfjava.ui.transition.TransitionableScreenAdapter;

public class TransitionManager {
    public final Game game;
    public final int screenWidth, screenHeight;

    public TransitionManager(Game game, int screenWidth, int screenHeight) {
        this.game = game;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void setScreen(final Screen screen, boolean skipOutTransition, boolean skipInTransition) {
        Screen current = game.getScreen();

        if (current == null) {
            // First screen: just show with transition in (unless skipped)
            game.setScreen(screen);
            if (!skipInTransition) {
                startTransition(screen, true, null);
            }
            return;
        }

        if (skipOutTransition) {
            game.setScreen(screen);
            if (!skipInTransition) startTransition(screen, true, null);
        } else {
            // Transition out current, then in new one
            startTransition(current, false, () -> {
                game.setScreen(screen);
                if (!skipInTransition) startTransition(screen, true, null);
            });
        }
    }

    public void startTransition(Screen screen, boolean in, Runnable onComplete) {
        if (!(screen instanceof TransitionableScreenAdapter)) {
            if (onComplete != null) onComplete.run();
            return;
        }

        TransitionableScreenAdapter tsa = (TransitionableScreenAdapter) screen;
        TransitionableScreenAdapter.TransitionConfig config = in ? tsa.getTransitionInConfig() : tsa.getTransitionOutConfig();

        if (config.type == TransitionableScreenAdapter.TransitionType.NONE) {
            if (onComplete != null) onComplete.run();
            return;
        }

        MusicBeatState musicScreen = (MusicBeatState) screen;
        Actor overlay = createOverlay(config.overlayColor, musicScreen.stage);

        Action action = createTransitionAction(config, overlay, in, onComplete);
        overlay.addAction(action);
    }

    public Actor createOverlay(Color color, Stage stage) {
        Actor overlay = createWipeOverlay(color);
        stage.addActor(overlay);
        overlay.toFront();
        return overlay;
    }

    public Action createTransitionAction(TransitionableScreenAdapter.TransitionConfig config, Actor overlay, boolean in, Runnable onComplete) {
        SequenceAction sequence = new SequenceAction();

        switch (config.type) {
            case WIPE_VERTICAL:
                if (in) {
                    overlay.setY(0);
                    sequence.addAction(Actions.moveTo(0, screenHeight, config.duration, config.interpolation));
                } else {
                    overlay.setY(-screenHeight);
                    sequence.addAction(Actions.moveTo(0, 0, config.duration, config.interpolation));
                }
                break;
        }

        if (in) {
            sequence.addAction(Actions.run(overlay::remove));
        } else {
            sequence.addAction(Actions.run(() -> {
                if (onComplete != null) onComplete.run();
            }));
        }

        return sequence;
    }

    public Image createWipeOverlay(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        Image overlay = new Image(texture);
        overlay.setSize(screenWidth, screenHeight);
        return overlay;
    }
}
