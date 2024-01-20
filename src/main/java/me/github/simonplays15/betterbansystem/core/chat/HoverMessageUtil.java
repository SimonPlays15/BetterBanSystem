package me.github.simonplays15.betterbansystem.core.chat;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * The type Hover message util.
 */
public class HoverMessageUtil {

    /**
     * Sends a hoverable message to the specified player.
     *
     * @param player       the player to send the message to
     * @param message      the main message to be displayed
     * @param hoverMessage the message to be displayed when hovering over the main message
     * @deprecated This method is deprecated and should not be used. Use the overloaded methods instead.
     */
    @Deprecated
    public static void sendHoverableMessage(Object player, String message, String hoverMessage) {
        BaseComponent[] hoverText = new ComponentBuilder(hoverMessage).create();
        BaseComponent[] sendMessage = new ComponentBuilder(message).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)).create();

        if (player instanceof org.bukkit.entity.Player a) {
            a.spigot().sendMessage(sendMessage);
        } else if (player instanceof net.md_5.bungee.api.connection.ProxiedPlayer a) {
            a.sendMessage(sendMessage);
        } else {
            GlobalLogger.getLogger().error(new IllegalArgumentException("Unknown instance of the player object -> " + player.getClass()));
        }
    }

    /**
     * Sends a hoverable message to a player.
     *
     * @param player     the player to send the message to
     * @param components the components of the message
     */
    public static void sendHoverableMessage(Object player, BaseComponent[] components) {
        if (player instanceof org.bukkit.entity.Player a) {
            a.spigot().sendMessage(components);
        } else if (player instanceof net.md_5.bungee.api.connection.ProxiedPlayer a) {
            a.sendMessage(components);
        }
    }

    /**
     * Sends a hoverable message to a player or proxied player.
     *
     * @param player    the player or proxied player to send the message to
     * @param component the text components to send as the message
     */
    public static void sendHoverableMessage(Object player, TextComponent... component) {
        if (player instanceof org.bukkit.entity.Player a) {
            a.spigot().sendMessage(component);
        } else if (player instanceof net.md_5.bungee.api.connection.ProxiedPlayer a) {
            a.sendMessage(component);
        }
    }


    /**
     * Sends a hoverable message to a player.
     *
     * @param player    the player to send the message to
     * @param component the text component to send
     */
    public static void sendHoverableMessage(Object player, TextComponent component) {
        if (player instanceof org.bukkit.entity.Player a) {
            a.spigot().sendMessage(component);
        } else if (player instanceof net.md_5.bungee.api.connection.ProxiedPlayer a) {
            a.sendMessage(component);
        }
    }

    /**
     * Builds a hoverable message with the specified message and hover text.
     *
     * @param message      The main message to display.
     * @param hoverMessage The hover text to display when the message is hovered.
     * @return The hoverable message as an array of BaseComponents.
     * @deprecated This method is deprecated. Use {@link HoverMessageUtil#sendHoverableMessage} instead.
     */
    @Deprecated
    public static BaseComponent[] buildHoverableMessage(String message, String hoverMessage) {
        BaseComponent[] hoverText = new ComponentBuilder(hoverMessage).create();
        return new ComponentBuilder(message).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)).create();
    }
}
