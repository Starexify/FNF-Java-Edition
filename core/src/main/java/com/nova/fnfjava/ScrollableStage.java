package com.nova.fnfjava;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ScrollableStage extends Stage {
/*
    public OrthographicCamera camera;
    public Vector2 cameraPosition = new Vector2();
    public Vector2 lastCameraPosition = new Vector2();
*/

    public ScrollableStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
/*        this.camera = (OrthographicCamera) viewport.getCamera();
        cameraPosition.set(camera.position.x, camera.position.y);
        lastCameraPosition.set(cameraPosition);*/
    }
/*
    public void setCameraPosition(float x, float y) {
        lastCameraPosition.set(cameraPosition);
        cameraPosition.set(x, y);
        camera.position.set(x, y, 0);
        camera.update();

        // Update all sprites' screen positions when camera moves
        updateScrollFactors();
    }

    public void moveCamera(float deltaX, float deltaY) {
        setCameraPosition(cameraPosition.x + deltaX, cameraPosition.y + deltaY);
    }

    private void updateScrollFactors() {
        // Update screen positions for all AnimatedSprites
        for (Actor actor : getActors()) {
            updateActorScrollFactor(actor);
        }
    }

    private void updateActorScrollFactor(Actor actor) {
        // Check if the actor implements Scrollable
        if (actor instanceof Scrollable) {
            ((Scrollable) actor).updateScreenPosition(camera);
        }

        // Handle groups recursively
        if (actor instanceof Group) {
            Group group = (Group) actor;
            for (Actor child : group.getChildren()) {
                updateActorScrollFactor(child);
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (!cameraPosition.equals(lastCameraPosition)) {
            updateScrollFactors();
            lastCameraPosition.set(cameraPosition);
        }
    }*/
}
