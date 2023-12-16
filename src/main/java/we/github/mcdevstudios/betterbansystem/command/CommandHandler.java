/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.command.commands.KickCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler implements CommandExecutor {

    private final Map<String, BaseCommand> commands = new HashMap<>();

    public CommandHandler() {
        registerCommand(new KickCommand());
    }

    public Map<String, BaseCommand> getCommands() {
        return commands;
    }


    private void registerCommand(BaseCommand command) {
        if (commands.containsKey(command.getCommandName().toLowerCase()))
            return;

        Bukkit.getPluginCommand(command.getCommandName()).setExecutor(this);
        commands.put(command.getCommandName().toLowerCase(), command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (commands.containsKey(command.getName().toLowerCase())) {
            BaseCommand baseCommand = commands.get(command.getName().toLowerCase());
            baseCommand.setLabel(label);
            if (!baseCommand.testPermission(sender)) {
                sender.sendMessage(BetterBanSystem.getLanguageFile().getMessage("permissions_message"));
                BetterBanSystem.getGlobalLogger().debug(sender.getName(), "tried to execute command " + command.getName() + " but has no permissions");
                return false;
            }

            boolean commandSuccess = false;
            try {
                commandSuccess = baseCommand.execute(sender, args);
                BetterBanSystem.getGlobalLogger().debug(sender.getName(), "executed command", command.getName(), (commandSuccess ? "with success" : "but the command failed"), "| Arguments: ", args);
            } catch (Exception e) {
                BetterBanSystem.getGlobalLogger().error("Failed to execute command " + command.getName() + " by user " + sender.getName(), e);
                sender.sendMessage(BetterBanSystem.getPrefix() + "Failed to execute command. See log for more details.");
                return false;
            }
            if (!commandSuccess)
                sender.sendMessage(BetterBanSystem.getPrefix() + baseCommand.getUsage());

            return commandSuccess;
        } else {
            sender.sendMessage(BetterBanSystem.getPrefix() + "Sorry but we didn't found the command in our system §c\"" + label + "\"");
        }
        return false;
    }
}
