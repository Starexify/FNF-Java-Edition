package com.nova.fnfjava.util.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class CameraFlash {
    private static CameraFlash instance;
    private Image flashOverlay;
    private Texture whiteTexture;
    private boolean flashActive = false;
    private Stage currentStage;

    private CameraFlash() {
        // Create white texture
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whiteTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    public static CameraFlash getInstance() {
        if (instance == null) instance = new CameraFlash();
        return instance;
    }

    public void setStage(Stage stage) {
        if (currentStage != stage) {
            currentStage = stage;
            setupOverlay();
        }
    }

    private void setupOverlay() {
        if (flashOverlay != null) {
            flashOverlay.remove();
        }

        flashOverlay = new Image(whiteTexture);
        flashOverlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        flashOverlay.getColor().a = 0f;
        flashOverlay.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);
        currentStage.addActor(flashOverlay);
    }

    public void flash(Color color, float duration) {
        if (currentStage == null || flashActive) return;

        flashActive = true;
        flashOverlay.setColor(color);
        flashOverlay.getColor().a = 1f;
        flashOverlay.toFront();

        flashOverlay.addAction(Actions.sequence(
            Actions.fadeOut(duration),
            Actions.run(() -> flashActive = false)
        ));
    }

    public void dispose() {
        if (whiteTexture != null) whiteTexture.dispose();
    }
}
