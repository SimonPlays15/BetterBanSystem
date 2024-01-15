package me.github.simonplays15.betterbansystem.bungeecord.event.events;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.automod.AutoModAction;
import me.github.simonplays15.betterbansystem.core.automod.AutoModActionParameters;
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
import java.util.stream.Collectors;

public class ChatEvents implements Listener {

    private final Map<UUID, Integer> playerSpamMap = new HashMap<>();
    private final Map<UUID, String> playerDuplicatedTextMap = new HashMap<>();
    private final Map<UUID, Map<AutoModAction, Integer>> playerActionMap = new HashMap<>();

    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(10);

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
            }, 2, TimeUnit.SECONDS);
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
     * Executes the actions specified in the configuration for a chat event.
     *
     * @param event      The chat event that triggered the execution of actions.
     * @param player     The player associated with the event.
     * @param configPath The path to the configuration entry containing the actions to execute.
     */
    private void executeActionsFromConfig(@NotNull ChatEvent event, ProxiedPlayer player, String configPath) {
        AutoModAction[] actions = AutoModAction.parseActions(BetterBanSystem.getInstance().getConfig().getString(configPath, "DELETE"));
        for (AutoModAction action : actions) {
            switch (action.getType()) {
                case WARN -> processActionWithLimitCheck("warn", player, action, AutoModActionParameters.REASON);
                case DELETE -> event.setCancelled(true);
                case MUTE ->
                        processActionWithLimitCheck("mute", player, action, AutoModActionParameters.DURATION, AutoModActionParameters.REASON);
            }
        }
    }

    /**
     * Checks the given message for profanity words and replaces them with asterisks.
     *
     * @param message The message to check and replace.
     * @param words   The list of profanity words to search for.
     * @return The message with profanity words replaced by asterisks.
     */
    private String checkAndReplaceProfanityWords(String message, @NotNull List<String> words) {
        for (String word : words) {
            if (message.toLowerCase().contains(word.toLowerCase())) {
                char[] stars = new char[word.length()];
                Arrays.fill(stars, '*');
                message = message.replace(word, new String(stars));
            }
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
     * Checks if a player has exceeded the action limit for a specific AutoMod action.
     *
     * @param player The player to check for.
     * @param action The AutoMod action to check against.
     * @return True if the player has exceeded the action limit, false otherwise.
     */
    private boolean hasProxiedPlayerExceededActionLimit(@NotNull ProxiedPlayer player, @NotNull AutoModAction action) {

        if (action.getActionLimit() <= 0)
            return true;

        return playerActionMap.containsKey(player.getUniqueId())
                && playerActionMap.get(player.getUniqueId()).get(action) >= action.getActionLimit();
    }


    /**
     * This method increments the action count for a player in the playerActionMap.
     *
     * @param player The player to increment the action count for.
     * @param action The AutoModAction to increment the count of.
     */
    private void incrementProxiedPlayerActionCount(@NotNull ProxiedPlayer player, AutoModAction action) {
        playerActionMap.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .compute(action, (act, count) -> count == null ? 1 : count + 1);
    }

    private boolean findBlockedCommand(String command) {
        List<String> blockedCommands = BetterBanSystem.getInstance().getConfig().getStringList("mute.blocked-commands");
        Optional<String> b = blockedCommands.stream().filter(g -> g.equalsIgnoreCase(command) || g.equalsIgnoreCase("minecraft:" + command) || g.equalsIgnoreCase("bukkit:" + command)).findFirst();
        return b.isPresent();
    }
}
