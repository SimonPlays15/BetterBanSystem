package me.github.simonplays15.betterbansystem.bungeecord.event.events;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.automod.AutoModAction;
import me.github.simonplays15.betterbansystem.core.automod.AutoModActionParameters;
import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import me.github.simonplays15.betterbansystem.core.mute.MuteHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Represents a class that handles chat events.
 */
public class ChatEvents implements Listener {

    /**
     * Map used to keep track of the spam count for each player.
     * The key of the map is the UUID of the player, and the value is the spam count as an Integer.
     */
    private final Map<UUID, Integer> playerSpamMap = new HashMap<>();
    /**
     * A private final variable that represents a map of duplicated text by player.
     * The keys in the map are UUIDs representing player IDs, and the values are the corresponding duplicated text.
     * <p>
     * This variable is part of the ChatEvents class and is used in various methods to track duplicated text
     * entered by players during chat events. It is an instance of the HashMap class.
     * <p>
     * This variable is accessed and modified by several methods in the ChatEvents class, including:
     * - onChat(ChatEvent event)
     * - executeActionsFromConfig(ChatEvent event, ProxiedPlayer player, String configPath)
     * - checkAndReplaceProfanityWords(String message, List<String> words)
     * - processActionWithLimitCheck(String command, ProxiedPlayer player, AutoModAction action, AutoModActionParameters... parameters)
     * - hasProxiedPlayerExceededActionLimit(ProxiedPlayer player, AutoModAction action)
     * - incrementProxiedPlayerActionCount(ProxiedPlayer player, AutoModAction action)
     * - findBlockedCommand(String command)
     * <p>
     * This variable is a member of the ChatEvents class, which inherits from java.lang.Object and implements
     * net.md_5.bungee.api.plugin.Listener.
     */
    private final Map<UUID, String> playerDuplicatedTextMap = new HashMap<>();
    /**
     * Represents a map that stores the number of times each AutoModAction has been performed by each player.
     * The map is organized by player UUID, with each UUID mapped to a map of AutoModAction and its corresponding count.
     * <p>
     * The playerActionMap variable is a private and final instance of a HashMap, specifically a Map implementation.
     * It ensures thread safety by being marked as final, which means it cannot be reassigned once initialized.
     * <p>
     * The outer Map uses UUID as the key, representing a unique identifier for each player.
     * The inner Map uses AutoModAction as the key, representing various actions performed by the player.
     * Integer is used as the value, storing the count of how many times the action has been performed by the player.
     * <p>
     * Example usage:
     * <p>
     * // Create a new instance of playerActionMap
     * private final Map<UUID, Map<AutoModAction, Integer>> playerActionMap = new HashMap<>();
     * <p>
     * // Add an action count for a player
     * UUID playerId = UUID.randomUUID();
     * AutoModAction action = AutoModAction.BAN;
     * int count = 1;
     * playerActionMap.computeIfAbsent(playerId, k -> new HashMap<>()).put(action, count);
     * <p>
     * // Retrieve the action count for a specific player and action
     * int actionCount = playerActionMap.getOrDefault(playerId, Collections.emptyMap()).getOrDefault(action, 0);
     * <p>
     * // Update the action count for a specific player and action
     * playerActionMap.computeIfPresent(playerId, (k, v) -> {
     * v.put(action, actionCount + 1);
     * return v;
     * });
     * <p>
     * // Remove an action count for a player and action
     * playerActionMap.computeIfPresent(playerId, (k, v) -> {
     * v.remove(action);
     * return v;
     * });
     * <p>
     * // Clear all action counts for a player
     * playerActionMap.remove(playerId);
     */
    private final Map<UUID, Map<AutoModAction, Integer>> playerActionMap = new HashMap<>();

    /**
     * The variable 'service' represents a scheduled executor service that manages a pool of threads.
     * It is a private final field of the class 'ChatEvents' and is of type ScheduledExecutorService.
     * <p>
     * This service is initialized using the 'Executors.newScheduledThreadPool(10)' method, which creates
     * a thread pool of 10 threads. This means that the service can schedule and execute tasks concurrently,
     * allowing for efficient execution of scheduled tasks.
     * <p>
     * As this variable is private and final, it cannot be modified or accessed from outside the class.
     * <p>
     * Note: The documentation provided does not contain any example code, author tags, or version tags as requested.
     */
    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(10);

    /**
     * Handles the chat event.
     *
     * @param event The chat event.
     */
    @EventHandler
    public void onChat(ChatEvent event) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayers().stream().filter(g -> g.getPendingConnection().getSocketAddress().equals(event.getSender().getSocketAddress())).findFirst().orElse(null);
        if (player == null)
            return;

        if (MuteHandler.findMuteEntry(player.getUniqueId()) != null) {
            if (event.isCommand()) {
                String command = event.getMessage().split(" ")[0].replaceAll("/", "");
                if (findBlockedCommand(command)) {
                    event.setCancelled(true);
                    player.sendMessage(new TextComponent(BetterBanSystem.getInstance().getPrefix() + "§4You cannot use this command while you're muted."));
                }
            } else {
                event.setCancelled(true);
                player.sendMessage(new TextComponent(BetterBanSystem.getInstance().getPrefix() + "§4You cannot chat while you're muted."));
            }

            return;
        }


