package com.nova.fnfjava.ui;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.nova.fnfjava.AnimatedSprite;
import com.nova.fnfjava.Conductor;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.system.frontEnds.SoundManager;

/**
 * Title screen of the application. Displayed after the application is created.
 */
public class TitleScreen extends MusicBeatState {
    final Main main;

    public AnimatedSprite logoBl;

    public AnimatedSprite gfDance;
    public boolean danceLeft = false;

    public AnimatedSprite titleText;

    public TitleScreen(Main main) {
        this.main = main;
    }

    @Override
    public void show() {
        super.show();

        playMenuMusic();

        logoBl = new AnimatedSprite(0, 0);
        logoBl.atlas = new TextureAtlas("images/logoBumpin.atlas");
        logoBl.animation.addByPrefix("bump", "logo bumpin", 24);

        gfDance = new AnimatedSprite(0,0);
        gfDance.atlas = new TextureAtlas("images/gfDanceTitle.atlas");
        gfDance.animation.addByIndices("danceLeft", "gfDance", new Array<>(new Integer[]{30, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14}), 24);
        gfDance.animation.addByIndices("danceRight", "gfDance", new Array<>(new Integer[]{15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29}), 24);

        titleText = new AnimatedSprite(100, Gdx.graphics.getHeight() * 0.2F);
        titleText.atlas = new TextureAtlas("images/titleEnter.atlas");
        titleText.animation.addByPrefix("idle", "Press Enter to Begin", 24);
        titleText.animation.addByPrefix("enter", "ENTER PRESSED", 24);
    }

    public void playMenuMusic() {
        Music music = Gdx.audio.newMusic(Gdx.files.internal("music/freakyMenu/freakyMenu.ogg"));
        Main.sound.playMusic(music);
    }

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

    @Override
    public void render(float delta) {
        Conductor.getInstance().update();

        input();
        logoBl.act(delta);
        gfDance.act(delta);
        titleText.act(delta);
        draw();
    }

    public void draw() {
        ScreenUtils.clear(Color.BLACK);

        main.viewport.apply();
        main.spriteBatch.setProjectionMatrix(main.viewport.getCamera().combined);

        main.spriteBatch.begin();

        logoBl.draw(main.spriteBatch, 1.0f);
        gfDance.draw(main.spriteBatch, 1.0f);
        titleText.draw(main.spriteBatch, 1.0f);

        main.spriteBatch.end();
    }

    public void input() {
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            titleText.playLoop("enter");
        }
    }

    @Override
    public void resize(int width, int height) {
        main.viewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        main.spriteBatch.dispose();
    }
}
