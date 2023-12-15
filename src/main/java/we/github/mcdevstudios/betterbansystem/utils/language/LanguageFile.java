/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.utils.language;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
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
        try (FileInputStream input = new FileInputStream(new File(languagePath))) {
            Yaml yaml = new Yaml();
            return yaml.load(input);
        } catch (IOException ex) {
            // TODO add correct logging
            ex.printStackTrace();
        }
        return new HashMap<>();
    }

    public String getMessage(String key) {
        // TODO Add automatic string replaces (PLAYER, REASON, ...)
        return messages.getOrDefault(key, "Message not found");
    }
}
