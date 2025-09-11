package com.nova.fnfjava.util;

import java.text.DecimalFormat;

public class StringTools {
    public static String toTitleCase(String value) {
        if (value == null || value.isEmpty()) return value;

        String[] words = value.split(" ");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1).toLowerCase());
                }
            }
            if (i < words.length - 1) {
                result.append(" ");
            }
        }

        return result.toString();
    }

    public static String formatMoney(long value, boolean showCurrencySymbol, boolean commaSeparated) {
        DecimalFormat df = commaSeparated ? new DecimalFormat("#,###") : new DecimalFormat("#");
        String result = df.format(value);

        if (showCurrencySymbol) result = "$" + result;

        return result;
    }
}
