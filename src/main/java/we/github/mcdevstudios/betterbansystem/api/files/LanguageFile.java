/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.files;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.core.chat.ChatColor;
import we.github.mcdevstudios.betterbansystem.core.logging.GlobalLogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class LanguageFile {
    private final Map<String, String> messages;

    public LanguageFile(String languagePath) {
        this.messages = loadLanguageFile(languagePath);
    }

    private Map<String, String> loadLanguageFile(String languagePath) {
        if (languagePath == null || languagePath.isEmpty()) {
            throw new NullPointerException("languagePath cannot be empty or null");
        }
        Yaml yaml = new Yaml();
        try (FileInputStream input = new FileInputStream(languagePath)) {
            return yaml.load(input);
        } catch (IOException ex) {
            GlobalLogger.getLogger().error("Failed to load language File", languagePath, "falling back to default language file", ex);
            try (InputStream input = new ResourceFile(BetterBanSystem.getInstance().getDataFolder()).getResource("language/en_US.yml")) {
                return yaml.load(input);
            } catch (IOException e) {
                GlobalLogger.getLogger().error("Failed to load default Language File", languagePath, e);
            }
        }
        return new HashMap<>();
    }

    public String getMessage(String key) {
        return getMessage(key, new HashMap<>());
    }

    public String getMessage(String key, @NotNull Map<String, String> placeholders) {

        String message = messages.getOrDefault(key, "Fai§kled§r to l§ko§rad Me§kssag§re");

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String replacement = entry.getValue();

            message = message.replace(placeholder, replacement);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
