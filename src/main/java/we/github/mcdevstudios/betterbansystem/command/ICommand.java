/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public interface ICommand extends CommandExecutor {

    String getPermission();

    @Override
    boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings);


}
