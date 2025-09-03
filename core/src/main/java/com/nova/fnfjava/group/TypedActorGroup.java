package com.nova.fnfjava.group;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.Axes;
import com.nova.fnfjava.ui.AtlasText;

public class TypedActorGroup<T extends Actor> extends Group {
    public final Array<T> members;
    public int maxSize = 0;
    public int length = 0;

    public TypedActorGroup(int maxSize) {
        super();
        this.maxSize = maxSize;
        this.members = new Array<T>();
    }

    public TypedActorGroup() {
        this(0);
    }

    @Override
    public float getWidth() {
        if (members.size == 0) return 0;
        return findMaxX() - findMinX();
    }

    @Override
    public float getHeight() {
        if (members.size == 0) return 0;
        return findMaxY() - findMinY();
    }

    public float findMinX() {
        return members.size == 0 ? getX() : findMinXHelper();
    }

    public float findMinXHelper() {
        float value = Float.POSITIVE_INFINITY;
        for (T member : members) {
            if (member == null) continue;

            float minX;
            if (member instanceof TypedActorGroup<?>) minX = ((TypedActorGroup<?>) member).findMinX();
            else minX = member.getX();

            if (minX < value) value = minX;
        }
        return value == Float.POSITIVE_INFINITY ? getX() : value;
    }

    public float findMaxX() {
        return members.size == 0 ? getX() : findMaxXHelper();
    }

    public float findMaxXHelper() {
        float value = Float.NEGATIVE_INFINITY;
        for (T member : members) {
            if (member == null) continue;

            float maxX;
            if (member instanceof TypedActorGroup<?>) {
                maxX = ((TypedActorGroup<?>) member).findMaxX();
            } else {
                maxX = member.getX() + member.getWidth();
            }

            if (maxX > value) {
                value = maxX;
            }
        }
        return value == Float.NEGATIVE_INFINITY ? getX() : value;
    }

    public float findMinY() {
        return members.size == 0 ? getY() : findMinYHelper();
    }

    public float findMinYHelper() {
        float value = Float.POSITIVE_INFINITY;
        for (T member : members) {
            if (member == null) continue;

            float minY;
            if (member instanceof TypedActorGroup<?>) minY = ((TypedActorGroup<?>) member).findMinY();
            else minY = member.getY();

            if (minY < value) value = minY;
        }
        return value == Float.POSITIVE_INFINITY ? getY() : value;
    }

    public float findMaxY() {
        return members.size == 0 ? getY() : findMaxYHelper();
    }

    public float findMaxYHelper() {
        float value = Float.NEGATIVE_INFINITY;
        for (T member : members) {
            if (member == null) continue;

            float maxY;
            if (member instanceof TypedActorGroup<?>) maxY = ((TypedActorGroup<?>) member).findMaxY();
            else maxY = member.getY() + member.getHeight();

            if (maxY > value) value = maxY;
        }
        return value == Float.NEGATIVE_INFINITY ? getY() : value;
    }

    public TypedActorGroup<T> screenCenter(Axes axes) {
        if (axes.hasX()) setX((Gdx.graphics.getWidth() - getWidth()) / 2f);
        if (axes.hasY()) setY((Gdx.graphics.getHeight() - getHeight()) / 2f);
        return this;
    }

    public TypedActorGroup<T> screenCenter() {
        return screenCenter(Axes.XY);
    }

    /**
     * Adds a new `Actor` subclass (`Actor`, `AnimatedActor`, `Image`, etc) to the group.
     * `TypedActorGroup` will try to replace a `null` member of the array first.
     * Failing that, `TypedActorGroup` will add it to the end of the member array.
     * WARNING: If the group has a `maxSize` that has already been met,
     * the object will NOT be added to the group!
     *
     * @param   actor  The `Actor` you want to add to the group.
     * @return  The same `Actor` object that was passed in.
     */
    public T add(T actor) {
        if (actor == null) {
            Gdx.app.error("TypedActorGroup", "Cannot add a `null` object to a TypedActorGroup.");
            return null;
        }

        // Don't bother adding an object twice.
        if (members.indexOf(actor, true) >= 0) return actor;

        // First, look for a null entry where we can add the object.
        int index = getFirstNull();
        if (index != -1) {
            members.set(index, actor);
            if (index >= length) {
                length = index + 1;
            }

            if (!getChildren().contains(actor, true)) {
                addActor(actor);
            }
            actor.setVisible(true);
            return actor;
        }

        // If the group is full, return the actor
        if (maxSize > 0 && length >= maxSize) return actor;

        // If we made it this far, we need to add the basic to the group.
        members.add(actor);
        length++;

        if (!getChildren().contains(actor, true)) addActor(actor);
        actor.setVisible(true);

        return actor;
    }

    public int getFirstNull() {
        return members.indexOf(null, true);
    }

    public void killCharacter(T actor) {
        // Similar to Flixel's kill() - hide but don't remove for reuse
        if (actor == null) return;
        actor.setVisible(false);
        actor.getColor().a = 0f;
    }

    public void kill() {
        for (T child : members) if (child != null) killCharacter(child);
    }

    public int countLiving() {
        int count = -1;
        for (T child : members) {
            if (child != null) {
                if (count < 0) count = 0;
                if (child instanceof AtlasText.AtlasChar && isCharacterAlive(child)) count++;
            }
        }
        return count;
    }

    public boolean isCharacterAlive(T actor) {
        // In libGDX, we use visibility and alpha to simulate Flixel's exists/alive
        return actor.isVisible() && actor.getColor().a > 0f;
    }

    public void reviveCharacter(T character) {
        // Similar to Flixel's revive() - make visible again
        character.setVisible(true);
        character.getColor().a = 1f;
    }
}
