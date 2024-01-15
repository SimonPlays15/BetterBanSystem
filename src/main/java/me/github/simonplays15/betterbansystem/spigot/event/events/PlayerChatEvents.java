package me.github.simonplays15.betterbansystem.spigot.event.events;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.automod.AutoModAction;
import me.github.simonplays15.betterbansystem.core.automod.AutoModActionParameters;
import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import me.github.simonplays15.betterbansystem.core.mute.MuteHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PlayerChatEvents implements Listener {

    private final Map<UUID, Integer> playerSpamMap = new HashMap<>();
    private final Map<UUID, String> playerDuplicatedTextMap = new HashMap<>();
    private final Map<UUID, Map<AutoModAction, Integer>> playerActionMap = new HashMap<>();

    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(10);

    @EventHandler
    public void onChat(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (MuteHandler.findMuteEntry(player.getUniqueId()) != null) {
            event.setCancelled(true);
            player.sendMessage(BetterBanSystem.getInstance().getPrefix() + "§4You cannot chat while you're muted.");
            return;
        }


        if (BetterBanSystem.getInstance().getConfig().getBoolean("automod.use")) {
            if (BetterBanSystem.getInstance().getPermissionsManager().hasPermission(player.getName(), "betterbansystem.exempts.automod"))
                return;

            // Spamming
            if (BetterBanSystem.getInstance().getConfig().getBoolean("automod.modules.chat.spamming.use")) {
                int maxSends = BetterBanSystem.getInstance().getConfig().getInt("automod.modules.chat.spamming.maxMessages", 5);
                if (maxSends == 0 || maxSends < 0)
                    return;

                playerSpamMap.compute(player.getUniqueId(), (k, v) -> (v == null) ? 1 : v + 1);
                service.schedule(() -> {
                    playerSpamMap.computeIfPresent(player.getUniqueId(), (k, v) -> v == 0 ? 0 : v - 1);
                }, 4, TimeUnit.SECONDS);
                int messagesSend = playerSpamMap.get(player.getUniqueId());
                if (messagesSend >= (maxSends - 1))
                    player.sendMessage(BetterBanSystem.getInstance().getPrefix() + "§ePlease slow down in Chat or you get warned!");

                if (messagesSend > maxSends) {
                    executeActionsFromConfig(event, player, "automod.modules.chat.spamming.action");
                }
            }
            // Duplicated Text
            if (BetterBanSystem.getInstance().getConfig().getBoolean("automod.modules.chat.duplicatedText.use")) {
                String message = event.getMessage();
                String duplicatedText = playerDuplicatedTextMap.get(player.getUniqueId());
                if (duplicatedText != null && duplicatedText.equalsIgnoreCase(message)) {
                    executeActionsFromConfig(event, player, "automod.modules.chat.duplicatedText.action");
                    player.sendMessage(BetterBanSystem.getInstance().getPrefix() + "§ePlease do not send duplicated messages!");
                }
                playerDuplicatedTextMap.compute(player.getUniqueId(), (k, v) -> message);
            }
            // CapsLock
            if (BetterBanSystem.getInstance().getConfig().getBoolean("automod.modules.chat.capslock.use")) {
                int maxCapsPercentage = 70;
                String message = event.getMessage();
                int totalChars = message.length();
                int capsChars = 0;
                for (char c : message.toCharArray()) {
                    if (Character.isUpperCase(c))
                        capsChars++;
                }
                int capsPercentage = (capsChars * 100) / totalChars;
                if (capsPercentage >= maxCapsPercentage) {
                    executeActionsFromConfig(event, player, "automod.modules.chat.capslock.action");
                    player.sendMessage(BetterBanSystem.getInstance().getPrefix() + "§ePlease do not use excessive caps in Chat!");
                }
            }
            // Message Contains Links
            if (BetterBanSystem.getInstance().getConfig().getBoolean("automod.modules.chat.links.use")) {
                String message = event.getMessage();
                if (message.matches(".*\\bhttps?://[^ ]*\\b.*")) {
                    executeActionsFromConfig(event, player, "automod.modules.chat.links.action");
                    player.sendMessage(BetterBanSystem.getInstance().getPrefix() + "§ePlease do not share links in Chat!");
                }
            }
            // Bad Words
            if (BetterBanSystem.getInstance().getConfig().getBoolean("automod.modules.chat.badwords.use")) {
                String message = event.getMessage();
                List<String> badWords = BetterBanSystem.getInstance().getConfig().getStringList("automod.modules.chat.badwords.badWordList");
                if (BetterBanSystem.getInstance().getConfig().getBoolean("automod.modules.chat.badwords.replaceWords")) {
                    event.setMessage(checkAndReplaceProfanityWords(message, badWords));
                } else
                    for (String word : badWords) {
                        if (message.toLowerCase().contains(word.toLowerCase())) {
                            executeActionsFromConfig(event, player, "automod.modules.chat.badwords.action");
                        }
                        player.sendMessage(BetterBanSystem.getInstance().getPrefix() + "§ePlease do not use inappropriate language in Chat!");
                        break;
                    }
            }
        }
    }

    /**
     * Executes the actions specified in the configuration for a chat event.
     *
     * @param event      The chat event that triggered the execution of actions.
     * @param player     The player associated with the event.
     * @param configPath The path to the configuration entry containing the actions to execute.
     */
    private void executeActionsFromConfig(@NotNull AsyncPlayerChatEvent event, Player player, String configPath) {
        AutoModAction[] actions = AutoModAction.parseActions(BetterBanSystem.getInstance().getConfig().getString(configPath, "DELETE"));
        for (AutoModAction action : actions) {
            GlobalLogger.getLogger().debug("Running action: " + action.toString());
            switch (action.getType()) {
                case WARN -> processActionWithLimitCheck("warn", player, action, AutoModActionParameters.REASON);
                case DELETE -> event.setCancelled(true);
                case MUTE ->
                        processActionWithLimitCheck("mute", player, action, AutoModActionParameters.DURATION, AutoModActionParameters.REASON);
                default -> GlobalLogger.getLogger().debug("Failed to retrieve AutoModAction for", configPath);
            }
        }
    }

    /**
     * Checks the given message for profanity words and replaces them with asterisks.
     *
     * @param message The message to check and replace profanity words.
     * @param words   The list of profanity words to check.
     * @return The message with profanity words replaced with asterisks.
     */
    private String checkAndReplaceProfanityWords(String message, @NotNull List<String> words) {
        for (String word : words) {
            String regex = "\\b" + Pattern.quote(word) + "\\b";
            Pattern wordPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher wordMatcher = wordPattern.matcher(message);

            String replacement = "*".repeat(word.length());
            message = wordMatcher.replaceAll(replacement);
        }
        return message;
    }

    /**
     * Processes an action with a limit check.
     *
     * @param command    the command associated with the action
     * @param player     the player executing the command
     * @param action     the auto moderation action to be performed
     * @param parameters optional parameters for the action
     */
    private void processActionWithLimitCheck(String command, Player player, AutoModAction action, AutoModActionParameters... parameters) {
        incrementPlayerActionCount(player, action);
        if (hasPlayerExceededActionLimit(player, action)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    String commandArgs = Arrays.stream(parameters)
                            .map(action::getParameter)
                            .collect(Collectors.joining(" "));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command + " " + player.getName() + " " + commandArgs);
                }
            }.runTask(((JavaPlugin) BetterBanSystem.getInstance().getRunningPlugin()));
        }
    }

    /**
     * Checks if a player has exceeded the action limit for a specific AutoMod action.
     *
     * @param player The player to check for.
     * @param action The AutoMod action to check against.
     * @return True if the player has exceeded the action limit, false otherwise.
     */
    private boolean hasPlayerExceededActionLimit(@NotNull Player player, @NotNull AutoModAction action) {

        if (action.getActionLimit() < 1)
            return true;

        Integer playerActionCount = playerActionMap.getOrDefault(player.getUniqueId(), Collections.emptyMap()).get(action);

        return playerActionCount != null && playerActionCount >= action.getActionLimit();
    }


    /**
     * This method increments the action count for a player in the playerActionMap.
     *
     * @param player The player to increment the action count for.
     * @param action The AutoModAction to increment the count of.
     */
    private void incrementPlayerActionCount(@NotNull Player player, AutoModAction action) {
        playerActionMap.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>(Map.of(action, 0)))
                .computeIfPresent(action, (act, count) -> count + 1);
        service.schedule(() -> {
            decrementPlayerActionCount(player, action);
        }, 6, TimeUnit.SECONDS);
    }

    /**
     * This method increments the action count for a player in the playerActionMap.
     *
     * @param player The player to increment the action count for.
     * @param action The AutoModAction to increment the count of.
     */
    private void decrementPlayerActionCount(@NotNull Player player, AutoModAction action) {
        playerActionMap.computeIfPresent(player.getUniqueId(), (id, actions) -> {
            actions.computeIfPresent(action, (act, count) -> count > 0 ? count - 1 : 0);
            return actions;
        });
    }

    private boolean findBlockedCommand(String command) {
        List<String> blockedCommands = BetterBanSystem.getInstance().getConfig().getStringList("mute.blocked-commands");
        Optional<String> b = blockedCommands.stream().filter(g -> g.equalsIgnoreCase(command) || g.equalsIgnoreCase("minecraft:" + command) || g.equalsIgnoreCase("bukkit:" + command)).findFirst();
        return b.isPresent();
    }

    @EventHandler
    public void onCommandSend(@NotNull PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (MuteHandler.findMuteEntry(player.getUniqueId()) != null) {
            String command = event.getMessage().split(" ")[0].replaceAll("/", "");
            if (findBlockedCommand(command)) {
                event.setCancelled(true);
                player.sendMessage(BetterBanSystem.getInstance().getPrefix() + "§4You cannot use this command while you're muted.");
            }
        }

    }

}
