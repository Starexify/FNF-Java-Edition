package com.nova.fnfjava.util;

import com.badlogic.gdx.utils.Array;

import java.util.Comparator;

public class SortUtil {

    /**
     * Sort predicate for sorting strings alphabetically.
     * @param a The first string to compare.
     * @param b The second string to compare.
     * @return 1 if `a` comes before `b`, -1 if `b` comes before `a`, 0 if they are equal
     */
    public static int alphabetically(String a, String b) {
        if (a == null && b == null) return 0;
        if (a == null) return 1;
        if (b == null) return -1;

        a = a.toUpperCase();
        b = b.toUpperCase();

        // Sort alphabetically. Yes that's how this works.
        return a.equals(b) ? 0 : a.compareTo(b) > 0 ? 1 : -1;
    }

    /**
     * Sort predicate which sorts two strings alphabetically, but prioritizes specific strings first.
     * Example usage: array.sort(defaultsThenAlphabetically(baseGameIds)) will sort prioritizing baseGameIds order first.
     *
     * @param defaultValues The values to prioritize in order.
     * @return A comparator that can be used with Array.sort()
     */
    public static Comparator<String> defaultsThenAlphabetically(Array<String> defaultValues) {
        return (a, b) -> {
            if (a.equals(b)) return 0;

            boolean aIsDefault = defaultValues.contains(a, false);
            boolean bIsDefault = defaultValues.contains(b, false);

            if (aIsDefault && bIsDefault) {
                // Sort by index in defaultValues
                return Integer.compare(
                    defaultValues.indexOf(a, false),
                    defaultValues.indexOf(b, false)
                );
            }
            if (aIsDefault) return -1;
            if (bIsDefault) return 1;
            return alphabetically(a, b);
        };
    }
}
