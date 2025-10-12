package com.nova.fnfjava.ui.options;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.Preferences;
import com.nova.fnfjava.group.TypedActorGroup;
import com.nova.fnfjava.text.FlxText;
import com.nova.fnfjava.ui.AtlasText;
import com.nova.fnfjava.ui.Page;
import com.nova.fnfjava.ui.TextMenuList;
import com.nova.fnfjava.ui.options.items.CheckboxPreferenceItem;
import com.nova.fnfjava.util.ImageUtil;

public class PreferencesMenu extends Page<OptionsState.OptionsMenuPageName> {
    TextMenuList items;
    TypedActorGroup preferenceItems;
    Array<String> preferenceDesc = new Array<>();
    FlxText itemDesc;
    Image itemDescBox;

    public PreferencesMenu() {
        add(items = new TextMenuList());
        add(preferenceItems = new TypedActorGroup());
        add(itemDescBox = new Image());

        add(itemDesc = new FlxText(0, 0, ""));

        createPrefItems();
        createPrefDescription();

        items.onChange.add((signal, object) -> itemDesc.setText(preferenceDesc.get(items.selectedIndex)));

        items.selectItem(0);
    }

    public void createPrefDescription() {
        itemDescBox = ImageUtil.createColored(1, 1, Color.BLACK);
        itemDescBox.getColor().a = 0.6f;
        itemDesc.setFormat("VCR OSD Mono", 32, Color.WHITE, FlxText.FlxTextBorderStyle.OUTLINE, Color.BLACK);
        itemDesc.borderSize = 3;

        itemDesc.setText(preferenceDesc.get(items.selectedIndex));
        //itemDesc.screenCenter();
        itemDesc.setY(itemDesc.getY() + 270);

        itemDescBox.setPosition(itemDesc.getX() - 10, itemDesc.getY() - 10);
        itemDescBox.setSize(itemDesc.getWidth() + 20, itemDesc.getHeight() + 25);
        //itemDescBox.updateHitbox();
    }

    public void createPrefItems() {
        createPrefItemCheckbox("Naughtyness", "If enabled, raunchy content (such as swearing, etc.) will be displayed.", Preferences.getNaughtyness());
        createPrefItemCheckbox("Downscroll", "If enabled, this will make the notes move downwards.", Preferences.getDownscroll());
    }

    public void createPrefItemCheckbox(String prefName, String prefDesc, boolean defaultValue, boolean available) {
        CheckboxPreferenceItem checkbox = new CheckboxPreferenceItem(0,  Gdx.graphics.getHeight() - 120 * items.length, defaultValue, available);

        items.createItem(0, Gdx.graphics.getHeight() - (120 * items.length) + 30, prefName, AtlasText.AtlasFont.BOLD, () -> {
            var value = !checkbox.currentValue;
            //onChange(value);
            checkbox.currentValue = value;
        }, false, available);

        preferenceItems.add(checkbox);
        preferenceDesc.add(prefDesc);
    }

    public void createPrefItemCheckbox(String prefName, String prefDesc, boolean defaultValue) {
        createPrefItemCheckbox(prefName, prefDesc, defaultValue, true);
    }
}
