package com.nova.fnfjava.ui.options;

import com.badlogic.gdx.Gdx;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.Paths;
import com.nova.fnfjava.PlayerSettings;
import com.nova.fnfjava.audio.FunkinSound;
import com.nova.fnfjava.graphics.AnimatedSprite;
import com.nova.fnfjava.ui.*;
import com.nova.fnfjava.ui.mainmenu.MainMenuState;
import com.nova.fnfjava.util.Axes;

public class OptionsState extends MusicBeatState {
    public static OptionsState instance;

    public Codex<OptionsMenuPageName> optionsCodex;
    public FunkinSound.OneShotSound drumsBG;
    public static int rememberedSelectedIndex = 0;

    public OptionsState(Main main) {
        super(main);
    }

    @Override
    public void show() {
        super.show();

        instance = this;

        persistentUpdate = true;

        //drumsBG = FunkinSound.OneShotSound.load(Paths.music("offsetsLoop/drumsLoop"), 0, true, false, false, false);
        AnimatedSprite menuBG = new AnimatedSprite().loadGraphic(Paths.image("menuBG"));
        //var hsv = new HSVShader(-0.6, 0.9, 3.6);
        //menuBG.shader = hsv;
        menuBG.setWidth(Gdx.graphics.getWidth() * 1.1f);
        menuBG.updateHitbox();
        menuBG.screenCenter();
        //menuBG.scrollFactor.set(0, 0);
        add(menuBG);

        optionsCodex = new Codex<>(OptionsMenuPageName.Options);
        add(optionsCodex);

        SaveDataMenu saveData = optionsCodex.addPage(OptionsMenuPageName.SaveData, new SaveDataMenu());
        OptionsMenu options = optionsCodex.addPage(OptionsMenuPageName.Options, new OptionsMenu(saveData));
        PreferencesMenu preferences = optionsCodex.addPage(OptionsMenuPageName.Preferences, new PreferencesMenu());
        ControlsMenu controls = optionsCodex.addPage(OptionsMenuPageName.Controls, new ControlsMenu());
        OffsetMenu offsets = optionsCodex.addPage(OptionsMenuPageName.Offsets, new OffsetMenu());

        if (options.hasMultipleOptions()) {
            options.onExit.add((signal, obj) -> exitToMainMenu());
            controls.onExit.add((signal, object) -> exitControls());
            preferences.onExit.add((signal, obj) -> optionsCodex.switchPage(OptionsMenuPageName.Options));
            offsets.onExit.add((signal, object) -> exitOffsets());
            saveData.onExit.add((signal, object) -> optionsCodex.switchPage(OptionsMenuPageName.Options));
        } else {
            preferences.onExit.add((signal, obj) -> exitToMainMenu());
            optionsCodex.setPage(OptionsMenuPageName.Preferences);
            controls.onExit.add((signal, obj) -> exitToMainMenu());
            optionsCodex.setPage(OptionsMenuPageName.Controls);
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        PlayerSettings.player1.controls.update();
    }

    public void exitOffsets() {
/*        if (drumsBG.volume > 0) drumsBG.fadeOut(0.5, 0);
        FlxG.sound.music.fadeOut(0.5, 0, function(tw) {
        FunkinSound.playMusic('freakyMenu',
            {
                startingVolume: 0,
            overrideExisting: true,
            restartTrack: true,
            persist: true
        });
        FlxG.sound.music.fadeIn(0.5, 1);
    });*/
        optionsCodex.switchPage(OptionsMenuPageName.Options);
    }

    public void exitControls() {
        // Apply any changes to the controls.
        //PlayerSettings.reset();
        //PlayerSettings.init();

        optionsCodex.switchPage(OptionsMenuPageName.Options);
    }

    public void exitToMainMenu() {
        optionsCodex.getCurrentPage().enabled = false;
        // TODO: Animate this transition?
        //FlxG.keys.enabled = false;
        main.switchState(new MainMenuState(main));
    }

    public static class OptionsMenu extends Page<OptionsMenuPageName> {
        public TextMenuList items;
        //var camFocusPoint:FlxObject;
        public final int CAMERA_MARGIN = 150;

        public OptionsMenu(SaveDataMenu saveDataMenu) {
            add(items = new TextMenuList());

            createItem("PREFERENCES", () -> codex.switchPage(OptionsMenuPageName.Preferences));
            createItem("CONTROLS", () -> codex.switchPage(OptionsMenuPageName.Controls));
            createItem("LAG ADJUSTMENT", () -> codex.switchPage(OptionsMenuPageName.Offsets));
            createItem("OPEN DATA FOLDER", () -> System.out.println("Open Data Folder not implemented yet"));
            if (saveDataMenu.hasMultipleOptions()) createItem("SAVE DATA OPTIONS", () -> codex.switchPage(OptionsMenuPageName.SaveData));
            else createItem("CLEAR SAVE DATA", () -> System.out.println("Clear Save Data not implemented yet"));
            createItem("EXIT", this::exit);

            items.onChange.add((signal, object) -> onMenuChange(object));

            System.out.println("OptionsMenu: " + items.items);
            onMenuChange(items.members.get(0));

            items.selectItem(OptionsState.rememberedSelectedIndex);
        }

        public void onMenuChange(TextMenuList.TextMenuItem selected) {
            //camFocusPoint.y = selected.getY();
        }

        public TextMenuList.TextMenuItem createItem(String name, Runnable callback, boolean fireInstantly) {
            TextMenuList.TextMenuItem item = items.createItem(0, Gdx.graphics.getHeight() - (100 + items.length * 100), name, AtlasText.AtlasFont.BOLD, callback);
            item.fireInstantly = fireInstantly;
            item.screenCenter(Axes.X);
            return item;
        }

        public TextMenuList.TextMenuItem createItem(String name, Runnable callback) {
            return createItem(name, callback, false);
        }

        public boolean hasMultipleOptions() {
            return items.length > 2;
        }
    }

    public enum OptionsMenuPageName {
        Options("options"),
        Controls("controls"),
        Colors("colors"),
        Mods("mods"),
        Preferences("preferences"),
        Offsets("offsets"),
        SaveData("saveData");

        private final String name;
        OptionsMenuPageName(String name) { this.name = name; }

        public String getName() { return name; }
    }
}
