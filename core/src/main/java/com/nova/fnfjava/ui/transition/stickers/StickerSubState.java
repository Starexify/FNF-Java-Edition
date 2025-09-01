package com.nova.fnfjava.ui.transition.stickers;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.ui.MusicBeatSubState;
import com.nova.fnfjava.ui.freeplay.FreeplayState;
import com.nova.fnfjava.util.Constants;

import java.util.function.Function;

public class StickerSubState extends MusicBeatSubState {
    public Group grpStickers;

    public StateSupplier targetState;

    String stickerPackId;
    StickerPack stickerPack;

    Array<String> soundSelections = new Array<>();

    String soundSelection = "";
    Array<String>  sounds = new Array<>();

    public StickerSubState(Main main, StickerSubStateParams params) {
        super(main);

         this.targetState = params.targetState != null ? params.targetState : null; // sticker -> FreeplayState.build(null, sticker)
    }

    public record StickerSubStateParams(StateSupplier targetState, String stickerPack, Array<StickerSprite> oldStickers) {
        public StickerSubStateParams() {
            this(null, Constants.DEFAULT_STICKER_PACK, null);
        }
    }

    @FunctionalInterface
    public interface StateSupplier {
        Screen create(StickerSubState subState);
    }
}
