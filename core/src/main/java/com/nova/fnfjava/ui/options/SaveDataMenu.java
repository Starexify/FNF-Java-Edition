package com.nova.fnfjava.ui.options;

import com.nova.fnfjava.ui.Page;
import com.nova.fnfjava.ui.TextMenuList;

public class SaveDataMenu extends Page<OptionsState.OptionsMenuPageName> {
    public TextMenuList items;

    public SaveDataMenu() {
        add(items = new TextMenuList());
    }

    public boolean hasMultipleOptions() {
        return items.length > 2;
    }
}