        if (!BetterBanSystem.getInstance().getConfig().getBoolean("automod.use")) {
            return;
        }
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
            }, 3, TimeUnit.SECONDS);
            int messagesSend = playerSpamMap.get(player.getUniqueId());
            if (messagesSend >= (maxSends - 1))
                player.sendMessage(new TextComponent(BetterBanSystem.getInstance().getPrefix() + "§ePlease slow down in Chat or you get warned!"));

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
                player.sendMessage(new TextComponent(BetterBanSystem.getInstance().getPrefix() + "§ePlease do not send duplicated messages!"));
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
                player.sendMessage(new TextComponent(BetterBanSystem.getInstance().getPrefix() + "§ePlease do not use excessive caps in Chat!"));
            }
        }
        // Message Contains Links
        if (BetterBanSystem.getInstance().getConfig().getBoolean("automod.modules.chat.links.use")) {
            String message = event.getMessage();
            if (message.matches(".*\\bhttps?://[^ ]*\\b.*")) {
                executeActionsFromConfig(event, player, "automod.modules.chat.links.action");
                player.sendMessage(new TextComponent(BetterBanSystem.getInstance().getPrefix() + "§ePlease do not share links in Chat!"));
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
                    player.sendMessage(new TextComponent(BetterBanSystem.getInstance().getPrefix() + "§ePlease do not use inappropriate language in Chat!"));
                    break;
                }
        }
    }

    /**
     * Executes actions from the given configuration path based on the ChatEvent and player.
     * The actions are parsed from the configuration and processed accordingly.
     *
     * @param event      The ChatEvent object representing the chat event.
     * @param player     The ProxiedPlayer object representing the player involved in the chat event.
     * @param configPath The String representing the path of the configuration to retrieve actions from.
     */
    private void executeActionsFromConfig(@NotNull ChatEvent event, ProxiedPlayer player, String configPath) {
        AutoModAction[] actions = AutoModAction.parseActions(BetterBanSystem.getInstance().getConfig().getString(configPath, "DELETE"));
        for (AutoModAction action : actions) {
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
     * @param message The message to check and replace.
     * @param words   The list of profanity words to match.
     * @return The modified message with profanity words replaced by asterisks.
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
     * Processes the given action with a limit check.
     *
     * @param command    the command to be executed
     * @param player     the player associated with the action
     * @param action     the action to be performed
     * @param parameters optional parameters for the action
     */
    private void processActionWithLimitCheck(String command, ProxiedPlayer player, AutoModAction action, AutoModActionParameters... parameters) {
        if (hasProxiedPlayerExceededActionLimit(player, action)) {
            String commandArgs = Arrays.stream(parameters)
                    .map(action::getParameter)
                    .collect(Collectors.joining(" "));
            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), command + " " + player.getName() + " " + commandArgs);
        } else {
            incrementProxiedPlayerActionCount(player, action);
        }
    }

    /**
     * Checks if the given {@link ProxiedPlayer} has exceeded the action limit for the specified {@link AutoModAction}.
     *
     * @param player The proxied player to check.
     * @param action The auto mod action to check against.
     * @return {@code true} if the player has exceeded the action limit, {@code false} otherwise.
     */
    private boolean hasProxiedPlayerExceededActionLimit(@NotNull ProxiedPlayer player, @NotNull AutoModAction action) {

        if (action.getActionLimit() < 1)
            return true;

        Integer playerActionCount = playerActionMap.getOrDefault(player.getUniqueId(), Collections.emptyMap()).get(action);

        return playerActionCount != null && playerActionCount >= action.getActionLimit();
    }


    /**
     * Increases the action count for a given ProxiedPlayer and AutoModAction.
     * If the player does not have an existing action map, a new map will be created.
     * If the action already exists in the map, its count will be incremented by 1.
     * If the action does not exist in the map, it will be added with a count of 1.
     *
     * @param player The ProxiedPlayer for which to increment the action count.
     * @param action The AutoModAction to increment the count for.
     */
    private void incrementProxiedPlayerActionCount(@NotNull ProxiedPlayer player, AutoModAction action) {
        playerActionMap.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>(Map.of(action, 0)))
                .computeIfPresent(action, (act, count) -> count + 1);
        service.schedule(() -> {
            decrementPlayerActionCount(player, action);
        }, 6, TimeUnit.SECONDS);
    }

    /**
     * Decrements the action count for a specific player and action.
     * If the player and action combination exists in the playerActionMap, the count is decremented.
     * If the count is already zero, it remains unchanged.
     *
     * @param player The player for whom the count needs to be decremented. Must not be null.
     * @param action The action for which the count needs to be decremented. Must not be null.
     */
    private void decrementPlayerActionCount(@NotNull ProxiedPlayer player, AutoModAction action) {
        playerActionMap.computeIfPresent(player.getUniqueId(), (id, actions) -> {
            actions.computeIfPresent(action, (act, count) -> count > 0 ? count - 1 : 0);
            return actions;
        });
    }

    /**
     * Finds if the given command is blocked.
     *
     * @param command The command to check if it is blocked.
     * @return True if the command is blocked, false otherwise.
     */
    private boolean findBlockedCommand(String command) {
        List<String> blockedCommands = BetterBanSystem.getInstance().getConfig().getStringList("mute.blocked-commands");
        Optional<String> b = blockedCommands.stream().filter(g -> g.equalsIgnoreCase(command) || g.equalsIgnoreCase("minecraft:" + command) || g.equalsIgnoreCase("bukkit:" + command)).findFirst();
        return b.isPresent();
    }
}
