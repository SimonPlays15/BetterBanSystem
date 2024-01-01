/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.ban;

import java.util.Date;

public interface IIPBanEntry {

    String ip();

    String source();

    Date created();

    Object expires();

    String reason();

}
