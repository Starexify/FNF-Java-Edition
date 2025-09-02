package com.nova.fnfjava.util.effects;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

public class FlickerUtil {
    public static void flicker(Actor actor, float duration, float interval, boolean endVisibility, boolean forceRestart) {
        if (forceRestart) actor.clearActions();

        // Calculate how many complete flickers we can fit in the duration
        int numFlickers = (int) Math.floor(duration / interval);

        // Use Array for dynamic sizing
        Array<Action> flickerActions = new Array<>();

        // Start with the actor visible for the first flash
        flickerActions.add(Actions.visible(true));
        flickerActions.add(Actions.delay(interval / 2));

        // Alternate visibility for the specified duration
        for (int i = 0; i < numFlickers; i++) {
            flickerActions.add(Actions.visible(false));
            flickerActions.add(Actions.delay(interval / 2));
            flickerActions.add(Actions.visible(true));
            flickerActions.add(Actions.delay(interval / 2));
        }

        // Set final visibility state
        flickerActions.add(Actions.visible(endVisibility));

        // Convert to array and execute all actions in sequence
        Action[] actionArray = new Action[flickerActions.size];
        for (int i = 0; i < flickerActions.size; i++) {
            actionArray[i] = flickerActions.get(i);
        }

        actor.addAction(Actions.sequence(actionArray));
    }
}
