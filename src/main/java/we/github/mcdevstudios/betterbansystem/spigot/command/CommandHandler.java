/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.spigot.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.command.BaseCommand;
import we.github.mcdevstudios.betterbansystem.api.logging.GlobalLogger;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.spigot.command.commands.KickCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public class CommandHandler implements CommandExecutor {

    private final Map<String, BaseCommand> commands = new HashMap<>();

    public CommandHandler() {
        registerCommand(new KickCommand());
    }

    public Map<String, BaseCommand> getCommands() {
        return commands;
    }


    private void registerCommand(@NotNull BaseCommand command) {
        if (commands.containsKey(command.getCommandName().toLowerCase()))
            return;

        Objects.requireNonNull(Bukkit.getPluginCommand(command.getCommandName())).setExecutor(this);
        commands.put(command.getCommandName().toLowerCase(), command);
        GlobalLogger.getLogger().debug("Registering following command:", command.getCommandName(), "| Description:", command.getDescription(), "| Usage:", command.getUsage(), "| Needed permission:", command.getPermission());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        GlobalLogger.getLogger().debug(sender.getName(), "executed command", command.getName(), "/", label);
        try {
            if (getCommands().containsKey(command.getName().toLowerCase())) {
                SpigotCommandSender commandSender = SpigotCommandSender.of(sender);
                BaseCommand baseCommand = getCommands().get(command.getName().toLowerCase());
                baseCommand.setLabel(label);
                if (sender instanceof Player && !baseCommand.testPermission(commandSender)) {
                    sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("permissions_message"));
                    GlobalLogger.getLogger().debug(sender.getName(), "tried to execute command " + command.getName() + " but has no permissions");
                    return true;
                }

                boolean commandSuccess = baseCommand.runCommand(commandSender, args);
                GlobalLogger.getLogger().debug(sender.getName(), "executed command", command.getName(), (commandSuccess ? "with success" : "but the command failed"), "| Arguments: ", args);

                if (!commandSuccess)
                    sender.sendMessage(BetterBanSystem.getInstance().getPrefix() + baseCommand.getUsage());

                return commandSuccess;
            } else {
                sender.sendMessage(BetterBanSystem.getInstance().getPrefix() + "Sorry but we didn't found the command in our system §c\"" + label + "\"");
            }
        } catch (Exception ex) {
            GlobalLogger.getLogger().log(Level.SEVERE, "Failed to execute command " + command.getName() + " by user " + sender.getName(), ex);
            sender.sendMessage(BetterBanSystem.getInstance().getPrefix() + "Failed to execute command. See log for more details.");
        }

        GlobalLogger.getLogger().log(Level.SEVERE, "Failed to execute command " + command.getName() + " by user " + sender.getName());
        return true;
    }


}