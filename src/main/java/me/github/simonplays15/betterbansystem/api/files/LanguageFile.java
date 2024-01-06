package me.github.simonplays15.betterbansystem.api.files;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.chat.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LanguageFile extends BaseConfig {

    public LanguageFile(String languagePath) {
        this.load(new File(languagePath));
    }

    public String getMessage(String key) {
        return getMessage(key, new HashMap<>());
    }

    public String getMessage(String key, @NotNull Map<String, String> placeholders) {

        String message = this.getString(key, "Failed to load message. See log for more details.");

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String replacement = entry.getValue();

            message = message.replace(placeholder, replacement);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
