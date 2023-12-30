/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.ban;

import java.util.Date;

public interface IIPBanEntry {

    String ip();

    String source();

    Date created();

    Object expires();

    String reason();

}
