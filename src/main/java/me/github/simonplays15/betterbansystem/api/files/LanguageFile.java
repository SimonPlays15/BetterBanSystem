package me.github.simonplays15.betterbansystem.api.files;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.chat.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a language file.
 * It extends the BaseConfig class.
 */
public class LanguageFile extends BaseConfig {

    /**
     * Constructs a LanguageFile object by loading the language file from
     * the specified path.
     *
     * @param languagePath The path of the language file to load.
     */
    public LanguageFile(String languagePath) {
        this.load(new File(languagePath));
    }

    /**
     * Retrieves a message by its key.
     *
     * @param key the key of the message
     * @return the message corresponding to the key, or a default error message if the key is not found
     * @throws NullPointerException if the key is null
     */
    public String getMessage(String key) {
        return getMessage(key, new HashMap<>());
    }

    /**
     * Retrieves the message with the given key and placeholders.
     *
     * @param key          the key of the message to retrieve
     * @param placeholders the placeholders to replace in the message
     * @return the formatted message with translated colors
     */
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
