/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.bungeecord.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.exceptions.CommandException;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.core.command.BaseCommand;
import we.github.mcdevstudios.betterbansystem.core.logging.GlobalLogger;
import we.github.mcdevstudios.betterbansystem.core.player.BaseCommandSender;
import we.github.mcdevstudios.betterbansystem.core.player.BungeeCordCommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class CommandHandler {
    private final Map<String, BaseCommand> commands = new HashMap<>();
    private final Plugin plugin;

    public CommandHandler(Plugin plugin) {
        this.plugin = plugin;

        // TODO
    }

    private void registerCommand(@NotNull BaseCommand command) {
        if (commands.containsKey(command.getCommandName())) return;
        plugin.getProxy().getPluginManager().registerCommand(plugin, new TabCompleterAndCommand(command.getCommandName(), command.getPermission(), String.valueOf(command.getAliases())) {

            @Override
            public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
                return command.onTabComplete(BungeeCordCommandSender.of(commandSender), strings);
            }

            @Override
            public void execute(CommandSender commandSender, String[] strings) {
                onCommand(commandSender, this, strings[0], strings);
            }

        });
        commands.put(command.getCommandName(), command);
        GlobalLogger.getLogger().debug("Registering Command:", command.getCommandName() + ":" + command.getPermission(), command.getDescription());
    }

    public Map<String, BaseCommand> getCommands() {
        return commands;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        GlobalLogger.getLogger().debug(sender.getName(), "executed command", command.getName() + "/" + label + "[" + String.join(", ", args) + "]");
        try {
            if (getCommands().containsKey(command.getName())) {
                BaseCommandSender commandSender = BungeeCordCommandSender.of(sender);
                BaseCommand baseCommand = getCommands().get(command.getName().toLowerCase());
                baseCommand.setLabel(label);
                if (!baseCommand.testPermission(commandSender)) {
                    commandSender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("permissions_message"));
                    GlobalLogger.getLogger().debug(sender.getName(), "tried to execute command " + command.getName() + " but has no permissions");
                    return true;
                }

                boolean commandSuccess;
                try {
                    commandSuccess = baseCommand.runCommand(commandSender, args);
                } catch (Throwable thr) {
                    commandSender.sendMessage("Failed to execute the command. Please see the console log for more informations");
                    GlobalLogger.getLogger().error("Failed to execute the command", sender.getName() + ":" + command.getName() + "/" + label + "[" + String.join(", ", args) + "]", thr);
                    return true;
                }

                if (!commandSuccess)
                    commandSender.sendMessage(baseCommand.getUsage());

                return true;
            }
        } catch (CommandException ex) {
            GlobalLogger.getLogger().log(Level.SEVERE, "Failed to execute command " + command.getName() + " by user " + sender.getName(), ex);
            sender.sendMessage(TextComponent.fromLegacyText(BetterBanSystem.getInstance().getPrefix() + "Failed to execute command. See log for more details."));
        }
        GlobalLogger.getLogger().log(Level.SEVERE, "Failed to execute command " + command.getName() + " by user " + sender.getName());
        return true;
    }

    public Iterable<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
        if (commands.containsKey(command.getName())) {
            BaseCommandSender commandSender = BungeeCordCommandSender.of(sender);
            BaseCommand baseCommand = getCommands().get(command.getName().toLowerCase());
            if (!baseCommand.testPermission(commandSender)) {
                return new ArrayList<>();
            }
            return baseCommand.onTabComplete(commandSender, args);
        }
        return null;
    }

    private abstract static class TabCompleterAndCommand extends Command implements TabExecutor {

        public TabCompleterAndCommand(String name) {
            super(name);
        }

        public TabCompleterAndCommand(String name, String permission, String... aliases) {
            super(name, permission, aliases);
        }
    }

}
