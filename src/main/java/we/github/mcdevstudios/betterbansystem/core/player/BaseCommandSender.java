/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.player;

import we.github.mcdevstudios.betterbansystem.core.command.CommandSenderType;

import java.util.UUID;

public abstract class BaseCommandSender {

    public BaseCommandSender() {
    }

    public abstract void sendMessage(String string);

    public abstract void sendMessage(String... strings);

    public abstract void sendMessage(UUID uuid, String string);

    public abstract void sendMessage(UUID uuid, String... strings);

    public abstract boolean hasPermission(String paramString);

    public abstract String getName();

    public abstract CommandSenderType getSenderType();

    public boolean isConsole() {
        return this.getSenderType() == CommandSenderType.CONSOLE;
    }

    public boolean isPlayer() {
        return this.getSenderType() == CommandSenderType.PLAYER;
    }

    public boolean isOther() {
        return this.getSenderType() == CommandSenderType.OTHER;
    }

    public boolean isBlock() {
        return this.getSenderType() == CommandSenderType.BLOCK;
    }

}
