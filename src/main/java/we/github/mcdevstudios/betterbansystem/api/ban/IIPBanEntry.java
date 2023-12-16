/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.ban;

import java.util.Date;

public interface IIPBanEntry {

    String bannedIP();

    String bannerName();

    Date creationDate();

    Date expirationDate();

    String reason();

    boolean isPermanent();

}
