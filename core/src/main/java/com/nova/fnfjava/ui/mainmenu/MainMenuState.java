package com.nova.fnfjava.ui.mainmenu;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Timer;
import com.nova.fnfjava.*;
import com.nova.fnfjava.api.discord.DiscordClient;
import com.nova.fnfjava.audio.FunkinSound;
import com.nova.fnfjava.ui.*;
import com.nova.fnfjava.ui.freeplay.FreeplayState;
import com.nova.fnfjava.ui.story.StoryMenuState;
import com.nova.fnfjava.ui.title.TitleState;
import com.nova.fnfjava.util.Constants;
import com.nova.fnfjava.util.effects.FlickerUtil;

public class MainMenuState extends MusicBeatState {
    public MenuTypedList<AtlasMenuItem> menuItems;

    public Image bg;
    public Image magenta;

    public Actor camFollowActor;

    public boolean overrideMusic = false;
    public UIStateMachine uiStateMachine = new UIStateMachine();

    public static int rememberedSelectedIndex = 0;

    public MainMenuState(Main main, boolean overrideMusic) {
        super(main);
        this.overrideMusic = overrideMusic;

        uiStateMachine.transition(UIStateMachine.UIState.ENTERING);

        camFollowActor = new Actor();
        camFollowActor.setSize(1, 1);
    }

    public MainMenuState(Main main) {
        this(main, false);
    }

    @Override
    public void show() {
        super.show();

        DiscordClient.instance.setPresence(new DiscordClient.DiscordPresenceParams("In the Menus", null));

        if (!overrideMusic) playMenuMusic();

        persistentUpdate = true;
        persistentDraw = true;

        bg = new Image(Assets.getTexture("assets/images/menuBG.png"));
        bg.scaleBy(0.2f);
        bg.setPosition((Gdx.graphics.getWidth() - bg.getWidth()) / 2, (Gdx.graphics.getHeight() - bg.getHeight()) / 2);
        addWithScrollFactor(bg, 0, 0.17f);

        magenta = new Image(Assets.getTexture("assets/images/menuBGMagenta.png"));
        magenta.setSize(bg.getWidth(), bg.getHeight());
        magenta.setPosition(bg.getImageX(), bg.getImageY());
        magenta.setVisible(false);

        if (Preferences.getFlashingLights()) addWithScrollFactor(magenta, 0, 0.17f);

        addWithScrollFactor(camFollowActor);

        menuItems = new MenuTypedList<>();
        addWithScrollFactor(menuItems);
        menuItems.onChange.add(onMenuItemChange());
        menuItems.onAcceptPress.add(new Listener<AtlasMenuItem>() {
            @Override
            public void receive(Signal<AtlasMenuItem> signal, AtlasMenuItem item) {
                FlickerUtil.flicker(magenta, 1.1f, 0.15f, false, true);
                uiStateMachine.transition(UIStateMachine.UIState.INTERACTING);
            }
        });

        menuItems.enabled = true;
        createMenuItem("storymode", "mainmenu/storymode", () -> {
            startExitState(new StoryMenuState(main));
        });
        createMenuItem("freeplay", "mainmenu/freeplay", () -> {
            persistentDraw = true;
            persistentUpdate = false;
            if (menuItems != null) rememberedSelectedIndex = menuItems.selectedIndex;

            openSubState(new FreeplayState(main));
            System.out.println("Freeplay selected!");
        });
        createMenuItem("options", "mainmenu/options", () -> System.out.println("Options selected!"));
        createMenuItem("credits", "mainmenu/credits", () -> System.out.println("Credits selected!"));

        final float spacing = 160;
        final float top = (main.viewport.getWorldHeight() + (spacing * (menuItems.members.size - 1))) / 2;
        for (int index = 0; index < menuItems.members.size; index++) {
            var menuItem = menuItems.items.get(index);
            menuItem.setX(main.viewport.getWorldWidth() / 2);
            menuItem.setY(top - spacing * index);
            //menuItem.scrollFactor.x = #if !mobile 0.0 #else 0.4 #end; // we want a lil scroll on mobile, for the cute gyro effect
            // This one affects how much the menu items move when you scroll between them.
            //menuItem.scrollFactor.y = 0.4;

            if (index == 1) {
                float itemCenterX = menuItem.getX();
                float itemCenterY = menuItem.getY() + menuItem.getHeight() / 2;
                camFollowActor.setPosition(itemCenterX, itemCenterY);
                //camFollow.setPosition(menuItem.getGraphicMidpoint().x, menuItem.getGraphicMidpoint().y);
            }
        }

        menuItems.selectItem(rememberedSelectedIndex);

        stage.follow(camFollowActor, 0.06f);
        stage.snapToTarget();

        initLeftWatermarkText();
    }

    public void initLeftWatermarkText() {
        if (leftWatermarkText == null) return;
        leftWatermarkText.setText(Constants.VERSION);
        leftWatermarkText.toFront();
    }

    public void playMenuMusic() {
        Main.sound.playMusic("freakyMenu", new FunkinSound.FunkinSoundPlayMusicParams.Builder()
            .overrideExisting(true)
            .restartTrack(false)
            .persist(true)
            .build());
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

    public Listener<AtlasMenuItem> onMenuItemChange() {
        return new Listener<AtlasMenuItem>() {
            @Override
            public void receive(Signal<AtlasMenuItem> signal, AtlasMenuItem item) {
                float itemCenterX = item.getX();
                float itemCenterY = item.getY() + item.getHeight() / 2;
                camFollowActor.setPosition(itemCenterX, itemCenterY);
            }
        };
    }

    public void startExitState(Screen nextScreen) {
        if (menuItems == null) return;

        uiStateMachine.transition(UIStateMachine.UIState.EXITING);
        rememberedSelectedIndex = menuItems.selectedIndex;

        stage.stopFollowing(false);

        float fadeOutDuration = 0.4f;

        for (int i = 0; i < menuItems.items.size; i++) {
            AtlasMenuItem item = menuItems.items.get(i);

            if (rememberedSelectedIndex != i) item.addAction(Actions.fadeOut(fadeOutDuration));
            else item.setVisible(false);
        }

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Main.logger.setTag("MainMenu").debug("MainMenuState", "Exiting MainMenuState...");
                main.switchState(nextScreen);
            }
        }, fadeOutDuration);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Conductor.getInstance().update();

        PlayerSettings.player1.controls.update();

        handleInputs();

        if (menuItems != null) menuItems.busy = !getCanInteract();
    }

    public void handleInputs() {
        if (!getCanInteract()) return;

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) goBack();
    }

    public void goBack() {
        uiStateMachine.transition(UIStateMachine.UIState.EXITING);
        rememberedSelectedIndex = (menuItems != null) ? menuItems.selectedIndex : 0;
        Main.sound.playOnce(Paths.sound("cancelMenu"));

        main.switchState(new TitleState(main));
    }

    public boolean getCanInteract() {
        return uiStateMachine.canInteract();
    }
}
