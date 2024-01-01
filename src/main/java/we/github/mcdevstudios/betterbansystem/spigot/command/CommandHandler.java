/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.spigot.command;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import we.github.mcdevstudios.betterbansystem.api.exceptions.CommandException;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.core.command.BaseCommand;
import we.github.mcdevstudios.betterbansystem.core.logging.GlobalLogger;
import we.github.mcdevstudios.betterbansystem.core.player.SpigotCommandSender;
import we.github.mcdevstudios.betterbansystem.spigot.command.commands.*;

import java.util.*;
import java.util.logging.Level;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final Map<String, BaseCommand> commands = new HashMap<>();

    public CommandHandler() {
        // TODO
        registerCommand(new KickCommand());
        registerCommand(new BanListCommand());
        registerCommand(new BanCommand());
        registerCommand(new UnbanIpCommand());
        registerCommand(new UnbanCommand());
        registerCommand(new LookUpCommand());
        registerCommand(new IpBanCommand());
        registerCommand(new TimeBanCommand());
        registerCommand(new WarnCommand());
        registerCommand(new DelWarnCommand());

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

    private void registerCommand(@NotNull BaseCommand command) {
        if (commands.containsKey(command.getCommandName()))
            return;

        Objects.requireNonNull(Bukkit.getPluginCommand(command.getCommandName())).setExecutor(this);
        commands.put(command.getCommandName(), command);
        GlobalLogger.getLogger().debug("Registering Command:", command.getCommandName() + ":" + command.getPermission(), command.getDescription());
    }

    public Map<String, BaseCommand> getCommands() {
        return commands;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        GlobalLogger.getLogger().debug(sender.getName(), "executed command", command.getName() + "/" + label + "[" + String.join(", ", args) + "]");
        try {
            if (getCommands().containsKey(command.getName())) {
                SpigotCommandSender commandSender = new SpigotCommandSender(sender);
                BaseCommand baseCommand = getCommands().get(command.getName().toLowerCase());
                baseCommand.setLabel(label);
                if (sender instanceof Player && !baseCommand.testPermission(commandSender)) {
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
            sender.sendMessage(BetterBanSystem.getInstance().getPrefix() + "Failed to execute command. See log for more details.");
            return true;
        }

        GlobalLogger.getLogger().log(Level.SEVERE, "Failed to execute command " + command.getName() + " by user " + sender.getName());
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (commands.containsKey(command.getName())) {
            SpigotCommandSender commandSender = new SpigotCommandSender(sender);
            BaseCommand baseCommand = getCommands().get(command.getName().toLowerCase());
            baseCommand.setLabel(label);
            if (sender instanceof Player && !baseCommand.testPermission(commandSender, baseCommand.getPermission())) {
                return new ArrayList<>();
            }
            return baseCommand.onTabComplete(commandSender, args);
        }
        return null;
    }
}