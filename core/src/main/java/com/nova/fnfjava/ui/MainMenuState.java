package com.nova.fnfjava.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.nova.fnfjava.Main;

public class MainMenuState extends MusicBeatState {
    public MenuTypedList<AtlasMenuItem> menuItems;

    public Actor bg;

    public MainMenuState(Main main) {
        super(main);
    }

    @Override
    public void show() {
        super.show();

        Texture bgTexture = new Texture(Gdx.files.internal("images/menuBG.png"));
        bg = new Actor();
        bg.setSize(bgTexture.getWidth(), bgTexture.getHeight());
        add(bg);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }
}
