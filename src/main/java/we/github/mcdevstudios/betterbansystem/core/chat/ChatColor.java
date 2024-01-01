/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.chat;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class ChatColor {

    public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '§' + "[0-9A-FK-ORX]");
    private static final String VALID_CHARS = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";

    /**
     * @param altColorChar    char
     * @param textToTranslate String
     * @return String
     * @apiNote Method extracted from original source of {@link net.md_5.bungee.api.ChatColor} / {@link org.bukkit.ChatColor}
     * @see net.md_5.bungee.api.ChatColor
     * @see org.bukkit.ChatColor
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
     * @param input String
     * @return String
     * @apiNote Method extracted from original source of {@link net.md_5.bungee.api.ChatColor} / {@link org.bukkit.ChatColor}
     * @see net.md_5.bungee.api.ChatColor
     * @see org.bukkit.ChatColor
     */
    public static String stripColor(String input) {
        return input == null ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

}
