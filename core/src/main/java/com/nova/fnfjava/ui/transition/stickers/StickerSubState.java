package com.nova.fnfjava.ui.transition.stickers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.nova.fnfjava.Assets;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.Paths;
import com.nova.fnfjava.data.stickers.StickerRegistry;
import com.nova.fnfjava.ui.MusicBeatSubState;
import com.nova.fnfjava.ui.freeplay.FreeplayState;
import com.nova.fnfjava.util.Constants;

public class StickerSubState extends MusicBeatSubState {
    public Group grpStickers;

    public StateSupplier targetState;

    public String stickerPackId;
    public StickerPack stickerPack;

    public Array<String> soundSelections = new Array<>();
    public String soundSelection = "";
    public Array<String> sounds = new Array<>();

    public Array<StickerSprite> oldStickers;

    public StickerSubState(Main main, StickerSubStateParams params) {
        super(main);

        this.targetState = params.targetState != null ? params.targetState : sticker -> FreeplayState.build(main, null, sticker);
        this.stickerPackId = params.stickerPack != null ? params.stickerPack : Constants.DEFAULT_STICKER_PACK;
        var targetStickerPack = StickerRegistry.instance.fetchEntry(this.stickerPackId);
        this.stickerPack = targetStickerPack != null ? targetStickerPack : StickerRegistry.instance.fetchDefault();

        this.oldStickers = params.oldStickers();

        var stickerSoundFiles = Assets.listFilesInDirectory("assets/shared/sounds/stickersounds");
        for (String filePath : stickerSoundFiles) {
            if (filePath.contains("/stickersounds/")) {
                String afterSticker = filePath.substring(filePath.indexOf("/stickersounds/") + "/stickersounds/".length());
                String folderName = afterSticker.split("/")[0];

                if (!soundSelections.contains(folderName, false)) soundSelections.add(folderName);
            }
        }

        if (soundSelections.size > 0) {
            soundSelection = soundSelections.random();

            // Get all sound files from the selected folder
            for (String i : stickerSoundFiles) {
                if (i.contains("/stickersounds/" + soundSelection + "/")) {
                    // Convert to the format the game expects: remove "assets/shared/sounds/" and file extension
                    String soundName = i.replace("shared/sounds/", "");
                    int dotIndex = soundName.lastIndexOf('.');
                    if (dotIndex > 0) soundName = soundName.substring(0, dotIndex);
                    sounds.add(soundName);
                }
            }
        }
    }

    @Override
    public void show() {
        super.show();

        grpStickers = new Group();
        add(grpStickers);

        if (oldStickers != null) {
            for (StickerSprite sticker : oldStickers) grpStickers.addActor(sticker);
            degenStickers();
        } else regenStickers();
    }

    public void degenStickers() {
        if (grpStickers.getChildren() == null || grpStickers.getChildren().size == 0) {
            switchingState = false;
            close();
            return;
        }

        for (int ind = 0; ind < grpStickers.getChildren().size; ind++) {
            StickerSprite sticker = (StickerSprite) grpStickers.getChildren().get(ind);
            int finalInd = ind;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    sticker.setVisible(false);
                    String daSound = sounds.random();
                    Main.sound.playOnce(Paths.sound(daSound));

                    if (grpStickers == null || finalInd == grpStickers.getChildren().size - 1) {
                        switchingState = false;
                        // FunkinMemory.clearStickers();
                        close();
                    }
                }
            }, sticker.timing);
        }
    }

    public void regenStickers() {
        if (grpStickers.getChildren().size > 0) grpStickers.clear();

        float xPos = -100;
        float yPos = -100;

        while (xPos <= Gdx.graphics.getWidth()) {
            String stickerPath = stickerPack.getRandomStickerPath(false);
            StickerSprite sticky = new StickerSprite(0, 0, stickerPath);
            sticky.setVisible(false);

            sticky.setX(xPos);
            sticky.setY(yPos);
            xPos += sticky.frameWidth * 0.5;

            if (xPos >= Gdx.graphics.getWidth()) {
                if (yPos <= Gdx.graphics.getHeight()) {
                    xPos = -100;
                    yPos += MathUtils.random(70f, 120f);
                }
            }

            sticky.setRotation(MathUtils.random(-60, 70));
            grpStickers.addActor(sticky);
        }

        grpStickers.getChildren().shuffle();

        String lastStickerPath = stickerPack.getRandomStickerPath(true);
        StickerSprite lastSticker = new StickerSprite(0, 0, lastStickerPath);
        lastSticker.setVisible(false);
        lastSticker.updateHitbox();
        lastSticker.setRotation(0);
        //lastSticker.screenCenter();
        grpStickers.addActor(lastSticker);

        for (int ind = 0; ind < grpStickers.getChildren().size; ind++) {
            StickerSprite sticker = (StickerSprite) grpStickers.getChildren().get(ind);

            sticker.timing = MathUtils.map(0, grpStickers.getChildren().size - 1, 0f, 0.9f, ind);

            final int finalInd = ind;

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (grpStickers == null) return;

                    sticker.setVisible(true);
                    String daSound = sounds.random();
                    Main.sound.playOnce(Paths.sound(daSound));

                    int frameTimer = MathUtils.random(0, 2);

                    // Always make the last one POP
                    if (finalInd == grpStickers.getChildren().size - 1) frameTimer = 2;

                    // Second timer for scaling effect
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            if (sticker == null) return;

                            float scale = MathUtils.random(0.97f, 1.02f);
                            sticker.setScaleX(scale);
                            sticker.setScaleY(scale);

                            // If this is the last sticker, switch state
                            if (finalInd == grpStickers.getChildren().size - 1) {
                                switchingState = true;

                                // Transition to target state
                                if (targetState != null) {
                                    Screen nextScreen = targetState.create(StickerSubState.this);
                                    // Switch to nextScreen using your game's state management
                                    main.setScreen(nextScreen);
                                }
                            }
                        }
                    }, (1f / 24f) * frameTimer);
                }
            }, sticker.timing);
        }
    }

    boolean switchingState = false;

    @Override
    public void close() {
        if (switchingState) return;
        super.close();
    }

    @Override
    public void dispose() {
        if (switchingState) return;
        super.dispose();
    }

    public record StickerSubStateParams(StateSupplier targetState, String stickerPack, Array<StickerSprite> oldStickers) {
        public StickerSubStateParams() {
            this(null, Constants.DEFAULT_STICKER_PACK, null);
        }

        public StickerSubStateParams(StateSupplier targetState) {
            this(targetState, Constants.DEFAULT_STICKER_PACK, null);
        }
    }

    @FunctionalInterface
    public interface StateSupplier {
        Screen create(StickerSubState subState);
    }
}
