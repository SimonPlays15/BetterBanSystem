/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.language;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;
import we.github.mcdevstudios.betterbansystem.BetterBanSystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LanguageFile {
    private final Map<String, String> messages;

    public LanguageFile(String languagePath) {
        this.messages = loadLanguageFile(languagePath);
    }

    private Map<String, String> loadLanguageFile(String languagePath) {
        Yaml yaml = new Yaml();
        try (FileInputStream input = new FileInputStream(languagePath)) {
            return yaml.load(input);
        } catch (IOException ex) {
            BetterBanSystem.getGlobalLogger().error("Failed to load language File", languagePath, "falling back to default language file", ex);
            try (FileInputStream input = new FileInputStream("en_US.yml")) {
                return yaml.load(input);
            } catch (IOException e) {
                BetterBanSystem.getGlobalLogger().error("Failed to load default Language File", languagePath, e);
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

        return BetterBanSystem.getPrefix() + ChatColor.translateAlternateColorCodes('&', message);
    }
}
