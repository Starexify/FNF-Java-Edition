package com.nova.fnfjava.ui;

public class TextMenuList extends MenuTypedList<TextMenuList.TextMenuItem> {
    public TextMenuItem createItem(float x, float y, String name, AtlasText.AtlasFont font, Runnable callback, boolean fireInstantly, boolean available) {
        TextMenuItem item = new TextMenuItem(x, y, name, font, callback, available);
        item.fireInstantly = fireInstantly;

        return addItem(name, item);
    }

    public TextMenuItem createItem(float x, float y, String name, AtlasText.AtlasFont font, Runnable callback) {
        return this.createItem(x, y, name, font, callback, false, true);
    }

    public static class TextMenuItem extends TextTypedMenuItem<AtlasText> {
        public AtlasText atlasText;

        public TextMenuItem(float x, float y, String name, AtlasText.AtlasFont font, Runnable callback, boolean available) {
            super(x, y, new AtlasText(0, 0, name, font), name, callback, available);
            atlasText = label;
        }

        public TextMenuItem(String name, Runnable callback) {
            this(0, 0, name, AtlasText.AtlasFont.BOLD, callback, true);
        }
    }

    public static class TextTypedMenuItem<T extends AtlasText> extends MenuTypedList.MenuTypedItem<T> {
        public TextTypedMenuItem(float x, float y, T label, String name, Runnable callback, boolean available) {
            super(x, y, label, name, callback, available);
        }
    }
}
