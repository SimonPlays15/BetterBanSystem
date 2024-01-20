package me.github.simonplays15.betterbansystem.bungeecord;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.api.exceptions.CommandException;
import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.command.BaseCommand;
import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import me.github.simonplays15.betterbansystem.core.player.BaseCommandSender;
import me.github.simonplays15.betterbansystem.core.player.BungeeCordCommandSender;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 * CommandHandler is responsible for handling commands in the plugin.
 */
public class CommandHandler {
    /**
     * Represents a plugin instance.
     */
    private final Plugin plugin;

    /**
     * The CommandHandler class handles the registration of commands for the plugin.
     */
    public CommandHandler(Plugin plugin) {
        this.plugin = plugin;
        this.registerCommands();
    }

    /**
     * Registers commands from the CommandHandler to the BungeeCord proxy plugin manager.
     */
    private void registerCommands() {
        for (BaseCommand command : BetterBanSystem.getInstance().getCommandHandler().getCommands().values()) {
            String[] aliases = new String[command.getAliases().size()];
            for (int i = 0; i < command.getAliases().size(); i++) {
                aliases[i] = "global" + command.getAliases().get(i);
            }
            plugin.getProxy().getPluginManager().registerCommand(plugin, new TabCompleterAndCommand(command, aliases) {

                @Override
                public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
                    BungeeCordCommandSender sender = BungeeCordCommandSender.of(commandSender);

                    if (sender.isPlayer() && !command.testPermission(sender, command.getPermission()))
                        return new ArrayList<>();

                    return command.onTabComplete(BungeeCordCommandSender.of(commandSender), strings);
                }

                @Override
                public void execute(CommandSender commandSender, String[] strings) {
                    onCommand(commandSender, this, this.getName(), strings);
                }

            });
        }
    }

    /**
     * Executes a command.
     *
     * @param sender  the command sender
     * @param command the command being executed
     * @param label   the command label
     * @param args    the command arguments
     * @return true if the command executed successfully, false otherwise
     */
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws CommandException {
        GlobalLogger.getLogger().debug(sender.getName(), "executed command", command.getName() + "/" + label + "[" + String.join(", ", args) + "]");
        try {
            if (BetterBanSystem.getInstance().getCommandHandler().getCommands().containsKey(command.getName())) {
                BaseCommandSender commandSender = BungeeCordCommandSender.of(sender);
                BaseCommand baseCommand = BetterBanSystem.getInstance().getCommandHandler().getCommands().get(command.getName().toLowerCase());
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
                    thr.printStackTrace(System.err);
                    return true;
                }

                if (!commandSuccess)
                    commandSender.sendMessage(baseCommand.getUsage());

                return true;
            }
        } catch (CommandException ex) {
            GlobalLogger.getLogger().log(Level.SEVERE, "Failed to execute command " + command.getName() + " by user " + sender.getName(), ex);
            sender.sendMessage(TextComponent.fromLegacyText(BetterBanSystem.getInstance().getPrefix() + "Failed to execute command. See log for more details."));
            ex.printStackTrace(System.err);
        }
        return true;
    }

    /**
     * Retrieves a list of tab completion options for a given command.
     *
     * @param sender  The command sender who initiated the tab completion.
     * @param command The command being executed.
     * @param args    The arguments provided after the command.
     * @return An Iterable containing possible tab completion options.
     */
    public Iterable<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
        if (BetterBanSystem.getInstance().getCommandHandler().getCommands().containsKey(command.getName())) {
            BaseCommandSender commandSender = BungeeCordCommandSender.of(sender);
            BaseCommand baseCommand = BetterBanSystem.getInstance().getCommandHandler().getCommands().get(command.getName().toLowerCase());
            if (!baseCommand.testPermission(commandSender)) {
                return new ArrayList<>();
            }
            return baseCommand.onTabComplete(commandSender, args);
        }
        return null;
    }

    /**
     * TabCompleterAndCommand is a class that extends the Command class and implements the TabExecutor interface.
     * It combines the functionality of both a command and a tab completer.
     */
    private abstract static class TabCompleterAndCommand extends Command implements TabExecutor {

        /**
         * The private final variable {@code command} holds an instance of the {@link BaseCommand} class.
         */
        private final BaseCommand command;

        /**
         * TabCompleterAndCommand is a class that extends the Command class and implements the TabExecutor interface.
         * It combines the functionality of both a command and a tab completer.
         */
        public TabCompleterAndCommand(@NotNull BaseCommand command, String[] aliases) {
            super(command.getCommandName(), command.getPermission(), aliases);
            this.command = command;
        }

        /**
         * Checks if the provided CommandSender has the permission to execute this command.
         *
         * @param sender The CommandSender to check the permission for.
         * @return True if the sender has the permission, false otherwise.
         */
        @Override
        public boolean hasPermission(CommandSender sender) {
            return command.testPermission(BungeeCordCommandSender.of(sender), this.command.getPermission());
        }
    }

}
