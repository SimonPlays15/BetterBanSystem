package me.github.simonplays15.betterbansystem.core.chat;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * The ChatColor class provides utility methods for manipulating colors and formatting in chat messages.
 */
public class ChatColor {

    /**
     * The Pattern used to strip color codes from a string.
     */
    public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '§' + "[0-9A-FK-ORX]");
    /**
     * A string of valid characters used for manipulating colors and formatting in chat messages.
     * It contains the digits (0-9) and the following letters: A, a, B, b, C, c, D, d, E, e, F, f, K, k, L, l, M, m, N, n, O, o, R, r, X, x.
     */
    private static final String VALID_CHARS = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";

    /**
     * Translates the alternate color codes in a given string.
     *
     * @param altColorChar    the alternate color code character to search for and replace
     * @param textToTranslate the text to translate the alternate color codes in
     * @return the translated string with alternate color codes replaced
     */
    @Contract("_, _ -> new")
    public static @NotNull String translateAlternateColorCodes(char altColorChar, @NotNull String textToTranslate) {
        char[] b = textToTranslate.toCharArray();

        for (int i = 0; i < b.length - 1; ++i) {
            if (b[i] == altColorChar && VALID_CHARS.indexOf(b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
    }

    /**
     * Removes color formatting from the input string.
     *
     * @param input the string to strip color from
     * @return the input string with color formatting removed
     */
    @Contract("null -> null")
    public static String stripColor(String input) {
        return input == null ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

}
