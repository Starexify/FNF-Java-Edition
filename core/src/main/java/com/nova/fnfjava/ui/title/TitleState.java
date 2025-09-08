package com.nova.fnfjava.ui.title;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.nova.fnfjava.*;
import com.nova.fnfjava.audio.FunkinSound;
import com.nova.fnfjava.graphics.AnimatedSprite;
import com.nova.fnfjava.ui.AtlasText;
import com.nova.fnfjava.ui.mainmenu.MainMenuState;
import com.nova.fnfjava.ui.MusicBeatState;
import com.nova.fnfjava.util.Axes;
import com.nova.fnfjava.util.Constants;
import com.nova.fnfjava.util.ImageUtil;
import com.nova.fnfjava.util.camera.CameraFlash;

public class TitleState extends MusicBeatState {
    public static boolean initialized = false;

    public Image blackScreen;

    public AnimatedSprite ngSpr;

    public Array<String> curWacky = new Array<>();
    public int lastBeat = 0;

    public AnimatedSprite logoBl;
    public AnimatedSprite gfDance;
    public boolean danceLeft = false;
    public AnimatedSprite titleText;

    public Group credGroup;
    public Group textGroup;

    public Timer.Task attractTimer;

    public TitleState(Main main) {
        super(main);
    }

    @Override
    public void show() {
        super.show();

        curWacky = getIntroTextShit().random();

        CameraFlash.getInstance().setStage(stage);

        startIntro();
    }

    public void startIntro() {
        if (!initialized || Main.sound.music == null) playMenuMusic();

        logoBl = new AnimatedSprite(-150, 100);
        logoBl.atlas = new TextureAtlas("assets/images/logoBumpin.atlas");
        logoBl.animation.addByPrefix("bump", "logo bumpin", 24);
        logoBl.animation.play("bump");

        gfDance = new AnimatedSprite(Gdx.graphics.getWidth() * 0.4F, Gdx.graphics.getHeight() * 0.07F);
        gfDance.atlas = new TextureAtlas("assets/images/gfDanceTitle.atlas");
        //gfDance.setFlxY(Gdx.graphics.getHeight() * 0.07F);
        gfDance.animation.addByIndices("danceLeft", "gfDance", new Array<>(new Integer[]{30, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14}), 24);
        gfDance.animation.addByIndices("danceRight", "gfDance", new Array<>(new Integer[]{15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29}), 24);

        add(logoBl);

        add(gfDance);

        titleText = new AnimatedSprite(50, main.viewport.getWorldHeight() * 0.2F);
        titleText.atlas = new TextureAtlas("assets/images/titleEnter.atlas");
        titleText.animation.addByPrefix("idle", "Press Enter to Begin", 24);
        titleText.animation.addByPrefix("press", "ENTER PRESSED", 24);
        titleText.animation.play("idle");
        add(titleText);

        if (!initialized) {
            credGroup = new Group();
            add(credGroup);
        }

        textGroup = new Group();

        blackScreen = ImageUtil.blackScreen();

        if (credGroup != null) {
            credGroup.addActor(blackScreen);
            credGroup.addActor(textGroup);
        }

        ngSpr = new AnimatedSprite(0, main.viewport.getWorldHeight() * 0.12F);

        if (Main.random.bool(1)) {
            ngSpr.loadGraphic(Paths.image("newgrounds_logo_classic"));
        } else if (Main.random.bool(30)) {
            ngSpr.loadGraphic(Paths.image("newgrounds_logo_animated"), true, 600);
            ngSpr.animation.add("idle", new Integer[]{0, 1}, 4);
            ngSpr.animation.play("idle");
            ngSpr.setSize(ngSpr.getWidth() * 0.55f, 0);
            ngSpr.setY(ngSpr.getY() + 25);
        } else {
            ngSpr.loadGraphic(Paths.image("newgrounds_logo"));
            ngSpr.setSize(ngSpr.getWidth() * 0.8f, ngSpr.getWidth() * 0.8f);
        }

        add(ngSpr);
        ngSpr.setVisible(false);

        ngSpr.updateHitbox();
        ngSpr.screenCenter(Axes.X);

        if (initialized) skipIntro();
        else initialized = true;
    }

    /**
     * After sitting on the title screen for a while, transition to the attract screen.
     */
    public void moveToAttract() {
        Main.sound.music.fadeOut(2.0f, 0);
        stage.addAction(Actions.sequence(Actions.fadeIn(2f), Actions.run(() -> main.switchState(new AttractState(main)))));
    }

    public void playMenuMusic() {
        Main.sound.playMusic("freakyMenu", new FunkinSound.FunkinSoundPlayMusicParams.Builder()
            .overrideExisting(true)
            .restartTrack(false)
            .persist(true)
            .build());
        Main.sound.music.fadeIn(4000f);
    }

