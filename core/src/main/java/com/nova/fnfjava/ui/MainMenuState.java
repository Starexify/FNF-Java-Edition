package com.nova.fnfjava.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.nova.fnfjava.*;

public class MainMenuState extends MusicBeatState {
    public MenuTypedList<AtlasMenuItem> menuItems;

    public Image bg;

    boolean overrideMusic = false;

    public MainMenuState(Main main) {
        super(main);
    }

    @Override
    public void show() {
        super.show();

        bg = new Image(Assets.getTexture("images/menuBG.png"));
        bg.setSize(Gdx.graphics.getWidth() * 1.2f, bg.getHeight() * (Gdx.graphics.getWidth() * 1.2f / bg.getWidth()));
        bg.setPosition((Gdx.graphics.getWidth() - bg.getWidth()) / 2, (Gdx.graphics.getHeight() - bg.getHeight()) / 2);
        add(bg);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Conductor.getInstance().update();
    }
}
