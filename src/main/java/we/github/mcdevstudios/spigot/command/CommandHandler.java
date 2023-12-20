/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.spigot.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.spigot.BetterBanSystem;
import we.github.mcdevstudios.spigot.command.commands.KickCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        BetterBanSystem.getGlobalLogger().debug("Registering following command:", command.getCommandName(), "| Description:", command.getDescription(), "| Usage:", command.getUsage(), "| Needed permission:", command.getPermission());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        BetterBanSystem.getGlobalLogger().debug(sender.getName(), "executed command", command.getName(), "/", label);
        try {
            if (getCommands().containsKey(command.getName().toLowerCase())) {
                BaseCommand baseCommand = getCommands().get(command.getName().toLowerCase());
                baseCommand.setLabel(label);
                if (!baseCommand.testPermission(sender)) {
                    sender.sendMessage(BetterBanSystem.getLanguageFile().getMessage("permissions_message"));
                    BetterBanSystem.getGlobalLogger().debug(sender.getName(), "tried to execute command " + command.getName() + " but has no permissions");
                    return false;
                }

                boolean commandSuccess = baseCommand.execute(sender, args);
                BetterBanSystem.getGlobalLogger().debug(sender.getName(), "executed command", command.getName(), (commandSuccess ? "with success" : "but the command failed"), "| Arguments: ", args);

                if (!commandSuccess)
                    sender.sendMessage(BetterBanSystem.getPrefix() + baseCommand.getUsage());

                return commandSuccess;
            } else {
                sender.sendMessage(BetterBanSystem.getPrefix() + "Sorry but we didn't found the command in our system §c\"" + label + "\"");
            }
        } catch (Exception ex) {
            BetterBanSystem.getGlobalLogger().error("Failed to execute command " + command.getName() + " by user " + sender.getName(), ex);
            sender.sendMessage(BetterBanSystem.getPrefix() + "Failed to execute command. See log for more details.");
        }
        return false;
    }
}