    public Array<Array<String>> getIntroTextShit() {
        String fullText = Assets.getText(Paths.txt("introText"));

        String[] firstArray = fullText.split("\\R");
        Array<Array<String>> swagGoodArray = new Array<>();

        for (String i : firstArray) {
            String[] parts = i.split("--");

            Array<String> inner = new Array<>(parts.length);
            for (String part : parts) inner.add(part.trim());
            swagGoodArray.add(inner);
        }

        return swagGoodArray;
    }

    public boolean transitioning = false;
    private Timer.Task timer;

    @Override
    public void render(float delta) {

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit();

        Conductor.getInstance().update();

        boolean pressedEnter = Gdx.input.isKeyJustPressed(Input.Keys.ENTER);
        if (pressedEnter && transitioning && skippedIntro) {
            if (timer != null) timer.cancel();
            moveToMainMenu();
        }

        if (pressedEnter && !transitioning && skippedIntro) {
            titleText.animation.play("press");
            CameraFlash.getInstance().flash(Color.WHITE, 1);
            Main.sound.playOnce(Paths.sound("confirmMenu"), 0.7f);
            transitioning = true;

            timer = Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    moveToMainMenu();
                }
            }, 2f);
        }
        if (pressedEnter && !skippedIntro && initialized) skipIntro();

        super.render(delta);
    }

    public void moveToMainMenu() {
        if (attractTimer != null) {
            attractTimer.cancel();
            attractTimer = null;
        }
        main.switchState(new MainMenuState(main), true, false);
    }

    public void createCoolText(Array<String> textArray) {
        if (credGroup == null || textGroup == null) return;

        for (int i = 0; i < textArray.size; i++) {
            AtlasText money = new AtlasText(0, 0, textArray.get(i), AtlasText.AtlasFont.BOLD);
            money.screenCenter(Axes.X);
            money.setY(money.getY() - (i * 60) + 400);
            textGroup.addActor(money);
        }
    }

    public void addMoreText(String text) {
        if (credGroup == null || textGroup == null) return;

        AtlasText coolText = new AtlasText(0, 0, text.trim(), AtlasText.AtlasFont.BOLD);
        coolText.screenCenter(Axes.X);
        coolText.setY(coolText.getY() - (textGroup.getChildren().size * 60) + 400);
        textGroup.addActor(coolText);
    }

    public void deleteCoolText() {
        if (credGroup == null || textGroup == null) return;

        while (textGroup.getChildren().size > 0) {
            textGroup.removeActor(textGroup.getChild(0), true);
        }
    }

    public boolean skippedIntro = false;

    @Override
    public void beatHit(Signal<Integer> integerSignal, Integer beat) {
        super.beatHit(integerSignal, beat);

        if (!skippedIntro) {
            if (beat > lastBeat) {
                for (int i = lastBeat; i < beat; i++) {
                    switch (i + 1) {
                        case 1:
                            createCoolText(new Array<>(new String[]{"The", "Funkin Crew Inc"}));
                            break;
                        case 3:
                            addMoreText("presents");
                            break;
                        case 4:
                            deleteCoolText();
                            break;
                        case 5:
                            createCoolText(new Array<>(new String[]{"In association", "with"}));
                            break;
                        case 7:
                            addMoreText("newgrounds");
                            if (ngSpr != null) ngSpr.setVisible(true);
                            break;
                        case 8:
                            deleteCoolText();
                            if (ngSpr != null) ngSpr.setVisible(false);
                            break;
                        case 9:
                            createCoolText(new Array<>(new String[]{curWacky.get(0)}));
                            break;
                        case 11:
                            addMoreText(curWacky.get(1));
                            break;
                        case 12:
                            deleteCoolText();
                            break;
                        case 13:
                            addMoreText("Friday");
                            break;
                        case 14:
                            // easter egg for when the game is trending with the wrong spelling
                            // the random intro text would be "trending--only on x"

                            if (curWacky.get(0).equals("trending")) addMoreText("Nigth");
                            else addMoreText("Night");
                            break;
                        case 15:
                            addMoreText("Funkin");
                            break;
                        case 16:
                            skipIntro();
                            break;
                    }
                }
            }
            lastBeat = beat;
        }
        if (skippedIntro) {
            if (logoBl != null && logoBl.animation != null) logoBl.animation.play("bump", true);

            danceLeft = !danceLeft;

            if (gfDance != null && gfDance.animation != null) {
                if (danceLeft) gfDance.animation.play("danceRight");
                else gfDance.animation.play("danceLeft");
            }
        }
    }

    public void skipIntro() {
        if (!skippedIntro) {
            ngSpr.remove();

            CameraFlash.getInstance().flash(Color.WHITE, initialized ? 1 : 4);

            if (credGroup != null) credGroup.remove();
            skippedIntro = true;
        }
        attractTimer = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                moveToAttract();
            }
        }, Constants.TITLE_ATTRACT_DELAY);
    }
}
