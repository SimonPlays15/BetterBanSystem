package me.github.simonplays15.betterbansystem.spigot;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.api.exceptions.CommandException;
import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.command.BaseCommand;
import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import me.github.simonplays15.betterbansystem.core.player.SpigotCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

/**
 * The CommandHandler class handles the execution of commands and tab completions.
 */
public class CommandHandler implements CommandExecutor, TabCompleter {

    /**
     * The CommandHandler class handles the registration of commands and sets default command handlers for unregistered commands.
     */
    public CommandHandler() {
        registerCommands();
        for (String s : BetterBanSystem.getInstance().getPluginDescription().getCommands().keySet()) {
            PluginCommand g = Bukkit.getPluginCommand(s);
            if (g != null && g.getExecutor() != this) {
                GlobalLogger.getLogger().warn("Command " + s + " has no default command handler. Setting to default Bukkit Command Executor");
                g.setExecutor((commandSender, command, s1, strings) -> {
                    commandSender.sendMessage(BetterBanSystem.getInstance().getPrefix() + "Sorry but this command is not fully implemented yet.");
                    return true;
                });
            }
        }
    }

    /**
     * Registers all commands from the CommandHandler into the Bukkit plugin command registry,
     * allowing them to be executed by players.
     */
    private void registerCommands() {
        for (BaseCommand value : BetterBanSystem.getInstance().getCommandHandler().getCommands().values()) {
            Objects.requireNonNull(Bukkit.getPluginCommand(value.getCommandName())).setExecutor(this);
        }
    }

    /**
     * Executes the command when it is called by a sender.
     *
     * @param sender  the command sender
     * @param command the command that is being executed
     * @param label   the command label
     * @param args    the command arguments
     * @return true if the command was successfully executed, false otherwise
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        GlobalLogger.getLogger().debug(sender.getName(), "executed command", command.getName() + "/" + label + "[" + String.join(", ", args) + "]");
        try {
            if (BetterBanSystem.getInstance().getCommandHandler().getCommands().containsKey(command.getName())) {
                SpigotCommandSender commandSender = SpigotCommandSender.of(sender);
                BaseCommand baseCommand = BetterBanSystem.getInstance().getCommandHandler().getCommands().get(command.getName().toLowerCase());
                baseCommand.setLabel(label);
                if (sender instanceof Player && !baseCommand.testPermission(commandSender)) {
                    commandSender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("defaults.permissionsMessage"));
                    GlobalLogger.getLogger().debug(sender.getName(), "tried to execute command " + command.getName() + " but has no permissions");
                    return true;
                }

                boolean commandSuccess;
                try {
                    commandSuccess = baseCommand.runCommand(commandSender, args);
                } catch (CommandException thr) {
                    commandSender.sendMessage("Failed to execute the command. Please see the console log for more informations");
                    GlobalLogger.getLogger().error("Failed to execute the command", sender.getName() + ":" + command.getName() + "/" + label + "[" + String.join(", ", args) + "]", thr);
                    return true;
                }

                if (!commandSuccess)
                    commandSender.sendMessage(baseCommand.getUsage());

                return true;
            }
        } catch (Throwable ex) {
            GlobalLogger.getLogger().log(Level.SEVERE, "Failed to execute command " + command.getName() + " by user " + sender.getName(), ex);
            sender.sendMessage(BetterBanSystem.getInstance().getPrefix() + "Failed to execute command. See log for more details.");
            ex.printStackTrace(System.err);
            return true;
        }

        GlobalLogger.getLogger().log(Level.SEVERE, "Failed to execute command " + command.getName() + " by user " + sender.getName(), new CommandException("Command is not registered inside the CommandHandler"));
        sender.sendMessage(BetterBanSystem.getInstance().getPrefix() + "§4Command is not registered inside the default CommandHandler. See the console log for more informations and provide the error please to the Developers.");
        return true;
    }

    /**
     * Provides tab completion for a command.
     *
     * @param sender  The CommandSender who is performing the tab completion.
     * @param command The command being tab completed.
     * @param label   The alias or label used to invoke the command.
     * @param args    The arguments provided for the command.
     * @return A List of tab completion options for the command arguments, or null if tab completion is not available for the command.
     */
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (BetterBanSystem.getInstance().getCommandHandler().getCommands().containsKey(command.getName())) {
            SpigotCommandSender commandSender = SpigotCommandSender.of(sender);
            BaseCommand baseCommand = BetterBanSystem.getInstance().getCommandHandler().getCommands().get(command.getName().toLowerCase());
            baseCommand.setLabel(label);
            if (sender instanceof Player && !baseCommand.testPermission(commandSender, baseCommand.getPermission())) {
                return new ArrayList<>();
            }
            return baseCommand.onTabComplete(commandSender, args);
        }
        return null;
    }
}