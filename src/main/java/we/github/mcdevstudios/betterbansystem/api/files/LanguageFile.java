/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.files;

import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.core.chat.ChatColor;

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
