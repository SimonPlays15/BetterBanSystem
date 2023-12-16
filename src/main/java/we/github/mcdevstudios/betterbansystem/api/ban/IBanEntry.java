/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.ban;

import java.util.Date;
import java.util.UUID;

public interface IBanEntry {

    UUID bannedPlayerUUID();

    String bannedPlayerName();

    String banningPlayerName();

    Date creationDate();

    Date expirationDate();

    String reason();

    boolean isPermanent();

}
