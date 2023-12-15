/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.utils;

import org.bukkit.entity.Player;

public class PermissionsHelper {

    public PermissionsHelper() {

    }

    public boolean hasPermission(Player player, String permString) {
        return player.isOp() || player.hasPermission(permString);
    }

    public boolean canAdministratePlayer(Player executor, Player target) {

        if (executor.hasPermission("betterbansystem.admin.administrator"))
            return true;
        return !target.hasPermission("betterbansystem.admin.excemptAll");
    }


}
