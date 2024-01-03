/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.chat;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import we.github.mcdevstudios.betterbansystem.core.logging.GlobalLogger;

public class HoverMessageUtil {

    /**
     * Sends a hoverable message to the specified player.
     *
     * @param player       the player to send the message to (has to be an instance of {@link org.bukkit.entity.Player} or {@link net.md_5.bungee.api.connection.ProxiedPlayer})
     * @param message      the main message text
     * @param hoverMessage the text to display when hovering over the main message
     * @apiNote {@link HoverEvent#HoverEvent(HoverEvent.Action, BaseComponent[])} is deprecated and is removed in future releases of the BungeeCord API. (Tested on 1.20.2)
     * @deprecated {@code BaseComponent[] sendMessage = new ComponentBuilder(message).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)).create();}
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

}
