/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.spigot.event.events.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.core.mute.MuteHandler;

import java.util.List;
import java.util.Optional;

public class PlayerChatEvents implements Listener {

    @EventHandler
    public void onChat(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (MuteHandler.findMuteEntry(player.getUniqueId()) != null) {
            event.setCancelled(true);
            player.sendMessage(BetterBanSystem.getInstance().getPrefix() + "§4You cannot chat while you're muted.");
        }

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
