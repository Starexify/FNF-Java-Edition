package com.nova.fnfjava.ui;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.nova.fnfjava.*;
import com.nova.fnfjava.ui.freeplay.FreeplayState;
import com.nova.fnfjava.util.Constants;

public class MainMenuState extends MusicBeatState {
    public MenuTypedList<AtlasMenuItem> menuItems;

    public Image bg;
    public Image magenta;

    public boolean overrideMusic = false;

    public boolean canInteract = false;

    public static int rememberedSelectedIndex = 0;

    public MainMenuState(Main main, boolean overrideMusic) {
        super(main);
        this.overrideMusic = overrideMusic;
    }

    public MainMenuState(Main main) {
        this(main, false);
    }

    @Override
    public void show() {
        super.show();

        bg = new Image(Assets.getTexture("images/menuBG.png"));
        bg.setSize(Gdx.graphics.getWidth() * 1.2f, bg.getHeight() * (Gdx.graphics.getWidth() * 1.2f / bg.getWidth()));
        bg.setPosition((Gdx.graphics.getWidth() - bg.getWidth()) / 2, (Gdx.graphics.getHeight() - bg.getHeight()) / 2);
        add(bg);

        magenta = new Image(Assets.getTexture("images/menuBGMagenta.png"));
        magenta.setSize(bg.getWidth(), bg.getHeight());
        magenta.setPosition(bg.getImageX(), bg.getImageY());
        magenta.setVisible(false);

        if (Preferences.getFlashingLights()) add(magenta);

        menuItems = new MenuTypedList<>();
        add(menuItems);
        menuItems.onChange.add(onMenuItemChange());
        menuItems.onAcceptPress.add(new Listener<AtlasMenuItem>() {
            @Override
            public void receive(Signal<AtlasMenuItem> signal, AtlasMenuItem item) {
                System.out.println("Accept Pressed for: " + item.name);
            }
        });

        menuItems.enabled = true;
        createMenuItem("storymode", "mainmenu/storymode", () -> System.out.println("Story Mode selected!"));
        createMenuItem("freeplay", "mainmenu/freeplay", () -> {
            if (menuItems != null) rememberedSelectedIndex = menuItems.selectedIndex;

            openSubState(new FreeplayState(main));
            System.out.println("Freeplay selected!");

            canInteract = true;
        });
        createMenuItem("options", "mainmenu/options", () -> System.out.println("Options selected!"));
        createMenuItem("credits", "mainmenu/credits", () -> System.out.println("Options selected!"));

        final float spacing = 160;
        final float top = (main.viewport.getWorldHeight() + (spacing * (menuItems.getChildren().size - 1))) / 2;
        for (int index = 0; index < menuItems.getChildren().size; index++) {
            var menuItem = menuItems.items.get(index);
            menuItem.setX(main.viewport.getWorldWidth() / 2);
            menuItem.setY(top - spacing * index);
            //menuItem.scrollFactor.x = #if !mobile 0.0 #else 0.4 #end; // we want a lil scroll on mobile, for the cute gyro effect
            // This one affects how much the menu items move when you scroll between them.
            //menuItem.scrollFactor.y = 0.4;

            if (index == 1) {
                //camFollow.setPosition(menuItem.getGraphicMidpoint().x, menuItem.getGraphicMidpoint().y);
            }
        }

        menuItems.selectItem(rememberedSelectedIndex);

        initLeftWatermarkText();
    }

    public void initLeftWatermarkText() {
        if (leftWatermarkText == null) return;
        leftWatermarkText.setText(Constants.VERSION);
        leftWatermarkText.toFront();
    }

    public Listener<AtlasMenuItem> onMenuItemChange() {
        return new Listener<AtlasMenuItem>() {
            @Override
            public void receive(Signal<AtlasMenuItem> signal, AtlasMenuItem item) {
                System.out.println("Selected: " + item.name);
            }
        };
    }

    public void createMenuItem(String name, String atlas, Runnable callback, boolean fireInstantly) {
        if (menuItems == null) return;

        AtlasMenuItem item = new AtlasMenuItem(name, new TextureAtlas(Paths.getAtlas(atlas)), callback);
        item.fireInstantly = fireInstantly;
        //item.ID = menuItems.length;
        //item.scrollFactor.set();
        item.centered = true;
        item.changeAnim("idle");
        menuItems.addItem(name, item);
    }

    public void createMenuItem(String name, String atlas, Runnable callback) {
        this.createMenuItem(name, atlas, callback, false);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Conductor.getInstance().update();

        PlayerSettings.player1.controls.update();

        handleInputs();
    }

    public void handleInputs() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) goBack();
    }

    public void goBack() {
        rememberedSelectedIndex = (menuItems != null) ? menuItems.selectedIndex : 0;
        Main.sound.playOnce(Paths.sound("cancelMenu"));

        main.switchState(new TitleState(main));
    }
}
