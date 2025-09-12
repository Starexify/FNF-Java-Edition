package com.nova.fnfjava.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.ObjectMap;

public class Codex<T> extends Group {
    public ObjectMap<T, Page<T>> pages;
    public T currentName;

    public Codex(T initPage) {
        pages = new ObjectMap<>();
        this.currentName = initPage;
    }

    public <P extends Page<T>> P addPage(T name,P page) {
        page.onSwitch.add((signal, object) ->  switchPage(object));
        page.codex = this;
        pages.put(name, page);
        addActor(page);
        page.setVisible(currentName.equals(name));
        return page;
    }

    public void setPage(T name) {
        if (pages.containsKey(currentName)) getCurrentPage().setVisible(false);
        currentName = name;
        if (pages.containsKey(currentName)) getCurrentPage().setVisible(true);
    }

    public void switchPage(T name) {
        // TODO: Animate this transition?
        setPage(name);
    }

    public Page<T> getCurrentPage() {
        return pages.get(currentName);
    }
}
