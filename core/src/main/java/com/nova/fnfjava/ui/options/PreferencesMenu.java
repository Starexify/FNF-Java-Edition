package com.nova.fnfjava.ui.options;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.group.TypedActorGroup;
import com.nova.fnfjava.text.FlxText;
import com.nova.fnfjava.ui.Page;
import com.nova.fnfjava.ui.TextMenuList;

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
    }

    public void createPrefItems() {
        createPrefItemCheckbox("Naughtyness", "If enabled, raunchy content (such as swearing, etc.) will be displayed.");
    }

    public void createPrefItemCheckbox(String prefName, String prefDesc) {
        //CheckboxPreferenceItem checkbox = new CheckboxPreferenceItem(0, 120 * (items.length - 1 + 1));

/*        items.createItem(0, (120 * items.length) + 30, prefName, AtlasFont.BOLD, function() {
            var value = !checkbox.currentValue;
            onChange(value);
            checkbox.currentValue = value;
        }, false, available);

        preferenceItems.add(checkbox);
        preferenceDesc.push(prefDesc);*/
    }
}
