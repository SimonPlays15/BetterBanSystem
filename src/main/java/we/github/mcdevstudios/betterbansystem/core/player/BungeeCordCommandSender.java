/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.player;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.core.command.CommandSenderType;

import java.util.Collection;
import java.util.UUID;

public class BungeeCordCommandSender extends BaseCommandSender implements CommandSender {

    private final CommandSender base;
    private final CommandSenderType type;

    public BungeeCordCommandSender(CommandSender sender) {
        this.base = sender;
        if (sender instanceof ProxiedPlayer)
            this.type = CommandSenderType.PLAYER;
        else
            this.type = CommandSenderType.CONSOLE;
    }

    public static @NotNull BungeeCordCommandSender of(Object sender) {
        if (sender instanceof CommandSender)
            return new BungeeCordCommandSender((CommandSender) sender);

        throw new IllegalArgumentException("Unsupported sender type: " + sender.getClass().getName());
    }

    /**
     * Sends multiple messages to the command sender.
     * Each message is prepended with the prefix defined in the BetterBanSystem instance.
     *
     * @param strings The messages to be sent.
     */
    @Override
    public void sendMessages(String @NotNull ... strings) {
        for (String string : strings) {
            this.base.sendMessage(TextComponent.fromLegacyText(BetterBanSystem.getInstance().getPrefix() + string));
        }
    }

    /**
     * Sends one or more BaseComponents as a message.
     *
     * @param baseComponents the BaseComponents to send as a message
     */
    @Override
    public void sendMessage(BaseComponent... baseComponents) {
        for (BaseComponent baseComponent : baseComponents) {
            this.sendMessage(baseComponent);
        }
    }

    /**
     * Sends a message to the command sender with the given BaseComponent.
     *
     * @param baseComponent the BaseComponent to send as a message
     */
    @Override
    public void sendMessage(BaseComponent baseComponent) {
        TextComponent a = new TextComponent();
        a.setText(BetterBanSystem.getInstance().getPrefix() + baseComponent.toLegacyText());
        this.base.sendMessage(a);
    }

    /**
     * Retrieves the groups associated with this command sender.
     *
     * @return a collection of group names
     */
    @Override
    public Collection<String> getGroups() {
        return this.base.getGroups();
    }

    /**
     * Adds groups to the CommandSender.
     *
     * @param strings The groups to add. Accepts varargs.
     */
    @Override
    public void addGroups(String... strings) {
        this.base.addGroups(strings);
    }

    /**
     * Removes the specified groups from the command sender.
     *
     * @param strings The groups to be removed.
     */
    @Override
    public void removeGroups(String... strings) {
        this.base.removeGroups(strings);
    }

    /**
     * Sets the permission for the given string with the specified boolean value.
     *
     * @param s the permission to be set
     * @param b the value of the permission (true for granting the permission, false for denying it)
     */
    @Override
    public void setPermission(String s, boolean b) {
        this.base.setPermission(s, b);
    }

    /**
     * Retrieves the permissions associated with the command sender.
     *
     * @return a collection of strings representing the permissions of the command sender
     */
    @Override
    public Collection<String> getPermissions() {
        return this.base.getPermissions();
    }

    /**
     * Sends a message to the command sender.
     *
     * @param string the message to send
     */
    @Override
    public void sendMessage(String string) {
        this.base.sendMessage(new TextComponent(string));
    }

    /**
     * Sends a message to the command sender.
     *
     * @param strings the messages to send
     */
    @Override
    public void sendMessage(String... strings) {
        this.sendMessages(strings);
    }

    /**
     * Sends a message to the specified UUID.
     *
     * @param uuid   the UUID of the recipient
     * @param string the message to send
     */
    @Override
    public void sendMessage(UUID uuid, String string) {
        throw new NotImplementedException("Not supported inside the BungeeCord API");
    }

    /**
     * Sends a message to the specified UUID.
     *
     * @param uuid    the UUID of the recipient
     * @param strings the strings to send as the message
     * @throws NotImplementedException if the method is called within the BungeeCord API
     */
    @Override
    public void sendMessage(UUID uuid, String... strings) {
        throw new NotImplementedException("Not supported inside the BungeeCord API");
    }

    /**
     * Checks if the command sender has a specific permission.
     *
     * @param paramString the permission to check
     * @return true if the command sender has the permission, false otherwise
     */
    @Override
    public boolean hasPermission(String paramString) {
        return this.base.hasPermission(paramString);
    }

    /**
     * Retrieves the name of the command sender.
     *
     * @return The name of the command sender.
     */
    @Override
    public String getName() {
        return this.base.getName();
    }

    /**
     * Returns the type of the command sender.
     *
     * @return the type of the command sender
     */
    @Override
    public CommandSenderType getSenderType() {
        return this.type;
    }
}
