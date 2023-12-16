/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.ban;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class BanHandler {

    public BanHandler() {
    }

    public static void addBan(@NotNull CommandSender sender, @NotNull OfflinePlayer target, String reason, Date expires) {
        boolean isPermanent = expires == null;
        IBanEntry banEntry = new BanEntry(target.getUniqueId(), target.getName(), sender.getName(), new Date(), (isPermanent ? new Date(Long.MAX_VALUE) : expires), reason, isPermanent);
        BanEntry.saveToJson(banEntry, "banned-players.json");
    }

    public static void addIpBann(@NotNull CommandSender sender, String ipAddress, String reason, Date expires) {
        boolean isPermanent = expires == null;
        IIPBanEntry entry = new IPBanEntry(ipAddress, sender.getName(), new Date(), expires, reason, isPermanent);
        IPBanEntry.saveToJson(entry, "banned-ips.json");
    }

}
