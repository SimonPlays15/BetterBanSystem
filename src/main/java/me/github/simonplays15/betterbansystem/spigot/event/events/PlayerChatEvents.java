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

/**
 * This class handles player chat events and performs various actions based on the event.
 */
public class PlayerChatEvents implements Listener {

    /**
     * Represents a map that stores the spam count for each player identified by a unique UUID.
     * The key is the UUID of the player, and the value is an integer representing the number of times
     * the player has triggered a spam action.
     * <p>
     * This map is used in the PlayerChatEvents class to keep track of player spam counts and perform
     * automatic moderation actions based on the defined limits.
     */
    private final Map<UUID, Integer> playerSpamMap = new HashMap<>();
    /**
     * This private final field represents a map that stores duplicated texts associated with player UUIDs.
     * It is implemented as a HashMap, where the keys are UUIDs of players and the values are the duplicated texts.
     * This map is used to track and store duplicated texts sent by players for further processing or analysis.
     */
    private final Map<UUID, String> playerDuplicatedTextMap = new HashMap<>();
    /**
     * A mapping of player UUIDs to a map of AutoMod actions and their respective counts.
     * <p>
     * The playerActionMap is a private final field of type Map<UUID, Map<AutoModAction, Integer>> that stores the information
     * of player actions in the AutoMod system. Each player UUID is mapped to a sub-map that contains the AutoMod actions
     * and the number of times each action has been performed by that player.
     * <p>
     * The playerActionMap is initialized as an empty HashMap using the default constructor. It is private and final, indicating
     * that its reference cannot be changed once assigned and can only be accessed within the containing class.
     * <p>
     * Example usage:
     * // Initializing playerActionMap
     * private final Map<UUID, Map<AutoModAction, Integer>> playerActionMap = new HashMap<>();
     * <p>
     * // Accessing player action count
     * UUID playerId = UUID.fromString("01234567-89ab-cdef-0123-456789abcdef");
     * AutoModAction action = AutoModAction.SWEAR_FILTER;
     * int actionCount = playerActionMap.getOrDefault(playerId, new HashMap<>()).getOrDefault(action, 0);
     * <p>
     * // Updating player action count
     * playerActionMap.computeIfAbsent(playerId, k -> new HashMap<>()).merge(action, 1, Integer::sum);
     * <p>
     * // Removing player action count
     * playerActionMap.computeIfPresent(playerId, (k, v) -> {
     * v.remove(action);
     * return v.isEmpty() ? null : v;
     * });
     */
    private final Map<UUID, Map<AutoModAction, Integer>> playerActionMap = new HashMap<>();

    /**
     * The service variable is an instance of ScheduledExecutorService.
     * It is a private final field that is initialized with a new instance of ScheduledExecutorService created by the Executors.newScheduledThreadPool method.
     * <p>
     * This service is used for scheduling and executing tasks with a delay or at fixed intervals. It is a thread pool that can manage up to 10 threads.
     * <p>
     * The service variable is used within the PlayerChatEvents class and is not accessible outside of it. It is used for handling various player chat events in the game.
     */
    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(10);

    /**
     * Handles the onChat event.
     *
     * @param event The AsyncPlayerChatEvent that is triggered when a player chats.
     */
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
                }, 3, TimeUnit.SECONDS);
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
     * Executes actions specified in the configuration file for the given player chat event.
     *
     * @param event      the player chat event
     * @param player     the player who triggered the event
     * @param configPath the path to the configuration property that contains the actions to execute
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
     * Increments the action count for the specified player and performs a limit check.
     * If the player has exceeded the action limit, the given command is executed with the player's name
     * and command arguments.
     *
     * @param command    the command to execute
     * @param player     the player to check and execute the command for
     * @param action     the auto moderation action
     * @param parameters the auto moderation action parameters
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
     * Checks if the given player has exceeded the action limit for the specified AutoModAction.
     *
     * @param player The player to check.
     * @param action The AutoModAction to check against.
     * @return {@code true} if the player has exceeded the action limit, {@code false} otherwise.
     */
    private boolean hasPlayerExceededActionLimit(@NotNull Player player, @NotNull AutoModAction action) {

        if (action.getActionLimit() < 1)
            return true;

        Integer playerActionCount = playerActionMap.getOrDefault(player.getUniqueId(), Collections.emptyMap()).get(action);

        return playerActionCount != null && playerActionCount >= action.getActionLimit();
    }


    /**
     * Increments the action count for a player based on the specified action.
     *
     * @param player The player whose action count needs to be incremented. Cannot be null.
     * @param action The action for which the count needs to be incremented.
     */
    private void incrementPlayerActionCount(@NotNull Player player, AutoModAction action) {
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
    private void decrementPlayerActionCount(@NotNull Player player, AutoModAction action) {
        playerActionMap.computeIfPresent(player.getUniqueId(), (id, actions) -> {
            actions.computeIfPresent(action, (act, count) -> count > 0 ? count - 1 : 0);
            return actions;
        });
    }

    /**
     * Checks if a command is blocked based on a list of blocked commands.
     *
     * @param command the command to check
     * @return true if the command is blocked, false otherwise
     */
    private boolean findBlockedCommand(String command) {
        List<String> blockedCommands = BetterBanSystem.getInstance().getConfig().getStringList("mute.blocked-commands");
        Optional<String> b = blockedCommands.stream().filter(g -> g.equalsIgnoreCase(command) || g.equalsIgnoreCase("minecraft:" + command) || g.equalsIgnoreCase("bukkit:" + command)).findFirst();
        return b.isPresent();
    }

    /**
     * This method handles the PlayerCommandPreprocessEvent and checks if the player is muted.
     * If the player is muted and the command is in the blocked command list, the event is cancelled and a message is sent to the player.
     *
     * @param event The PlayerCommandPreprocessEvent that triggered the method.
     */
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
