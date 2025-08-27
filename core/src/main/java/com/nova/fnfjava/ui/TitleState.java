package com.nova.fnfjava.ui;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.AnimatedSprite;
import com.nova.fnfjava.Conductor;
import com.nova.fnfjava.Main;

/**
 * Title screen of the application. Displayed after the application is created.
 */
public class TitleState extends MusicBeatState {
    public static boolean initialized = false;

    public AnimatedSprite logoBl;
    public AnimatedSprite gfDance;
    public boolean danceLeft = false;

    public AnimatedSprite titleText;

    public TitleState(Main main) {
        super(main);
    }

    @Override
    public void show() {
        super.show();

        startIntro();
    }

    public void startIntro() {
        if (!initialized || Main.sound.music == null) playMenuMusic();

        logoBl = new AnimatedSprite( -150, 100);
        logoBl.atlas = new TextureAtlas("images/logoBumpin.atlas");
        logoBl.animation.addByPrefix("bump", "logo bumpin", 24);
        logoBl.animation.play("bump");

        gfDance = new AnimatedSprite((Gdx.graphics.getHeight() * 0.71F), Gdx.graphics.getHeight() * 0.01F);
        gfDance.atlas = new TextureAtlas("images/gfDanceTitle.atlas");
        gfDance.animation.addByIndices("danceLeft", "gfDance", new Array<>(new Integer[]{30, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14}), 24);
        gfDance.animation.addByIndices("danceRight", "gfDance", new Array<>(new Integer[]{15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29}), 24);

        add(logoBl);

        add(gfDance);

        titleText = new AnimatedSprite(100, main.viewport.getWorldHeight() * 0.2F);
        titleText.atlas = new TextureAtlas("images/titleEnter.atlas");
        titleText.animation.addByPrefix("idle", "Press Enter to Begin", 24);
        titleText.animation.addByPrefix("enter", "ENTER PRESSED", 24);
        titleText.animation.play("idle");
        add(titleText);

        if (initialized) skipIntro();
        else initialized = true;
    }

    public void playMenuMusic() {
        Music music = Gdx.audio.newMusic(Gdx.files.internal("music/freakyMenu/freakyMenu.ogg"));
        Main.sound.playMusic(music);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Conductor.getInstance().update();

        input();
    }

    public void input() {
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            titleText.animation.play("enter");
            main.switchState(new MainMenuState(main));
        }
    }

    public boolean skippedIntro = false;

    @Override
    public void beatHit(Signal<Integer> integerSignal, Integer beat) {
        super.beatHit(integerSignal, beat);
        Gdx.app.log("Conductor Test", "Cur beat: " + beat);
        danceLeft = !danceLeft;
        if (gfDance != null && gfDance.animation != null) {
            if (danceLeft) gfDance.animation.play("danceRight");
            else gfDance.animation.play("danceLeft");
        }
    }

    public void skipIntro() {
        if (!skippedIntro) {

            skippedIntro = true;
        }
    }
}
