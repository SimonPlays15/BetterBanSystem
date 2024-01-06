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

public class CommandHandler {
    private final Plugin plugin;

    public CommandHandler(Plugin plugin) {
        this.plugin = plugin;
        this.registerCommands();
    }

    private void registerCommands() {
        for (BaseCommand command : BetterBanSystem.getInstance().getCommandHandler().getCommands().values()) {
            plugin.getProxy().getPluginManager().registerCommand(plugin, new TabCompleterAndCommand(command.getCommandName(), command.getPermission(), String.valueOf(command.getAliases())) {

                @Override
                public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
                    BungeeCordCommandSender sender = BungeeCordCommandSender.of(commandSender);

                    if (sender.isPlayer() && !command.testPermission(sender, command.getPermission()))
                        return new ArrayList<>();

                    return command.onTabComplete(BungeeCordCommandSender.of(commandSender), strings);
                }

                @Override
                public void execute(CommandSender commandSender, String[] strings) {
                    if (onCommand(commandSender, this, this.getName(), strings)) {
                        GlobalLogger.getLogger().trace(commandSender.getName(), "run command", this.getName() + "/" + this.getName() + "[" + String.join(", ", strings) + "]", "successfully");
                    }
                }

            });
        }
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
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
        }
        GlobalLogger.getLogger().log(Level.SEVERE, "Failed to execute command " + command.getName() + " by user " + sender.getName());
        return true;
    }

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

    private abstract static class TabCompleterAndCommand extends Command implements TabExecutor {

        public TabCompleterAndCommand(String name, String permission, String... aliases) {
            super(name, permission, aliases);
        }
    }

}
